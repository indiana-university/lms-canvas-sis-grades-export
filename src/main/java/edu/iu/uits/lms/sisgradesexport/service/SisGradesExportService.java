package edu.iu.uits.lms.sisgradesexport.service;

import canvas.client.generated.api.CoursesApi;
import canvas.client.generated.model.Course;
import canvas.client.generated.model.Enrollment;
import canvas.client.generated.model.Grades;
import edu.iu.uits.lms.sisgradesexport.model.CsvResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    public CsvResponse exportGradesForSis(String courseId) {
        final String gradeType = "FIN";
        final Date currDate = new Date();

        List<Object[]> data = new ArrayList<Object[]>();
        List<String> columns = Arrays.asList(new String[]{gradeType});
        log.debug("Preparing to write out grade to csv file...");

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
