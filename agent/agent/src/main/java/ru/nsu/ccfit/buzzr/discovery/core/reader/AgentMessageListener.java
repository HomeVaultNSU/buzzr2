package ru.nsu.ccfit.buzzr.discovery.core.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import ru.nsu.ccfit.buzzr.discovery.api.dto.AgentCounterResponse;
import ru.nsu.ccfit.buzzr.discovery.api.dto.AgentMessageDto;
import ru.nsu.ccfit.buzzr.discovery.config.AgentConfigurationProperties;
import ru.nsu.ccfit.buzzr.discovery.core.dao.model.MessageStatus;
import ru.nsu.ccfit.buzzr.discovery.core.discovery.InstanceInfo;
import ru.nsu.ccfit.buzzr.discovery.core.discovery.InstanceRegistry;
import ru.nsu.ccfit.buzzr.discovery.core.quorum.AgentQuorumCounterService;
import ru.nsu.ccfit.buzzr.discovery.core.quorum.AgentQuorumMessageService;
import ru.nsu.ccfit.buzzr.discovery.core.quorum.QuorumResult;
import ru.nsu.ccfit.buzzr.discovery.core.service.AgentCounterService;

@Slf4j
@RequiredArgsConstructor
public class AgentMessageListener {

    private final AgentMessageConsumer messageConsumer;

    private final AgentQuorumMessageService agentQuorumMessageService;

    private final AgentQuorumCounterService agentQuorumCounterService;

    private final InstanceRegistry instanceRegistry;

    private final AgentConfigurationProperties properties;

    private final AgentCounterService counterService;

    @Scheduled(fixedRateString = "${agent.listener.listen-interval-ms:5000}")
    public void listen() {
        instanceRegistry.getInstances().values().forEach(this::checkUpdates);
    }

    private void checkUpdates(InstanceInfo instanceInfo) {

        QuorumResult<AgentCounterResponse> counterResult =
                agentQuorumCounterService.read(instanceInfo.getId(), properties.getId());

        if (!counterResult.isSuccess()) {
            log.warn("can't read counter for srcId={}, dstId={}", instanceInfo.getId(), properties.getId());
            return;
        }

        Long quorumSeq = counterResult.getResponse().getSeq();
        Long localSeq = counterService.getCounter(instanceInfo.getId(), properties.getId()).getSeq();

        log.info("srcId={}, dstId={}, localSeq={}, quorumSeq={}",
                instanceInfo.getId(), properties.getId(), localSeq, quorumSeq);

        for (long seq = localSeq + 1; seq <= quorumSeq; seq++) {
            boolean result = readMessage(instanceInfo.getId(), seq);

            if (!result) {
                break;
            }
        }

    }

    private boolean readMessage(String srcId, long seq) {

        log.info("readMessage srcId={}, seq={} ...", srcId, seq);

        QuorumResult<AgentMessageDto> messageResult =
                agentQuorumMessageService.read(srcId, properties.getId(), seq);

        if (!messageResult.isSuccess()) {
            log.info("readMessage srcId={}, seq={} ... failed", srcId, seq);
            return false;
        }

        AgentMessageDto message = messageResult.getResponse();

        log.info("read a message srcId={}, dstId={}, seq={}, status={}, payload={}",
                message.getSrcId(), message.getDstId(), message.getSeq(), message.getStatus(), message.getPayload());

        if (message.getStatus() == MessageStatus.OUTDATED) {
            log.warn("got an outdated message!");
        }

        messageConsumer.consume(message);

        counterService.saveCounter(srcId, properties.getId(), seq);

        message.setStatus(MessageStatus.PROCESSED);

        agentQuorumMessageService.write(message);

        return true;
    }

}
