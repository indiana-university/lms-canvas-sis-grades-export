package edu.iu.uits.lms.sisgradesexport.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class SisInfo implements Serializable {
   @XmlElement(nillable = true)
   private String siteId;

   @XmlElement(nillable = true)
   private String ungradedWarning;

   @XmlElement(nillable = true)
   private String noGradeSchemeWarning;

   @XmlElement(nillable = true)
   private List<SisUserGrades> sisUserGrades;
}
