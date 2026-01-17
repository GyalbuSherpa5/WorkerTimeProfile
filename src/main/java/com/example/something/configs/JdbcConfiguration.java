package com.example.something.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;

import java.util.Arrays;

@Configuration
public class JdbcConfiguration {

    @Bean
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(Arrays.asList(
                new JsonNodeConverters.PGobjectToJsonNodeConverter(),
                new JsonNodeConverters.StringToJsonNodeConverter(),
                new JsonNodeConverters.JsonNodeToPGobjectConverter()
        ));
    }
}