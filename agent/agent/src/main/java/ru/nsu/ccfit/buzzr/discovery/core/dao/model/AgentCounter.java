package ru.nsu.ccfit.buzzr.discovery.core.dao.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentCounter implements Serializable {

    private String srcId;

    private String dstId;

    private Long counter;

}