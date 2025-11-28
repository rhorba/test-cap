package com.renault.garage.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * Configuration pour le traitement asynchrone des événements
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(AsyncConfig.class);
    
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("event-consumer-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        
        logger.info("✅ Configuration du pool de threads asynchrone: " +
                    "core={}, max={}, queue={}", 
                    executor.getCorePoolSize(), 
                    executor.getMaxPoolSize(), 
                    executor.getQueueCapacity());
        
        return executor;
    }
    
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) -> {
            logger.error("❌ Exception non gérée dans la méthode asynchrone: {}", 
                        method.getName(), throwable);
            logger.error("Paramètres: {}", params);
        };
    }
}
