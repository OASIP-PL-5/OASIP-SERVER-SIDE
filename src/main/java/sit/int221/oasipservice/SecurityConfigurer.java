package sit.int221.oasipservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import sit.int221.oasipservice.exceptions.CustomAuthenticationFailureHandler;
import sit.int221.oasipservice.filters.JwtFilter;
import sit.int221.oasipservice.services.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    //    PasswordEncoder
//    NoOpPasswordEncoder.getInstance()

    @Autowired
    private JwtFilter jwtFilter;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

//    ใช้ try-catch ดักได้
//    @Bean
//    public AuthenticationFailureHandler authenticationFailureHandler(){
//        return new CustomAuthenticationFailureHandler();
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                //public endpoints
                .antMatchers("/api/login").permitAll()
                .antMatchers(HttpMethod.GET,"/api/event-categories/**").permitAll()
                .antMatchers("/api/events/**").permitAll()
                .antMatchers("/api/match").permitAll()
                .antMatchers(HttpMethod.POST,"/api/users").permitAll()
                .antMatchers(HttpMethod.GET,"/api/events/{id}").permitAll()



                //privilege endpoint
//                .antMatchers("/api/**").hasAnyRole(String.valueOf(EnumRole.admin))
//                .antMatchers(HttpMethod.POST,"/api/event-categories/**").hasAnyRole(String.valueOf(EnumRole.admin))
//                .antMatchers(HttpMethod.PUT,"/api/event-categories/**").hasAnyRole(String.valueOf(EnumRole.admin))
                .antMatchers("/api/**").hasRole("admin")
//                .antMatchers(HttpMethod.POST,"/api/users/**").hasAnyRole("ADMIN")
//                .antMatchers(HttpMethod.PUT,"/api/users/**").hasAnyRole("ADMIN")
//                .antMatchers(HttpMethod.DELETE,"/api/users/**").hasAnyRole("ADMIN")


                .anyRequest().authenticated()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


    }
}