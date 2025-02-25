package com.university.healthcenter.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Autowired
    private PasswordEncoderFactory passwordEncoderFactory;

	@Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
	  @Bean
    public PasswordEncoder passwordEncoder() {
        return passwordEncoderFactory.createPasswordEncoder("bcrypt");
    }
}
