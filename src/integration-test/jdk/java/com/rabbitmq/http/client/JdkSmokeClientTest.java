package com.rabbitmq.http.client;

import com.rabbitmq.http.client.domain.VhostInfo;
import org.junit.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class JdkSmokeClientTest {

    static final String DEFAULT_USERNAME = "guest";
    static final String DEFAULT_PASSWORD = "guest";

    @Test
    public void should_connect_to_rabbit_using_http_client() throws MalformedURLException, URISyntaxException {
        String url = "http://" + DEFAULT_USERNAME + ":" + DEFAULT_PASSWORD + "@127.0.0.1:" + managementPort() + "/api/";

        Client client = new Client(new ClientParameters()
                .url(url)
                .restTemplateConfigurator(ctx -> {
                    RestTemplate restTemplate = ctx.getRestTemplate();
                    restTemplate.setInterceptors(singletonList(new BasicAuthenticationInterceptor(
                            ctx.getClientParameters().getUsername(), ctx.getClientParameters().getPassword()
                    )));
                    restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
                    return restTemplate;
                })
        );

        List<VhostInfo> vhosts = client.getVhosts();
        assertThat(vhosts, not(emptyList()));
        assertThat(vhosts.get(0).getName(), equalTo("/"));
    }

    static int managementPort() {
        return System.getProperty("rabbitmq.management.port") == null ?
                15672 :
                Integer.valueOf(System.getProperty("rabbitmq.management.port"));
    }

}