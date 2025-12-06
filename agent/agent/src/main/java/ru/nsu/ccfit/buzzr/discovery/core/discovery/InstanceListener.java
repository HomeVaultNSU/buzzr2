package ru.nsu.ccfit.buzzr.discovery.core.discovery;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import ru.nsu.ccfit.buzzr.discovery.util.NetworkUtils;
import tools.jackson.databind.ObjectMapper;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static ru.nsu.ccfit.buzzr.discovery.config.AgentConfigurationProperties.BROADCAST_PORT;

@Slf4j
public class InstanceListener {

    public static final int DATAGRAM_PACKET_BUFFER_SIZE = 1024;

    private final InstanceRegistry registry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public InstanceListener(InstanceRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    private void init() {
        Thread thread = new Thread(this::listen, "discovery-listener");
        thread.setDaemon(true);
        thread.start();
    }

    private void listen() {
        try {
            DatagramChannel channel = DatagramChannel.open();
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            channel.setOption(StandardSocketOptions.SO_REUSEPORT, true);
            channel.bind(new InetSocketAddress(BROADCAST_PORT));

            ByteBuffer buf = ByteBuffer.allocate(DATAGRAM_PACKET_BUFFER_SIZE);

            while (true) {
                buf.clear();
                InetSocketAddress sender = (InetSocketAddress) channel.receive(buf);

                buf.flip();
                String json = StandardCharsets.UTF_8.decode(buf).toString();

                try {
                    Map<String, Object> payload = objectMapper.readValue(json, Map.class);
                    String id = (String) payload.get("id");
                    Integer port = (Integer) payload.get("port");

                    InstanceInfo info = new InstanceInfo(NetworkUtils.buildAddress(sender, port), id, port);

                    log.info("[Discovery] :: Got a discovery packet from agent id = {} port = {} from {}", id, port, sender);

                    registry.updateInstance(info);

                } catch (Exception ex) {
                    log.warn("Failed to parse discovery JSON from {}: {}", sender, json, ex);
                }
            }

        } catch (Exception e) {
            log.error("Discovery :: listener error: {}", e.getMessage(), e);
        }
    }
}

