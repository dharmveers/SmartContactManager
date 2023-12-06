package com.example.dharam.config;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Cleanup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class UserConfig {
	
	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}
	
	@Bean
	public BCryptPasswordEncoder paasEncoder() {
		return (new BCryptPasswordEncoder());
	}
	
	@Bean 
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider dao = new DaoAuthenticationProvider();
		dao.setUserDetailsService(getUserDetailsService());
		dao.setPasswordEncoder(paasEncoder());
		return dao;
 	}
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
	  return auth.getAuthenticationManager(); 
	  }
	
    @Bean 
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		  http
		  		.csrf()
		  		.disable()
		  		.authorizeRequests()
		  		.antMatchers("/user/**")
		  		.hasAuthority("USER")
		  		.antMatchers("/**")
		  		.permitAll()
		  		.and()
		  		.formLogin()
		  		.loginPage("/login")
		  		.loginProcessingUrl("/doLogin")
		  		.defaultSuccessUrl("/user/index");
		  http.authenticationProvider(daoAuthenticationProvider());
		  return http.build();
		  
		  }
}
