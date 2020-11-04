package com.haxul.exchangeCurrency.entities;

import com.haxul.exchangeCurrency.models.CurrenciesExchanges;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "currencies_rate")
public class CurrencyRate implements Serializable {

    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "currencies_rate_id_seq"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private Integer id;

    @Column(name = "exchanged_currencies", nullable = false, unique = true, length = 15)
    @Enumerated(EnumType.STRING)
    private CurrenciesExchanges exchangedCurrencies;

    @Column(name = "rate", nullable = false, columnDefinition = "real")
    private Float rate;

    @Column(name = "date", nullable = false)
    private Date date;
}
