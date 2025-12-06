package ru.nsu.ccfit.buzzr.discovery.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nsu.ccfit.buzzr.discovery.core.dao.model.MessageStatus;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentMessageDto implements Serializable {

    private long seq;

    private String srcId;

    private String dstId;

    private String payload;

    private MessageStatus status;
}