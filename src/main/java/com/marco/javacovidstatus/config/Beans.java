package com.marco.javacovidstatus.config;

import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.fasterxml.classmate.TypeResolver;
import com.marco.javacovidstatus.services.implementations.EmailNotificationSender;
import com.marco.javacovidstatus.services.interfaces.NotificationSenderInterface;
import com.marco.javacovidstatus.utils.CovidUtils;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Standard Springboot configuration class
 * 
 * @author Marco
 *
 */
@Configuration
public class Beans {

    @Value("${covidstatus.version}")
    private String appVersion;
    @Value("${spring.mail.username}")
    private String emailUser;
    @Value("${spring.mail.password}")
    private String emailPassw;

    @Bean()
    public ThreadPoolTaskScheduler taskScheduler() {
        /*
         * Setting a thread pool, so the scheduler can run in a different thread
         */
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(20);
        return taskScheduler;
    }
    
    @Bean
    public JavaMailSender getJavaMailSender() {
        /*
         * Send email using your GMAIL account
         */
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(emailUser);
        mailSender.setPassword(emailPassw);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean
    public NotificationSenderInterface getNotificationSenderInterface() {
        return new EmailNotificationSender();
    }

    @Bean
    public CovidUtils getCovidUtils() {
        return new CovidUtils();
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:/messages/errorCodes");

        return messageSource;
    }

    @Bean
    public Docket swaggerConfiguration() {
        TypeResolver resolver = new TypeResolver();
        return new Docket(DocumentationType.SWAGGER_2)
                .alternateTypeRules(AlternateTypeRules.newRule(resolver.resolve(List.class, LocalDate.class),
                        resolver.resolve(List.class, String.class), Ordered.HIGHEST_PRECEDENCE))
                .select().apis(RequestHandlerSelectors.basePackage("com.marco.javacovidstatus.controllers"))
                .paths(PathSelectors.regex("/.*")).build().apiInfo(apiEndPointsInfo());
    }

    private ApiInfo apiEndPointsInfo() {
        return new ApiInfoBuilder().title("Covid 19 Italy").description(
                "I have created this project to collect the Covid 19 data related to the Italian situation and display them into multiple charts")
                .contact(new Contact("Marco Solina", "", "")).version(appVersion).build();
    }

}
