package com.haxul.headhunter.models.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.haxul.headhunter.models.experience.ExperienceHeadhunter;
import lombok.Data;

@Data
public class VacancyHeadHunter {

     private SalaryHeadHunter salary;

     private Integer id;

     @JsonIgnore
     private ExperienceHeadhunter experience;

}


