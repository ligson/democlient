package democlient.eureka;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class EurekaRestTemplate {
    private RestTemplate restTemplate;
    @Autowired
    private EurekaClient eurekaClient;

    public RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            ClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            restTemplate = new RestTemplate(clientHttpRequestFactory);
            List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
            interceptors.add(new ClientHttpRequestInterceptor() {
                @Override
                public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                    String serviceId = request.getURI().getHost();
                    String address = eurekaClient.loadBalance(serviceId);
                    if (StringUtils.isBlank(address)) {
                        throw new RuntimeException(String.format("%s找不到对应实例", serviceId));
                    }
                    HttpRequest req = new HttpRequest() {

                        @Override
                        public HttpHeaders getHeaders() {
                            return request.getHeaders();
                        }

                        @Override
                        public String getMethodValue() {
                            return request.getMethodValue();
                        }

                        @Override
                        public URI getURI() {
                            URI uri = request.getURI();
                            String url = address.endsWith("/") ? address.substring(0, address.length() - 1) + uri.getPath() : address + uri.getPath();
                            try {
                                return new URI(url);
                            } catch (URISyntaxException e) {
                                //e.printStackTrace();
                                log.error(e.getMessage(), e);
                            }
                            return null;
                        }
                    };
                    return execution.execute(req, body);
                }
            });
            restTemplate.setInterceptors(interceptors);
        }
        return restTemplate;
    }

}
