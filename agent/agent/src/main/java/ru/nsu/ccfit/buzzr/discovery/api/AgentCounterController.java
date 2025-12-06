package ru.nsu.ccfit.buzzr.discovery.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.ccfit.buzzr.discovery.api.dto.AgentCounterResponse;
import ru.nsu.ccfit.buzzr.discovery.core.service.AgentCounterService;

@RestController
@RequestMapping("/api/counters")
@RequiredArgsConstructor
public class AgentCounterController {

    private final AgentCounterService counterService;

    @GetMapping("/{srcId}/{dstId}")
    public ResponseEntity<AgentCounterResponse> read(@PathVariable String srcId, @PathVariable String dstId) {
        return ResponseEntity.ok(counterService.getCounter(srcId, dstId));
    }

    @PostMapping("/{srcId}/{dstId}/{seq}")
    public ResponseEntity<String> write(@PathVariable String srcId, @PathVariable String dstId, @PathVariable Long seq) {
        counterService.saveCounter(srcId, dstId, seq);
        return ResponseEntity.ok("OK");
    }

}