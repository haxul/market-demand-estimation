package com.haxul.headhunter.models.hhApiResponses;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.haxul.headhunter.models.experience.ExperienceHeadhunter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonDeserialize(using = VacancyDetailedPageHeadHunterJsonDeserializer.class)
public class VacancyDetailedPageHeadHunter {
     private Integer id;
     private ExperienceHeadhunter experience;

     @Override
     public int hashCode() {
          final int prime = 31;
          int result = 1;
          result = prime * result + ((id == null) ? 0 : id.hashCode());
          return result;
     }

     @Override
     public boolean equals(Object other) {
          if (this == other) return true;
          if (other == null) return false;
          if (!(other instanceof VacancyDetailedPageHeadHunter)) return false;
          var otherVacancy = (VacancyDetailedPageHeadHunter) other;
          return id.equals(otherVacancy.getId());
     }
}
