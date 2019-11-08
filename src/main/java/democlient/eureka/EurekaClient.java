package democlient.eureka;

import democlient.eureka.enums.ActionType;
import democlient.eureka.enums.DataCenterInfo;
import democlient.eureka.vo.InstanceInfo;
import democlient.eureka.vo.MyDataCenterInfo;
import democlient.eureka.vo.RegisterInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.URI;
import java.util.HashMap;

/***
 * statusPageUrlPath: http://${eureka.instance.hostname}:${eureka.instance.non-secure-port}${server.servlet.context-path}/actuator/info
 * #healthCheckUrlPath: http://${eureka.instance.hostname}:${eureka.instance.non-secure-port}${server.servlet.context-path}/actuator/health
 * #home-page-url-path: http://${eureka.instance.ip-address}:31223${server.servlet.context-path}/
 * instance-id: ${eureka.instance.hostname}:${spring.application.name}:${server.port}
 */
@Slf4j
@Component
@Lazy(value = false)
public class EurekaClient {
    @Value("${eureka.defaultZone}")
    private String defaultZone;
    @Value("${eureka.prefer-ip-address}")
    private boolean preferIpAddress;
    @Value("${eureka.context-path}")
    private String contextPath;
    @Value("${eureka.port}")
    private int port;
    @Value("${eureka.application.name}")
    private String appName;
    private static boolean init = false;

    @PostConstruct
    public void init() throws Exception {
        if (init) {
            return;
        } else {
            init = true;
        }
        log.error("000000000000000000000000");
        String baseUrl;
        String instanceId;
        if (preferIpAddress) {
            String address = InetAddress.getLocalHost().getHostAddress();
            baseUrl = "http://" + address + ":" + port + contextPath;
            instanceId = address + ":" + appName + ":" + port;
        } else {
            String address = InetAddress.getLocalHost().getHostName();
            baseUrl = "http://" + address + ":" + port + contextPath;
            instanceId = address + ":" + appName + ":" + port;
        }
        String statusPageUrlPath = baseUrl + "/actuator/info";
        String healthCheckUrlPath = baseUrl + "/actuator/health";
        RegisterInfo registerInfo = new RegisterInfo();


        InstanceInfo instanceInfo = new InstanceInfo();
        instanceInfo.setHostName(InetAddress.getLocalHost().getHostName());
        instanceInfo.setIpAddr(InetAddress.getLocalHost().getHostAddress());
        instanceInfo.setHomePageUrl(baseUrl);
        instanceInfo.setStatusPageUrl(statusPageUrlPath);
        instanceInfo.setHealthCheckUrl(healthCheckUrlPath);
        instanceInfo.setInstanceId(instanceId);
        instanceInfo.setActionType(ActionType.ADDED);
        instanceInfo.setApp(appName);
        instanceInfo.setDataCenterInfo(new MyDataCenterInfo());
        registerInfo.setInstance(instanceInfo);
        RestTemplate restTemplate = new RestTemplate();
        String registerUrl = defaultZone + "apps/" + appName;

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        RequestEntity<RegisterInfo> requestEntity = new RequestEntity<>(registerInfo, headers, HttpMethod.POST, new URI(registerUrl));

        ResponseEntity<String> result = restTemplate.exchange(requestEntity, String.class);
        result.getBody();
        log.debug(result.toString());
    }

    @PreDestroy
    public void destory() {

    }

}
