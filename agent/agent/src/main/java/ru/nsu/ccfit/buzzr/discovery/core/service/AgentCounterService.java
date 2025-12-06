package ru.nsu.ccfit.buzzr.discovery.core.service;

import lombok.RequiredArgsConstructor;
import ru.nsu.ccfit.buzzr.discovery.api.dto.AgentCounterResponse;
import ru.nsu.ccfit.buzzr.discovery.core.dao.AgentCounterDao;
import ru.nsu.ccfit.buzzr.discovery.core.dao.model.AgentCounter;

import java.util.Optional;

@RequiredArgsConstructor
public class AgentCounterService {

    private final AgentCounterDao agentCounterDao;

    public AgentCounterResponse getCounter(String srcId, String dstId) {
        Optional<AgentCounterResponse> counterResponse = agentCounterDao.find(srcId, dstId)
                .map(cnt -> new AgentCounterResponse(srcId, dstId, cnt.getCounter()));

        return counterResponse.orElseGet(() -> {
            saveCounter(srcId, dstId, 0L);
            return getCounter(srcId, dstId);
        });
    }

    public void saveCounter(String srcId, String dstId, Long seq) {
        agentCounterDao.save(new AgentCounter(srcId, dstId, seq));
    }

}
