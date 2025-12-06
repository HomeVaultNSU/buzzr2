package ru.nsu.ccfit.buzzr.discovery.core.quorum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.retry.RetryTemplate;
import ru.nsu.ccfit.buzzr.discovery.api.client.AgentClient;
import ru.nsu.ccfit.buzzr.discovery.api.dto.AgentCounterResponse;
import ru.nsu.ccfit.buzzr.discovery.config.AgentConfigurationProperties;
import ru.nsu.ccfit.buzzr.discovery.core.discovery.InstanceInfo;
import ru.nsu.ccfit.buzzr.discovery.core.discovery.InstanceRegistry;

import java.util.Comparator;
import java.util.Map;

@Slf4j
public class AgentQuorumCounterService extends AgentQuorumBaseService {

    public AgentQuorumCounterService(InstanceRegistry instanceRegistry,
                                     AgentConfigurationProperties properties,
                                     RetryTemplate retryTemplate) {
        super(instanceRegistry, properties, retryTemplate);
    }

    public QuorumResult<AgentCounterResponse> read(String srcId, String dstId) {

        QuorumResult<Map<InstanceInfo, AgentCounterResponse>> quorumResult = sendAsyncUntilQuorum(
                instanceRegistry.getInstances().values(),
                properties.getQuorum().getRead(),
                instanceInfo -> {
                    AgentClient client = CLIENT_FACTORY.apply(instanceInfo);
                    return client.getCounter(srcId, dstId);
                }
        );

        if (!quorumResult.isSuccess()) {
            return QuorumResult.failedQuorum();
        }

        Map<InstanceInfo, AgentCounterResponse> counters = quorumResult.getResponse();

        if (counters.isEmpty()) {
            return QuorumResult.failedQuorum();
        }

        return counters.values()
                .stream()
                .max(Comparator.comparing(AgentCounterResponse::getSeq))
                .map(message -> new QuorumResult<>(QuorumResult.Status.SUCCESS, message))
                .orElse(QuorumResult.failedQuorum());
    }

    public QuorumResult<Void> write(String srcId, String dstId, Long seq) {
        QuorumResult<Map<InstanceInfo, String>> quorumResult = sendAsyncUntilQuorum(
                instanceRegistry.getInstances().values().stream().filter(i -> !i.getId().equals(dstId)).toList(),
                properties.getQuorum().getWrite(),
                instanceInfo -> {
                    AgentClient client = CLIENT_FACTORY.apply(instanceInfo);
                    return client.setCounter(srcId, dstId, seq);
                }
        );

        if (quorumResult.isSuccess()) {
            return QuorumResult.successQuorum();

        } else {
            return QuorumResult.failedQuorum();
        }
    }


}
