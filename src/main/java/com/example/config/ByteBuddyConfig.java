package com.example.config;

import com.example.repository.UserRepository;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class ByteBuddyConfig {
    private static final Logger logger = LoggerFactory.getLogger(ByteBuddyConfig.class);

    @PostConstruct
    public void init() {
        logger.info("Initializing ByteBuddy configuration...");
        
        try {
            // Install the ByteBuddy agent
            ByteBuddyAgent.install();
            logger.info("ByteBuddy agent installed successfully");

            // Create a new ByteBuddy instance
            new ByteBuddy()
                .redefine(UserRepository.class)
                .method(ElementMatchers.isPublic().and(ElementMatchers.not(ElementMatchers.isStatic())))
                .intercept(MethodDelegation.to(UserRepository.class))
                .make()
                .load(UserRepository.class.getClassLoader());
            
            logger.info("ByteBuddy instrumentation completed successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize ByteBuddy", e);
        }
    }

    @Bean
    public UserRepository userRepository() throws Exception {
        logger.info("Creating ByteBuddy proxy for UserRepository");
        Class<? extends UserRepository> proxyClass = new ByteBuddy()
                .subclass(UserRepository.class)
                .method(ElementMatchers.any())
                .intercept(MethodDelegation.to(UserRepository.class))
                .make()
                .load(UserRepository.class.getClassLoader())
                .getLoaded();
        return proxyClass.getDeclaredConstructor().newInstance();
    }
} 