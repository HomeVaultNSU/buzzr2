package ru.nsu.ccfit.buzzr.discovery.core.dao.impl;

import ru.nsu.ccfit.buzzr.discovery.api.dto.AgentMessageDto;
import ru.nsu.ccfit.buzzr.discovery.config.AgentConfigurationProperties;
import ru.nsu.ccfit.buzzr.discovery.core.dao.AgentMessageDao;
import ru.nsu.ccfit.buzzr.discovery.util.PersistentConcurrentMap;

import java.io.Serializable;
import java.util.Optional;

public class DefaultAgentMessageDao implements AgentMessageDao {

    private final PersistentConcurrentMap<Key, AgentMessageDto> storage;

    public DefaultAgentMessageDao(AgentConfigurationProperties properties) {
        this.storage = new PersistentConcurrentMap<>("agentMessageDao_%s.ser".formatted(properties.getId()));
    }

    @Override
    public Optional<AgentMessageDto> getMessage(String srcId, String dstId, Long seq) {
        return Optional.ofNullable(storage.get(new Key(srcId, dstId, seq)));
    }

    @Override
    public void saveMessage(AgentMessageDto message) {
        storage.put(new Key(message.getSrcId(), message.getDstId(), message.getSeq()), message);
    }

    private record Key(String srcId, String dstId, Long seq) implements Serializable {
    }
}