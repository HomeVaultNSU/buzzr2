package ru.nsu.ccfit.buzzr.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.NestedRuntimeException;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import ru.nsu.ccfit.buzzr.discovery.config.AgentConfigurationProperties;

import java.time.Duration;

@Configuration
public class AgentRetryingAutoConfiguration {

    @Bean
    public RetryTemplate retryTemplate(@Qualifier("agentApiRetryPolicy") RetryPolicy agentApiRetryPolicy) {
        return new RetryTemplate(agentApiRetryPolicy);
    }

    @Bean
    public RetryPolicy agentApiRetryPolicy(AgentConfigurationProperties properties) {
        return RetryPolicy.builder()
                .maxRetries(properties.getRetry().getMaxRetries())
                .delay(Duration.ofMillis(properties.getRetry().getDelayMs()))
                .multiplier(properties.getRetry().getMultiplier())
                .includes(NestedRuntimeException.class)
                .build();
    }

}
