package ru.nsu.ccfit.buzzr.discovery.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.nsu.ccfit.buzzr.discovery.api.dto.AgentMessageDto;
import ru.nsu.ccfit.buzzr.discovery.core.dao.AgentMessageDao;
import ru.nsu.ccfit.buzzr.discovery.core.dao.model.MessageStatus;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class AgentMessageService {

    private final AgentMessageDao messageDao;

    public Optional<AgentMessageDto> get(String srcId, String dstId, long seq) {
        return messageDao.getMessage(srcId, dstId, seq);
    }

    public void update(AgentMessageDto dto) {

        log.info("got message `{}`", dto);

        boolean shouldSave = get(dto.getSrcId(), dto.getDstId(), dto.getSeq())
                .map(AgentMessageDto::getStatus)
                .map(oldStatus -> MessageStatus.COMPARATOR.compare(dto.getStatus(), oldStatus) > 0)
                .orElse(true);

        if (shouldSave) {
            messageDao.saveMessage(dto);

        } else {
            log.info("skip update: new status has lower or equal priority");
        }
    }
}
