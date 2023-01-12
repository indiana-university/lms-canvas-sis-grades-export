package edu.iu.uits.lms.sisgradesexport.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.iu.uits.lms.canvas.model.Grades;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@JsonIgnoreProperties(ignoreUnknown=true)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
public class SisGrade {

    @XmlElement(nillable=true)
    @JsonProperty("current_score")
    private String currentScore;

    @XmlElement(nillable=true)
    @JsonProperty("final_score")
    private String finalScore;

    @XmlElement(nillable=true)
    @JsonProperty("current_grade")
    private String currentGrade;

    @XmlElement(nillable=true)
    @JsonProperty("final_grade")
    private String finalGrade;

    public SisGrade(Grades grades) {
        if (grades != null) {
            this.currentGrade = grades.getCurrentGrade();
            this.currentScore = grades.getCurrentScore();
            this.finalGrade= grades.getFinalGrade();
            this.finalScore = grades.getFinalScore();
        }
    }
}
