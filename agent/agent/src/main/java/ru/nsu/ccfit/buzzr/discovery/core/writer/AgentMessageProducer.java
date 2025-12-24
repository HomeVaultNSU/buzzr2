package ru.nsu.ccfit.buzzr.discovery.core.writer;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import ru.nsu.ccfit.buzzr.discovery.api.dto.AgentMessageDto;
import ru.nsu.ccfit.buzzr.discovery.config.AgentConfigurationProperties;
import ru.nsu.ccfit.buzzr.discovery.core.dao.model.MessageStatus;
import ru.nsu.ccfit.buzzr.discovery.core.quorum.AgentQuorumCounterService;
import ru.nsu.ccfit.buzzr.discovery.core.quorum.AgentQuorumMessageService;
import ru.nsu.ccfit.buzzr.discovery.core.quorum.QuorumResult;
import ru.nsu.ccfit.buzzr.discovery.core.service.AgentCounterService;
import ru.nsu.ccfit.buzzr.discovery.core.service.AgentMessageService;

@Slf4j
@RequiredArgsConstructor
public class AgentMessageProducer {

    private final AgentQuorumMessageService agentQuorumMessageService;

    private final AgentQuorumCounterService agentQuorumCounterService;

    private final AgentConfigurationProperties properties;

    private final AgentCounterService agentCounterService;

    private final AgentMessageService agentMessageService;

    @Synchronized
    public QuorumResult<Void> sendMessage(String dstId, String message) {

        Long seq = agentCounterService.getCounter(properties.getId(), dstId).getSeq() + 1;

        AgentMessageDto messageDto = AgentMessageDto.builder()
                .srcId(properties.getId())
                .dstId(dstId)
                .payload(message)
                .seq(seq)
                .status(MessageStatus.NEW)
                .build();

        agentMessageService.update(messageDto);

        log.info("sending a message: {} ...", messageDto.toString());

        QuorumResult<Void> quorumResult = agentQuorumMessageService.write(messageDto);

        if (quorumResult.isSuccess()) {
            agentCounterService.saveCounter(properties.getId(), dstId, seq);
            agentQuorumCounterService.write(properties.getId(), dstId, seq);
        }

        return quorumResult;
    }

}
