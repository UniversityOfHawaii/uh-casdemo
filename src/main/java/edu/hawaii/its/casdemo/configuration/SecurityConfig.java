package edu.hawaii.its.casdemo.configuration;

import static java.util.Collections.singletonList;

import jakarta.annotation.PostConstruct;
import org.apereo.cas.client.session.SingleSignOutFilter;
import org.apereo.cas.client.validation.Cas30ServiceTicketValidator;
import org.apereo.cas.client.validation.TicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.cas.web.authentication.ServiceAuthenticationDetailsSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.util.Assert;

import edu.hawaii.its.casdemo.access.CasUserDetailsService;
import edu.hawaii.its.casdemo.access.DelegatingAuthenticationFailureHandler;
import edu.hawaii.its.casdemo.access.UserBuilder;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${app.url.base}")
    private String appUrlBase;

    @Value("${app.url.home}")
    private String appUrlHome;

    @Value("${app.url.error-login}")
    private String appUrlError;

    @Value("${cas.login.url}")
    private String casLoginUrl;

    @Value("${cas.mainUrl}")
    private String casMainUrl;

    @Autowired
    private UserBuilder userBuilder;

    @PostConstruct
    public void init() {
        logger.info("SecurityConfig starting...");

        logger.info("   appUrlHome: {}", appUrlHome);
        logger.info("   appUrlBase: {}", appUrlBase);
        logger.info("  appUrlError: {}", appUrlError);
        logger.info("   casMainUrl: {}", casMainUrl);
        logger.info("  casLoginUrl: {}", casLoginUrl);
        logger.info("  userBuilder: {}", userBuilder);

        Assert.hasLength(appUrlHome, "property 'appUrlHome' is required");
        Assert.hasLength(appUrlBase, "property 'appUrlBase' is required");
        Assert.hasLength(appUrlError, "property 'appUrlError' is required");
        Assert.hasLength(casMainUrl, "property 'casMainUrl' is required");
        Assert.hasLength(casLoginUrl, "property 'casLoginUrl' is required");

        logger.info("SecurityConfig started.");
    }

    @Bean
    @ConfigurationProperties(prefix = "cas")
    public ServiceProperties serviceProperties() {
        return new ServiceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "cas.saml")
    public TicketValidator casTicketValidator() {
        return new Cas30ServiceTicketValidator(casMainUrl);
    }

    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setLoginUrl(casLoginUrl);
        entryPoint.setServiceProperties(serviceProperties());
        return entryPoint;
    }

    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        return new SingleSignOutFilter();
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setKey("an_id_for_this_auth_provider_only");
        provider.setAuthenticationUserDetailsService(authenticationUserDetailsService());
        provider.setServiceProperties(serviceProperties());
        provider.setTicketValidator(casTicketValidator());
        return provider;
    }

    @Bean
    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> authenticationUserDetailsService() {
        return new CasUserDetailsService(userBuilder);
    }

    @Bean
    protected AuthenticationManager authenticationManager() {
        return new ProviderManager(singletonList(casAuthenticationProvider()));
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());

        filter.setProxyAuthenticationFailureHandler(authenticationFailureHandler());
        filter.setAuthenticationFailureHandler(authenticationFailureHandler());

        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler());

        ServiceAuthenticationDetailsSource authenticationDetailsSource =
                new ServiceAuthenticationDetailsSource(serviceProperties());
        filter.setAuthenticationDetailsSource(authenticationDetailsSource);
        filter.setProxyReceptorUrl("/receptor");
        filter.setServiceProperties(serviceProperties());

        return filter;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        SavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler =
                new SavedRequestAwareAuthenticationSuccessHandler();
        authenticationSuccessHandler.setAlwaysUseDefaultTargetUrl(false);
        authenticationSuccessHandler.setDefaultTargetUrl(appUrlHome);
        return authenticationSuccessHandler;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new DelegatingAuthenticationFailureHandler(appUrlError);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CasAuthenticationFilter casAuthenticationFilter) throws Exception {
        http
                .headers(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/fonts/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/javascript/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/campus", "/campuses").authenticated()
                        .requestMatchers("/contact").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/error-login").permitAll()
                        .requestMatchers("/home").permitAll()
                        .requestMatchers("/help/**").permitAll()
                        .requestMatchers("/404").permitAll()
                        .requestMatchers("/h2-ui/**").hasRole("ADMIN")
                        .requestMatchers("/holiday", "/holidays").hasRole("ADMIN")
                        .requestMatchers("/holidaygrid").hasRole("ADMIN")
                        .requestMatchers("/holidaysgrid").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user").hasRole("USER")
                        .requestMatchers("/user/data").hasRole("USER")
                        .requestMatchers("/feedback").hasRole("USER")
                        .requestMatchers("/logout").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(casAuthenticationEntryPoint()))
                .logout((logout) -> logout.logoutSuccessUrl("/"))
                .addFilter(casAuthenticationFilter);

        return http.build();
    }

}
