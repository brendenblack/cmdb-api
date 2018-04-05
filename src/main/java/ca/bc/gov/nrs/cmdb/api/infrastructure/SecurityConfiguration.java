package ca.bc.gov.nrs.cmdb.api.infrastructure;

//import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakPreAuthActionsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends KeycloakWebSecurityConfigurerAdapter
{
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
    {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    public FilterRegistrationBean keycloakAuthenticationProcessingFilterRegistrationBean(KeycloakAuthenticationProcessingFilter filter)
    {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean<>(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean keycloakPreAuthActionsFilterRegistrationBean(KeycloakPreAuthActionsFilter filter)
    {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean<>(filter);
        registrationBean.setEnabled(true);
        return registrationBean;
    }



//    @Bean
//    public KeycloakSpringBootConfigResolver keycloakConfigResolver()
//    {
//        return new KeycloakSpringBootConfigResolver();
//    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy()
    {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        super.configure(http);
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .sessionAuthenticationStrategy(this.sessionAuthenticationStrategy())
                .and()
                .addFilterBefore(keycloakPreAuthActionsFilter(), LogoutFilter.class)
                .addFilterBefore(keycloakAuthenticationProcessingFilter(), X509AuthenticationFilter.class)
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
                .and()
                .authorizeRequests()
                .antMatchers("/token").permitAll()
                .antMatchers("/**").authenticated() //.hasRole(CmdbPermissions.READER) //.and().authenticated()
                .anyRequest().permitAll();
    }
}
