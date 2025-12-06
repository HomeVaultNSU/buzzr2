package ru.nsu.ccfit.buzzr.discovery.core.dao.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentMessage {

    private String srcId;

    private String dstId;

    private Long seq;

    private String payload; // В данной реализации сообщение - строка

    private MessageStatus status;

}
