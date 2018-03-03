package com.github.yavu3.main;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

@SpringBootApplication
@EnableWebFlux
public class Application {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    @Bean
    public ApplicationRunner runner() {
        return args -> {

            SslContext sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();

            ReactorClientHttpConnector connector = new ReactorClientHttpConnector(
                    options -> {
                        if (args.containsOption("fiddler")) {
                            options.httpProxy(addressSpec -> addressSpec.address(new InetSocketAddress("127.0.0.1", 8888)));
                        }
                        options.sslContext(sslContext);
                    }
            );

            WebClient webClient = WebClient.builder()
                    .baseUrl("https://httpbin.org")
                    //.defaultHeader(HttpHeaders.CONTENT_TYPE, GITHUB_V3_MIME_TYPE)
                    //.defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT)
                    /*.filter(ExchangeFilterFunctions
                            .basicAuthentication(appProperties.getGithub().getUsername(),
                                    appProperties.getGithub().getToken()))
                    .filter(logRequest())*/
                    .clientConnector(connector)
                    .build();

            WebClient.RequestHeadersSpec request1 = webClient
                    .method(HttpMethod.GET)
                    .uri("/get");


            Mono<String> result = request1.retrieve()
                    .bodyToMono(String.class);
            String response = result.block();

            System.out.println(response);

            String api = "http://stockmarket.streamdata.io/prices";
            String token = "cdd372d9437d79622aee342fbbf3bdc955639e1a";


/*
            Mono<ClientResponse> monoCR = request1.exchange();
            ClientResponse cr = monoCR.block();
            Mono<String> monoS = cr.bodyToMono(String.class);
            String s = monoS.block();
*/
            /*String s1 = request1.exchange()
                    .block()
                    .bodyToMono(String.class)
                    .block();*/

            //ClientResponse cr = request1.exchange().block();

            //System.out.println(s1);

        };
    }
}
