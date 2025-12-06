package ru.nsu.ccfit.buzzr.discovery.core.reader;

import ru.nsu.ccfit.buzzr.discovery.api.dto.AgentMessageDto;

public interface AgentMessageConsumer {

    void consume(AgentMessageDto message);

}
