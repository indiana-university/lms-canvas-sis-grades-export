package edu.iu.uits.lms.sisgradesexport.services;

import canvas.client.generated.api.CoursesApi;
import canvas.client.generated.api.SectionsApi;
import canvas.client.generated.model.Course;
import canvas.client.generated.model.Enrollment;
import canvas.client.generated.model.Grades;
import canvas.client.generated.model.User;
import edu.iu.uits.lms.sisgradesexport.model.CsvResponse;
import edu.iu.uits.lms.sisgradesexport.service.SisGradeAuditService;
import edu.iu.uits.lms.sisgradesexport.service.SisGradesExportService;
import iuonly.client.generated.api.SudsApi;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("none")
public class SisGradesExportServiceTest {
   @InjectMocks
   private SisGradesExportService sisGradesExportService;

   @Mock
   private CoursesApi coursesApi;

   @Mock
   private SectionsApi sectionsApi;

   @Mock
   private SudsApi sudsApi;

   @Mock
   private SisGradeAuditService sisGradeAuditService;

   @Test
   public void testCsvSorting() {
      User user1 = new User();
      user1.setSisUserId("1111");
      user1.setSortableName("Spock");

      User user2 = new User();
      user2.setSisUserId("5555");
      user2.setSortableName("Kirk");

      User user3 = new User();
      user3.setSisUserId("9999");
      user3.setSortableName("McCoy");


      Enrollment enrollment1 = new Enrollment();
      enrollment1.setUser(user1);
      Grades grades1 = new Grades();
      grades1.setFinalGrade("A");
      enrollment1.setGrades(grades1);

      Enrollment enrollment2 = new Enrollment();
      enrollment2.setUser(user2);
      Grades grades2 = new Grades();
      grades2.setFinalGrade("C");
      enrollment2.setGrades(grades2);

      Enrollment enrollment3 = new Enrollment();
      enrollment3.setUser(user3);
      Grades grades3 = new Grades();
      grades3.setFinalGrade("F");
      enrollment3.setGrades(grades3);

      final String courseId = "1234";

      Mockito.when(coursesApi.getStudentCourseEnrollment(courseId))
              .thenReturn(Arrays.asList(enrollment1, enrollment2, enrollment3));

      Course returnedCourse = new Course();
      returnedCourse.setSisCourseId(courseId);

      Mockito.when(coursesApi.getCourse(any(String.class))).thenReturn(returnedCourse);

      CsvResponse csvResponse = sisGradesExportService.exportGradesForSis(courseId);
      Assert.assertNotNull(csvResponse);

      List<Object[]> records = csvResponse.getRecords();
      Assert.assertNotNull(records);

      Assert.assertEquals(3, records.size());

      // Make sure they are sorted by sortable name
      Assert.assertEquals("5555", records.get(0)[0]);
      Assert.assertEquals("9999", records.get(1)[0]);
      Assert.assertEquals("1111", records.get(2)[0]);
   }
}
