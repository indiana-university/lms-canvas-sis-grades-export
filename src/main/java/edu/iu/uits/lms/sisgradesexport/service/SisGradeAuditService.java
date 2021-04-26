package edu.iu.uits.lms.sisgradesexport.service;

import edu.iu.uits.lms.sisgradesexport.model.SisGradeAudit;
import edu.iu.uits.lms.sisgradesexport.model.SisUserGrades;
import edu.iu.uits.lms.sisgradesexport.repository.SisGradeAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Writes audit information to the SIS_GRADE_AUDIT table when grades are requested from SIS.
 */
@Service
public class SisGradeAuditService {

    @Autowired
    private SisGradeAuditRepository sisGradeAuditRepository = null;

    /**
     * Writes audit information to the SIS_GRADE_AUDIT table when grades are requested from SIS.
     * Uses the sis_user_id and current_grade fields to write out to the database.
     * @param sisSectionId String representation of the section from the SIS
     * @param sisUserGradesList
     */

    public void writeSisGradeAudit(String sisSectionId, List<SisUserGrades> sisUserGradesList) {

        List<SisGradeAudit> newSisGradeAuditList = new ArrayList<>();

        for (SisUserGrades userGrade : sisUserGradesList) {
            String grade = userGrade.getGrade().getCurrentGrade();
            String score = userGrade.getGrade().getCurrentScore();


            String user = userGrade.getUser().getSisUserId();
            if (user == null)
                user = userGrade.getUser().getLoginId();

            SisGradeAudit newSisGradeAudit = new SisGradeAudit(sisSectionId, user, grade, score);
            newSisGradeAuditList.add(newSisGradeAudit);

        }
        sisGradeAuditRepository.saveAll(newSisGradeAuditList);
    }

}

