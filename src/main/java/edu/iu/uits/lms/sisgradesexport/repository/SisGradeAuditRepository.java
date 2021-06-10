package edu.iu.uits.lms.sisgradesexport.repository;

import edu.iu.uits.lms.sisgradesexport.model.SisGradeAudit;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tnguyen on 8/3/17.
 */

@Component
public interface SisGradeAuditRepository extends PagingAndSortingRepository<SisGradeAudit, Long> {

    List<SisGradeAudit> findByUserId(String userId);
    List<SisGradeAudit> findBySisSectionId(String courseId);
}