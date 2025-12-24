package ru.nsu.ccfit.buzzr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.nsu.ccfit.buzzr.discovery.core.writer.AgentMessageProducer;

import java.util.UUID;

@ConditionalOnProperty(name = "agent.id", havingValue = "agent-1")
@Slf4j
@Component
@RequiredArgsConstructor
public class Demo {

    private final AgentMessageProducer agentMessageProducer;

    @Scheduled(fixedRate = 8_000, initialDelay = 10_000)
    private void testWrite() {
        String targetId = "agent-2";

        agentMessageProducer.sendMessage(targetId, UUID.randomUUID().toString());

        log.info("======= ======= ======= ======= ======= =======");
    }

}
