package edu.iu.uits.lms.sisgradesexport.service;

/*-
 * #%L
 * sis-grades-export
 * %%
 * Copyright (C) 2015 - 2023 Indiana University
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Indiana University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import edu.iu.uits.lms.canvas.model.Course;
import edu.iu.uits.lms.canvas.model.Enrollment;
import edu.iu.uits.lms.canvas.model.Grades;
import edu.iu.uits.lms.canvas.model.Section;
import edu.iu.uits.lms.canvas.services.CourseService;
import edu.iu.uits.lms.canvas.services.SectionService;
import edu.iu.uits.lms.iuonly.services.SisServiceImpl;
import edu.iu.uits.lms.sisgradesexport.model.CsvResponse;
import edu.iu.uits.lms.sisgradesexport.model.SisGrade;
import edu.iu.uits.lms.sisgradesexport.model.SisInfo;
import edu.iu.uits.lms.sisgradesexport.model.SisUserGrades;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SisGradesExportService {
    @Autowired
    private CourseService courseService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private SisServiceImpl sisService;

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

        List<Enrollment> enrollments = courseService.getStudentCourseEnrollment(courseId);

        enrollments = enrollments.stream()
                .sorted(Comparator.comparing(e -> (e.getUser().getSortableName())))
                .collect(Collectors.toList());

        for (Enrollment enrollment : enrollments) {
            Grades grades = overrideCurrentAndFinalGrades(enrollment.getGrades());
            data.add(new String[]{enrollment.getUser().getSisUserId(), grades.getCurrentGrade()});
        }

        SimpleDateFormat dateFormatForFilename = new SimpleDateFormat("yyyy-MM-dd");

        Course course = courseService.getCourse(courseId);

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

        int iuoccCourseCount = sisService.getIuoccCourseCount(sisSectionId);
        if (iuoccCourseCount > 0) {
            Section section = sectionService.getSection("sis_section_id:" + sisSectionId);
            enrollments = courseService.getStudentCourseEnrollment(section.getCourse_id());
        } else {
            enrollments = sectionService.getStudentSectionEnrollments(sisSectionId);
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
