package ru.nsu.ccfit.buzzr.discovery.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "agent")
public class AgentConfigurationProperties {

    public static final String BROADCAST_ADDRESS = "255.255.255.255";

    public static final Integer BROADCAST_PORT = 7777;

    private String id;

    private Integer port;

    private Long requestTimeoutMs;

    private Quorum quorum = new Quorum();

    private Discovery discovery = new Discovery();

    private Retry retry = new Retry();

    @Getter
    @Setter
    public static class Quorum {

        private Integer read;

        private Integer write;

    }

    @Getter
    @Setter
    public static class Discovery {

        private Long thresholdMs;

    }

    @Getter
    @Setter
    public static class Retry {

        private Integer maxRetries;

        private Long delayMs;

        private Double multiplier;

    }

}
