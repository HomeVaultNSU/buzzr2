package ru.nsu.ccfit.buzzr.discovery.core.quorum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.retry.RetryTemplate;
import ru.nsu.ccfit.buzzr.discovery.api.client.AgentClient;
import ru.nsu.ccfit.buzzr.discovery.api.dto.AgentMessageDto;
import ru.nsu.ccfit.buzzr.discovery.config.AgentConfigurationProperties;
import ru.nsu.ccfit.buzzr.discovery.core.dao.model.MessageStatus;
import ru.nsu.ccfit.buzzr.discovery.core.discovery.InstanceInfo;
import ru.nsu.ccfit.buzzr.discovery.core.discovery.InstanceRegistry;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class AgentQuorumMessageService extends AgentQuorumBaseService {

    public AgentQuorumMessageService(InstanceRegistry instanceRegistry,
                                     AgentConfigurationProperties properties,
                                     RetryTemplate retryTemplate) {
        super(instanceRegistry, properties, retryTemplate);
    }

    public QuorumResult<AgentMessageDto> read(String srcId, String dstId, Long seq) {

        QuorumResult<Map<InstanceInfo, AgentMessageDto>> quorumResult = sendAsyncUntilQuorum(
                instanceRegistry.getInstances().values(),
                properties.getQuorum().getRead(),
                instanceInfo -> {
                    AgentClient client = CLIENT_FACTORY.apply(instanceInfo);
                    return client.getMessage(srcId, dstId, seq);
                }
        );

        if (!quorumResult.isSuccess()) {
            return QuorumResult.failedQuorum();
        }

        Map<InstanceInfo, AgentMessageDto> counters = quorumResult.getResponse();

        if (counters.isEmpty()) {
            return QuorumResult.failedQuorum();
        }

        return counters.values()
                .stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparing(AgentMessageDto::getStatus, MessageStatus.COMPARATOR))
                .map(message -> new QuorumResult<>(QuorumResult.Status.SUCCESS, message))
                .orElse(QuorumResult.failedQuorum());
    }

    public QuorumResult<Void> write(AgentMessageDto agentMessageDto) {
        QuorumResult<Map<InstanceInfo, String>> quorumResult = sendAsyncUntilQuorum(
                instanceRegistry.getInstances().values(),
                properties.getQuorum().getWrite() - 1, // 1 - запись локально
                instanceInfo -> {
                    AgentClient client = CLIENT_FACTORY.apply(instanceInfo);
                    return client.updateMessage(agentMessageDto);
                }
        );

        if (quorumResult.isSuccess()) {
            return QuorumResult.successQuorum();

        } else {
            return QuorumResult.failedQuorum();
        }
    }


}
