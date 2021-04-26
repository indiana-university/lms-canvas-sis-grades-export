package edu.iu.uits.lms.sisgradesexport.model;

import canvas.client.generated.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@JsonIgnoreProperties (ignoreUnknown=true)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@AllArgsConstructor
public class SisUserGrades implements Serializable {
	@XmlElement(nillable=true)
	private SisGrade grade;
	
	@XmlElement(nillable=true)
	private User user;
	
}
