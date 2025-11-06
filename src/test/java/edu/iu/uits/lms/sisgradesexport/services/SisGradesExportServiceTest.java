package edu.iu.uits.lms.sisgradesexport.services;

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
import edu.iu.uits.lms.canvas.model.User;
import edu.iu.uits.lms.canvas.services.CourseService;
import edu.iu.uits.lms.canvas.services.SectionService;
import edu.iu.uits.lms.iuonly.services.SisServiceImpl;
import edu.iu.uits.lms.sisgradesexport.model.CsvResponse;
import edu.iu.uits.lms.sisgradesexport.service.SisGradeAuditService;
import edu.iu.uits.lms.sisgradesexport.service.SisGradesExportService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ContextConfiguration(classes={SisGradesExportService.class})
@SpringBootTest
@ActiveProfiles("none")
public class SisGradesExportServiceTest {
   @Autowired
   private SisGradesExportService sisGradesExportService;

   @MockBean
   private CourseService courseService;

   @MockBean
   private SectionService sectionService;

   @MockBean
   private SisServiceImpl sisService;

   @MockBean
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

      Mockito.when(courseService.getStudentCourseEnrollments(courseId))
              .thenReturn(Arrays.asList(enrollment1, enrollment2, enrollment3));

      Course returnedCourse = new Course();
      returnedCourse.setSisCourseId(courseId);

      Mockito.when(courseService.getCourse(any(String.class))).thenReturn(returnedCourse);

      CsvResponse csvResponse = sisGradesExportService.exportGradesForSis(courseId);
      Assertions.assertNotNull(csvResponse);

      List<Object[]> records = csvResponse.getRecords();
      Assertions.assertNotNull(records);

      Assertions.assertEquals(3, records.size());

      // Make sure they are sorted by sortable name
      Assertions.assertEquals("5555", records.get(0)[0]);
      Assertions.assertEquals("9999", records.get(1)[0]);
      Assertions.assertEquals("1111", records.get(2)[0]);
   }
}
