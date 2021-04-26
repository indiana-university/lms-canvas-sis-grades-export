package edu.iu.uits.lms.sisgradesexport.model;

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
