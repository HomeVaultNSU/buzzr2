package ru.nsu.ccfit.buzzr.discovery.core.quorum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryTemplate;
import ru.nsu.ccfit.buzzr.discovery.api.client.AgentClient;
import ru.nsu.ccfit.buzzr.discovery.api.client.AgentClientFactory;
import ru.nsu.ccfit.buzzr.discovery.config.AgentConfigurationProperties;
import ru.nsu.ccfit.buzzr.discovery.core.discovery.InstanceInfo;
import ru.nsu.ccfit.buzzr.discovery.core.discovery.InstanceRegistry;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public abstract class AgentQuorumBaseService {

    protected final InstanceRegistry instanceRegistry;

    protected final AgentConfigurationProperties properties;

    private final RetryTemplate retryTemplate;

    protected static final Function<InstanceInfo, AgentClient> CLIENT_FACTORY =
            instanceInfo -> AgentClientFactory.createRestClient(instanceInfo.getSender());

    protected <T> QuorumResult<Map<InstanceInfo, T>> sendAsyncUntilQuorum(
            Collection<InstanceInfo> instances,
            int quorum,
            Function<InstanceInfo, T> requester
    ) {
        CountDownLatch latch = new CountDownLatch(quorum);
        ConcurrentHashMap<InstanceInfo, T> results = new ConcurrentHashMap<>();

        instances.forEach(instance -> CompletableFuture.runAsync(() -> {
            try {
                T result = retryTemplate.execute(() -> requester.apply(instance));

                results.put(instance, result);
                latch.countDown();

            } catch (RetryException e) {
                log.warn("Failed request to {}: {}. Retry count: {}", instance.getId(), e.getCause().getMessage(), e.getRetryCount());

            } catch (Exception e) {
                log.warn("Internal server error on request to {}: {}", instance.getId(), e.getCause().getMessage());
            }
        }));

        try {
            latch.await(properties.getRequestTimeoutMs(), TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("Quorum result: {} / {}", results.size(), quorum);

        if (results.size() < quorum) {
            return QuorumResult.failedQuorum();
        }

        return new QuorumResult<>(QuorumResult.Status.SUCCESS, results);
    }

}
