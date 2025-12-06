package ru.nsu.ccfit.buzzr.discovery.core.discovery;

import lombok.Getter;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@Getter
public class InstanceInfo {

    private final InetSocketAddress sender;

    private final String id;

    private final Integer tcpPort;

    private final AtomicLong successCount = new AtomicLong();

    private Instant lastSeen;

    public InstanceInfo(InetSocketAddress sender, String id, Integer tcpPort) {
        this.sender = sender;
        this.id = id;
        this.tcpPort = tcpPort;
        this.lastSeen = Instant.now();
    }

    public void update() {
        lastSeen = Instant.now();
        successCount.incrementAndGet();
    }
}
