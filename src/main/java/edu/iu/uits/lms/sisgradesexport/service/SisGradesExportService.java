package edu.iu.uits.lms.sisgradesexport.service;

import canvas.client.generated.api.CoursesApi;
import canvas.client.generated.api.SectionsApi;
import canvas.client.generated.model.Course;
import canvas.client.generated.model.Enrollment;
import canvas.client.generated.model.Grades;
import canvas.client.generated.model.Section;
import edu.iu.uits.lms.sisgradesexport.model.CsvResponse;
import edu.iu.uits.lms.sisgradesexport.model.SisGrade;
import edu.iu.uits.lms.sisgradesexport.model.SisInfo;
import edu.iu.uits.lms.sisgradesexport.model.SisUserGrades;
import iuonly.client.generated.api.SudsApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SisGradesExportService {
    @Autowired
    private CoursesApi coursesApi;

    @Autowired
    private SectionsApi sectionsApi;

    @Autowired
    private SudsApi sudsApi;

    @Autowired
    private SisGradeAuditService sisGradeAuditService;

    /**
     * Used for the csv download from the gradebook tool
     * @param courseId
     * @return
     */
    public CsvResponse exportGradesForSis(String courseId) {
        final String gradeType = "FIN";
        final Date currDate = new Date();

        List<Object[]> data = new ArrayList<Object[]>();
        List<String> columns = Arrays.asList(new String[]{gradeType});
        log.debug("Preparing to write out grades to csv file for course {}...", courseId);

        List<Enrollment> enrollments = coursesApi.getStudentCourseEnrollment(courseId);

        for (Enrollment enrollment : enrollments) {
            Grades grades = overrideCurrentAndFinalGrades(enrollment.getGrades());
            data.add(new String[]{enrollment.getUser().getSisUserId(), grades.getCurrentGrade()});
        }

        SimpleDateFormat dateFormatForFilename = new SimpleDateFormat("yyyy-MM-dd");

        Course course = coursesApi.getCourse(courseId);

        String fileName = "sis_grade_export_" + course.getSisCourseId() + "_" + dateFormatForFilename.format(currDate) + ".csv";

        return new CsvResponse(data, columns, fileName, false);
    }

    /**
     * Used by SIS to pull the grades into peoplesoft
     * @param sisSectionId
     * @return
     */
    public ResponseEntity getInfoForSisFromSisSectionId(String sisSectionId) {
        log.debug("getInfoForSisFromSisSectionId({})", sisSectionId);
        if (sisSectionId == null || "".equals(sisSectionId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Enrollment> enrollments;

        int iuoccCourseCount = sudsApi.getIuoccCourseCount(sisSectionId);
        if (iuoccCourseCount > 0) {
            Section section = sectionsApi.getSection("sis_section_id:" + sisSectionId);
            enrollments = coursesApi.getStudentCourseEnrollment(section.getCourseId());
        } else {
            enrollments = sectionsApi.getStudentSectionEnrollments(sisSectionId);
        }
        List<SisUserGrades> sisUserGradesList = new ArrayList<>();

        String finalScore = "";
        String currentScore = "";
        String finalGrade = "";

        String ungradedWarning = "";
        String noGradeSchemeWarning = "WARNING from Canvas: The grades for this course can not be loaded into SIS until you select a course grading scheme in Canvas. For instructions on how to enable a course grading scheme, see: https://community.canvaslms.com/docs/DOC-2924";

        boolean keepLookingForWarnings = true;
        for(Enrollment enrollment: enrollments) {

            Grades grades = enrollment.getGrades();
            grades = overrideCurrentAndFinalGrades(grades);
            SisGrade sisGrades = new SisGrade(grades);
            sisUserGradesList.add(new SisUserGrades(sisGrades, enrollment.getUser()));

            if (keepLookingForWarnings) {
                finalScore = sisGrades.getFinalScore();
                currentScore = sisGrades.getCurrentScore();
                finalGrade = sisGrades.getFinalGrade();

                if ((finalScore == null && currentScore == null) || (finalScore != null && currentScore == null) ||
                      (finalScore == null && currentScore != null) ||
                      (finalScore != null && currentScore != null && !finalScore.equals(currentScore))) {
                    ungradedWarning = "WARNING from Canvas: The imported grades for this course include gradebook items that were not graded for some students. Ungraded items are not included in the course grade calculation. To include these items in the course grade calculation, enter a grade of zero or higher in the empty gradebook cells. To excuse a student from a specific graded activity, enter a grade of 'EX' in the corresponding gradebook cell or leave the cell empty.";
                }

                if (finalGrade != null) {
                    noGradeSchemeWarning = "";
                }

                if ("".equals(noGradeSchemeWarning) && !"".equals(ungradedWarning)) {
                    keepLookingForWarnings = false;
                }
            }
        }

        SisInfo sis = new SisInfo();
        sis.setSiteId(sisSectionId);
        sis.setUngradedWarning(ungradedWarning);
        sis.setNoGradeSchemeWarning(noGradeSchemeWarning);
        sis.setSisUserGrades(sisUserGradesList);

        // Audit the grades being sent off
        try {
            sisGradeAuditService.writeSisGradeAudit(sisSectionId, sisUserGradesList);
        } catch (Exception e) {
            log.error("Unable to write audit information to the database", e);

            // Fail the request if something bad happened when writing the audit information
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to write audit information to the database");
        }
        return ResponseEntity.status(HttpStatus.OK).body(sis);
    }

    /**
     * override current grades/scores from unposted and overrides
     * @return
     */
    private Grades overrideCurrentAndFinalGrades(Grades grades) {
        if (grades != null) {
            if (grades.getOverrideGrade() !=null && ! grades.getOverrideGrade().isEmpty()) {
                grades.setCurrentGrade(grades.getOverrideGrade());
            } else {
                grades.setCurrentGrade(grades.getUnpostedCurrentGrade());
            }

            if (grades.getOverrideScore() !=null && ! grades.getOverrideScore().isEmpty()) {
                grades.setCurrentScore(grades.getOverrideScore());
            } else {
                grades.setCurrentScore(grades.getUnpostedCurrentScore());
            }

            grades.setFinalGrade(grades.getUnpostedFinalGrade());
            grades.setFinalScore(grades.getUnpostedFinalScore());
        }

        return grades;
    }

}
