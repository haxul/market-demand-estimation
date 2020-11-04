package com.haxul.headhunter.entities;

import com.haxul.headhunter.models.area.City;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "market_demands")
public class MarketDemand implements Serializable {

    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "market_demands_id_seq"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private Long id;

    @Column(name = "position", nullable = false , length = 200)
    private String position;

    @Column(name = "average_rub_gross_salary", nullable = false)
    private int averageRubGrossSalary;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "at_moment", nullable = false)
    private Date atMoment;

    @Enumerated(EnumType.STRING)
    @Column(name = "city", nullable = false, length = 100)
    private City city;

    @Column(name = "min_year_experience", nullable = false)
    private int minYearExperience;

    @Column(name = "source", nullable = false , length = 50)
    private String source;
}
