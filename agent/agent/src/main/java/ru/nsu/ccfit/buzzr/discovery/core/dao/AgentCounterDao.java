package ru.nsu.ccfit.buzzr.discovery.core.dao;

import ru.nsu.ccfit.buzzr.discovery.core.dao.model.AgentCounter;

import java.util.Optional;

public interface AgentCounterDao {

    Optional<AgentCounter> find(String srcId, String dstId);

    void save(AgentCounter agentCounter);

}
