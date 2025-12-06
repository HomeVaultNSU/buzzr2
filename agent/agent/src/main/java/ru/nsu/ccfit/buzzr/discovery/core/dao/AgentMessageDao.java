package ru.nsu.ccfit.buzzr.discovery.core.dao;

import ru.nsu.ccfit.buzzr.discovery.api.dto.AgentMessageDto;

import java.util.Optional;

public interface AgentMessageDao {

    Optional<AgentMessageDto> getMessage(String srcId, String dstId, Long seq);

    void saveMessage(AgentMessageDto message);

}