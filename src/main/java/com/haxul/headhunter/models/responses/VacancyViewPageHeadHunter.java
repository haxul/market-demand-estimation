package com.haxul.headhunter.models.responses;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.haxul.headhunter.models.experience.ExperienceHeadhunter;
import lombok.Data;

@Data
@JsonDeserialize(using = VacancyViewPageHeadHunterJsonDeserializer.class)
public class VacancyViewPageHeadHunter {
     private Integer id;
     private ExperienceHeadhunter experience;
}
