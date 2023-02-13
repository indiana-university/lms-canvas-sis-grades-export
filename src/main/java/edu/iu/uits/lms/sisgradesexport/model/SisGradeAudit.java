package edu.iu.uits.lms.sisgradesexport.model;

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

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by tnguyen on 8/3/17.
 */

@Entity
@Table(name = "SIS_GRADE_AUDIT")

@SequenceGenerator(name = "SIS_GRADE_AUDIT_ID_SEQ", sequenceName = "SIS_GRADE_AUDIT_ID_SEQ", allocationSize = 1)
@Data
@NoArgsConstructor
public class SisGradeAudit implements Serializable {

    @Id
    @GeneratedValue(generator = "SIS_GRADE_AUDIT_ID_SEQ")
    @Column(name = "SIS_GRADE_AUDIT_ID")
    private Long id;

    @NonNull
    @Column(name = "SIS_SECTION_ID")
    private String sisSectionId;

    @NonNull
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "GRADE")
    private String grade;

    @Column(name = "DATE_SENT")
    private Date dateSent;

    @Column(name = "SCORE")
    private String score;

    public SisGradeAudit(String sisSectionId, String userId, String grade, String score) {
        this.sisSectionId = sisSectionId;
        this.userId = userId;
        this.grade = grade;
        this.score = score;
    }

    @PreUpdate
    @PrePersist
    public void updateTimeStamps() {
        dateSent = new Date();
    }

}
