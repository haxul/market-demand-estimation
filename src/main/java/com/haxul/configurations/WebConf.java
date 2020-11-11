package com.haxul.configurations;


import com.haxul.analytics.DateType;
import com.haxul.headhunter.models.area.City;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConf implements WebMvcConfigurer {


    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Converter<String, City>() {
            @Override
            public City convert(String source) {
                return City.valueOf(source.toUpperCase());
            }
        });
        registry.addConverter(new Converter<String, DateType>() {
            @Override
            public DateType convert(String source) {
                return DateType.valueOf(source.toUpperCase());
            }
        });
    }
}
