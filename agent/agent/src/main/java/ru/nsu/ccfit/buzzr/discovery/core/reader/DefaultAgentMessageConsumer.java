package ru.nsu.ccfit.buzzr.discovery.core.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.nsu.ccfit.buzzr.discovery.api.dto.AgentMessageDto;

@Slf4j
@RequiredArgsConstructor
public class DefaultAgentMessageConsumer implements AgentMessageConsumer {

    @Override
    public void consume(AgentMessageDto message) {
        log.info("consuming a message: {} ... done!", message.toString());
    }

}
