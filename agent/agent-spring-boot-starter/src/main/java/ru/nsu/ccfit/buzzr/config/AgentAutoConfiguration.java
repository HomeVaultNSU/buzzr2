package ru.nsu.ccfit.buzzr.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryTemplate;
import ru.nsu.ccfit.buzzr.discovery.api.AgentCounterController;
import ru.nsu.ccfit.buzzr.discovery.api.AgentMessageController;
import ru.nsu.ccfit.buzzr.discovery.config.AgentConfigurationProperties;
import ru.nsu.ccfit.buzzr.discovery.core.dao.AgentCounterDao;
import ru.nsu.ccfit.buzzr.discovery.core.dao.AgentMessageDao;
import ru.nsu.ccfit.buzzr.discovery.core.dao.impl.DefaultAgentCounterDao;
import ru.nsu.ccfit.buzzr.discovery.core.dao.impl.DefaultAgentMessageDao;
import ru.nsu.ccfit.buzzr.discovery.core.discovery.InstanceBroadcaster;
import ru.nsu.ccfit.buzzr.discovery.core.discovery.InstanceListener;
import ru.nsu.ccfit.buzzr.discovery.core.discovery.InstanceRegistry;
import ru.nsu.ccfit.buzzr.discovery.core.quorum.AgentQuorumCounterService;
import ru.nsu.ccfit.buzzr.discovery.core.quorum.AgentQuorumMessageService;
import ru.nsu.ccfit.buzzr.discovery.core.reader.AgentMessageConsumer;
import ru.nsu.ccfit.buzzr.discovery.core.reader.AgentMessageListener;
import ru.nsu.ccfit.buzzr.discovery.core.reader.DefaultAgentMessageConsumer;
import ru.nsu.ccfit.buzzr.discovery.core.service.AgentCounterService;
import ru.nsu.ccfit.buzzr.discovery.core.service.AgentMessageService;
import ru.nsu.ccfit.buzzr.discovery.core.writer.AgentMessageProducer;

@Configuration
@EnableConfigurationProperties(AgentConfigurationProperties.class)
public class AgentAutoConfiguration {

    @Bean
    public InstanceRegistry instanceRegistry(AgentConfigurationProperties properties) {
        return new InstanceRegistry(properties);
    }

    @Bean
    public InstanceListener instanceListener(InstanceRegistry registry) {
        return new InstanceListener(registry);
    }

    @Bean
    public InstanceBroadcaster instanceBroadcaster(AgentConfigurationProperties properties) throws Exception {
        return new InstanceBroadcaster(properties);
    }

    @ConditionalOnMissingBean
    @Bean
    public AgentCounterDao agentCounterDao(AgentConfigurationProperties properties) {
        return new DefaultAgentCounterDao(properties);
    }

    @ConditionalOnMissingBean
    @Bean
    public AgentMessageDao agentMessageDao(AgentConfigurationProperties properties) {
        return new DefaultAgentMessageDao(properties);
    }

    @Bean
    public AgentCounterService agentCounterService(AgentCounterDao agentCounterDao) {
        return new AgentCounterService(agentCounterDao);
    }

    @Bean
    public AgentMessageService agentMessageService(AgentMessageDao agentMessageDao) {
        return new AgentMessageService(agentMessageDao);
    }

    @Bean
    public AgentMessageController agentMessageController(AgentMessageService agentMessageService) {
        return new AgentMessageController(agentMessageService);
    }

    @Bean
    public AgentCounterController agentCounterController(AgentCounterService agentCounterService) {
        return new AgentCounterController(agentCounterService);
    }

    @Bean
    public AgentQuorumMessageService agentQuorumMessageService(
            InstanceRegistry instanceRegistry,
            AgentConfigurationProperties properties,
            RetryTemplate retryTemplate) {
        return new AgentQuorumMessageService(instanceRegistry, properties, retryTemplate);
    }

    @Bean
    public AgentQuorumCounterService agentQuorumCounterService(
            InstanceRegistry instanceRegistry,
            AgentConfigurationProperties properties,
            RetryTemplate retryTemplate) {
        return new AgentQuorumCounterService(instanceRegistry, properties, retryTemplate);
    }

    @ConditionalOnMissingBean
    @Bean
    public AgentMessageConsumer agentMessageConsumer() {
        return new DefaultAgentMessageConsumer();
    }

    @Bean
    public AgentMessageProducer agentMessageProducer(AgentQuorumMessageService agentQuorumMessageService,
                                                     AgentQuorumCounterService agentQuorumCounterService,
                                                     AgentConfigurationProperties properties,
                                                     AgentCounterService agentCounterService,
                                                     AgentMessageService agentMessageService
                                                     ) {
        return new AgentMessageProducer(agentQuorumMessageService, agentQuorumCounterService,
                properties, agentCounterService, agentMessageService);
    }

    @Bean
    public AgentMessageListener agentMessageListener(AgentMessageConsumer messageConsumer,
                                                     AgentQuorumMessageService agentQuorumMessageService,
                                                     AgentQuorumCounterService agentQuorumCounterService,
                                                     InstanceRegistry instanceRegistry,
                                                     AgentConfigurationProperties properties,
                                                     AgentCounterService agentCounterService) {
        return new AgentMessageListener(messageConsumer, agentQuorumMessageService, agentQuorumCounterService,
                instanceRegistry, properties, agentCounterService);
    }


}

