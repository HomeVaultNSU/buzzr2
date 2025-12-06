package ru.nsu.ccfit.buzzr.discovery.core.discovery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import ru.nsu.ccfit.buzzr.discovery.config.AgentConfigurationProperties;
import tools.jackson.databind.ObjectMapper;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

@Slf4j
public class InstanceBroadcaster {

    private final DatagramSocket socket;
    private final AgentConfigurationProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public InstanceBroadcaster(AgentConfigurationProperties properties) throws Exception {
        this.socket = new DatagramSocket();
        this.properties = properties;
    }

    @Scheduled(fixedRateString = "${agent.discovery.broadcast-interval-ms:3000}")
    public void broadcast() {
        try {
            Map<String, Object> payload = Map.of("id", properties.getId(), "port", properties.getPort());

            byte[] data = objectMapper.writeValueAsBytes(payload);

            DatagramPacket packet = new DatagramPacket(
                    data,
                    data.length,
                    InetAddress.getByName(AgentConfigurationProperties.BROADCAST_ADDRESS),
                    AgentConfigurationProperties.BROADCAST_PORT
            );

            log.info("[Discovery] :: Broadcasting a discovery packet ... (id = {}, port = {})",
                    properties.getId(), properties.getPort());

            socket.send(packet);

        } catch (Exception e) {
            log.error("Discovery :: broadcast error: {}", e.getMessage(), e);
        }
    }
}
