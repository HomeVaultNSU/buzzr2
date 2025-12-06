package ru.nsu.ccfit.buzzr.discovery.api.client;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AgentClientFactory {

    private static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(30);
    private static final ConcurrentMap<InetSocketAddress, Object> CLIENT_CACHE = new ConcurrentHashMap<>();

    public static AgentClient createRestClient(InetSocketAddress address) {
        return (AgentClient) CLIENT_CACHE.computeIfAbsent(
                address,
                AgentClientFactory::createRestClientInternal
        );
    }

    private static AgentClient createRestClientInternal(InetSocketAddress address) {
        String url = "http://" + address.getHostString() + ":" + address.getPort();
        return createRestClient(url, AgentClient.class);
    }

    private static <T> T createRestClient(String url, Class<T> restClientClass) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(DEFAULT_READ_TIMEOUT);

        RestClient restClient = RestClient.builder()
                .baseUrl(url)
                .requestFactory(requestFactory)
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(restClientClass);
    }
}