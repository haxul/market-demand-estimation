package com.haxul.headhunter.models.responses;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.haxul.headhunter.models.experience.Experience;
import lombok.Data;

@Data
@JsonDeserialize(using = VacancyViewPageResponseJsonDeserializer.class)
public class VacancyViewPageResponse {
     private Integer id;
     private Experience experience;
}
