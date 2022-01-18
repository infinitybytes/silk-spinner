package ai.ibytes.ingester.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import ai.ibytes.ingester.storage.FileSystemStorageService;
import ai.ibytes.ingester.model.SystemUser;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@EnableWebSecurity
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
    private FileSystemStorageService storageService;

	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Bean
	public InMemoryUserDetailsManager users() {
		List<UserDetails> userList = new ArrayList<>();

		try {
			// load users from disk
			SystemUser users[] = objectMapper.readValue(storageService.loadJson("users").toFile(), SystemUser[].class);

			Arrays.asList(users).stream().forEach( user -> {
				userList.add(
					User.builder()
						.username(user.getUsername())
						.password(user.getPassword())
						.roles(user.getRoles())
						.build()
				);
			});
		} catch (JsonParseException e) {
			log.error("Error loading users from disk, login disabled.",e);
		} catch (JsonMappingException e) {
			log.error("Error loading users from disk, login disabled.",e);
		} catch (IOException e) {
			log.error("Error loading users from disk, login disabled.",e);
		}

		return new InMemoryUserDetailsManager(userList);
	}

	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
		.csrf().disable()
		.authorizeRequests()
            .antMatchers("/**").hasRole("ADMIN")
            .anyRequest().authenticated()
            .and()
            .formLogin().permitAll()
            .and()
            .logout().permitAll()
            .and()
            .exceptionHandling().accessDeniedPage("/404")
            ;
    }
}
