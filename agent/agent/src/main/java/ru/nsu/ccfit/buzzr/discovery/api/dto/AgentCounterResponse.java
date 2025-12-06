package ru.nsu.ccfit.buzzr.discovery.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentCounterResponse {

    private String srcId;

    private String dstId;

    private Long seq;

}