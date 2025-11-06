package edu.iu.uits.lms.sisgradesexport.service;

/*-
 * #%L
 * sis-grades-export
 * %%
 * Copyright (C) 2015 - 2025 Indiana University
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

