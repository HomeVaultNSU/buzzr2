package ru.nsu.ccfit.buzzr.discovery.core.dao.impl;

import lombok.extern.slf4j.Slf4j;
import ru.nsu.ccfit.buzzr.discovery.config.AgentConfigurationProperties;
import ru.nsu.ccfit.buzzr.discovery.core.dao.AgentCounterDao;
import ru.nsu.ccfit.buzzr.discovery.core.dao.model.AgentCounter;
import ru.nsu.ccfit.buzzr.discovery.util.PersistentConcurrentMap;

import java.io.Serializable;
import java.util.Optional;

@Slf4j
public class DefaultAgentCounterDao implements AgentCounterDao {

    private final PersistentConcurrentMap<Key, AgentCounter> storage;

    public DefaultAgentCounterDao(AgentConfigurationProperties properties) {
        this.storage = new PersistentConcurrentMap<>("agentCounterDao_%s.ser".formatted(properties.getId()));
    }

    @Override
    public Optional<AgentCounter> find(String srcId, String dstId) {
        Optional<AgentCounter> counter = Optional.ofNullable(storage.get(new Key(srcId, dstId)));

        log.info("[DAO] :: (find) counter [srcId={}, dstId={}] ==> {}",
                srcId, dstId, counter.map(cnt -> cnt.getCounter().toString()).orElse("not found"));

        return counter;
    }

    @Override
    public void save(AgentCounter counter) {

        log.info("[DAO] :: (save) counter [srcId={}, dstId={}] <== {}",
                counter.getSrcId(), counter.getDstId(), counter.getCounter());

        storage.put(new Key(counter.getSrcId(), counter.getDstId()), counter);
    }

    private record Key(String srcId, String dstId) implements Serializable {
    }
}