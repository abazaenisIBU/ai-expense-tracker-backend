package com.example.aiexpensetracker.rest.configuration.AsyncConfiguration;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;

class AsyncConfigurationTest {

    @Test
    void taskExecutorBeanProperties() {
        AsyncConfiguration config = new AsyncConfiguration();
        Executor executor = config.taskExecutor();

        assertNotNull(executor);
        assertInstanceOf(ThreadPoolTaskExecutor.class, executor);

        ThreadPoolTaskExecutor tpe = (ThreadPoolTaskExecutor) executor;
        assertEquals(5, tpe.getCorePoolSize());
        assertEquals(10, tpe.getMaxPoolSize());
        assertEquals(25, tpe.getQueueCapacity());
        assertEquals("Async-", tpe.getThreadNamePrefix());
    }
}