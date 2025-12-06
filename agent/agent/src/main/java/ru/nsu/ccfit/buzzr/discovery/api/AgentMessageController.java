package ru.nsu.ccfit.buzzr.discovery.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nsu.ccfit.buzzr.discovery.api.dto.AgentMessageDto;
import ru.nsu.ccfit.buzzr.discovery.core.service.AgentMessageService;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class AgentMessageController {

    private final AgentMessageService messageService;

    @GetMapping("/{srcId}/{dstId}/{seq}")
    public ResponseEntity<AgentMessageDto> get(@PathVariable String srcId, @PathVariable String dstId, @PathVariable Long seq) {
        return messageService.get(srcId, dstId, seq)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/update")
    public ResponseEntity<String> update(@RequestBody AgentMessageDto dto) {
        messageService.update(dto);
        return ResponseEntity.ok("OK");
    }
}