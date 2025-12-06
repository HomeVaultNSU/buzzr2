package ru.nsu.ccfit.buzzr.discovery.api.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import ru.nsu.ccfit.buzzr.discovery.api.dto.AgentCounterResponse;
import ru.nsu.ccfit.buzzr.discovery.api.dto.AgentMessageDto;

@HttpExchange("/api")
public interface AgentClient {

    @GetExchange("/counters/{srcId}/{dstId}")
    AgentCounterResponse getCounter(@PathVariable String srcId, @PathVariable String dstId);

    @PostExchange("/counters/{srcId}/{dstId}/{seq}")
    String setCounter(@PathVariable String srcId, @PathVariable String dstId, @PathVariable Long seq);

    @GetExchange("/messages/{srcId}/{dstId}/{seq}")
    AgentMessageDto getMessage(@PathVariable String srcId, @PathVariable String dstId, @PathVariable Long seq);

    @PostExchange("/messages/update")
    String updateMessage(@RequestBody AgentMessageDto dto);
}