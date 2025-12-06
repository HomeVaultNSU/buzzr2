package ru.nsu.ccfit.buzzr.discovery.core.discovery;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import ru.nsu.ccfit.buzzr.discovery.config.AgentConfigurationProperties;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class InstanceRegistry {

    private final AgentConfigurationProperties properties;

    private final ConcurrentHashMap<String, InstanceInfo> instances = new ConcurrentHashMap<>();

    public void updateInstance(InstanceInfo instanceInfo) {
        if (instanceInfo.getId().equals(properties.getId())) {
            return;
        }

        instances.compute(instanceInfo.getId(), (_, v) -> {
            if (v == null) {
                return instanceInfo;

            } else {
                v.update();
                return v;
            }
        });
    }

    public Map<String, InstanceInfo> getInstances() {
        return Collections.unmodifiableMap(instances);
    }

    @Scheduled(fixedRateString = "${agent.discovery.health-check-ms:10000}")
    public void cleanup() {
        Instant thresholdInstant = Instant.now().minusMillis(properties.getDiscovery().getThresholdMs());
        instances.entrySet().removeIf(e -> e.getValue().getLastSeen().isBefore(thresholdInstant));
    }

}
