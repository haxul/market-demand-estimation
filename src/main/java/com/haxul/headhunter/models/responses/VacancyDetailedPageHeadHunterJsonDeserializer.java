package com.haxul.headhunter.models.responses;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

import static com.haxul.headhunter.models.experience.ExperienceHeadhunter.*;

public class VacancyDetailedPageHeadHunterJsonDeserializer extends StdDeserializer<VacancyDetailedPageHeadHunter> {

    public VacancyDetailedPageHeadHunterJsonDeserializer() {
        this(null);
    }
    protected VacancyDetailedPageHeadHunterJsonDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public VacancyDetailedPageHeadHunter deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        VacancyDetailedPageHeadHunter vacancyViewPageResponse = new VacancyDetailedPageHeadHunter();
        vacancyViewPageResponse.setId(Integer.parseInt(node.get("id").asText()));
        String s = node.get("experience").get("id").asText();

        if (s.equals(NO_EXPERIENCE.getId())) vacancyViewPageResponse.setExperience(NO_EXPERIENCE);
        else if (s.equals(BETWEEN_1_AND_3.getId()))  vacancyViewPageResponse.setExperience(BETWEEN_1_AND_3);
        else if (s.equals(BETWEEN_3_AND_6.getId())) vacancyViewPageResponse.setExperience(BETWEEN_3_AND_6);
        else if (s.equals(MORE_THAN_6.getId())) vacancyViewPageResponse.setExperience(MORE_THAN_6);
        return vacancyViewPageResponse;
    }
}
