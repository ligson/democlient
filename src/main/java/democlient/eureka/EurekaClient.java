package democlient.eureka;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import democlient.eureka.enums.ActionType;
import democlient.eureka.enums.InstanceStatus;
import democlient.eureka.vo.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
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
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/***
 * statusPageUrlPath: http://${eureka.instance.hostname}:${eureka.instance.non-secure-port}${server.servlet.context-path}/actuator/info
 * #healthCheckUrlPath: http://${eureka.instance.hostname}:${eureka.instance.non-secure-port}${server.servlet.context-path}/actuator/health
 * #home-page-url-path: http://${eureka.instance.ip-address}:31223${server.servlet.context-path}/
 * instance-id: ${eureka.instance.hostname}:${spring.application.name}:${server.port}
 */
@Slf4j
@Component
@Lazy(value = false)
public class EurekaClient implements EnvironmentAware {
    @Value("${eureka.client.serviceUrl.defaultZone:'http://127.0.0.1:8761'}")
    private String defaultZone;
    @Value("${eureka.instance.prefer-ip-address}")
    private boolean preferIpAddress;
    @Value("${eureka.instance.ip-address:}")
    private String ipAddress;
    @Value("${server.servlet.context-path:/}")
    private String contextPath;
    @Value("${server.port:8080}")
    private int port;
    @Value("${eureka.instance.id:}")
    private String instanceId;
    @Value("${eureka.instance.hostname:}")
    private String hostname;
    @Value("${spring.application.name}")
    private String appName;
    @Value("${eureka.instance.home-page-url:}")
    private String homePageUrl;
    /***
     * isReplication是从请求头中获取的字符串，因此可以得知，Eureka在向peer节点发送同步请求时会在请求头中携带自定义的x-netflix-discovery-replication头：
     *
     * public static final String HEADER_REPLICATION = "x-netflix-discovery-replication";
     * 1
     * peer节点通过该请求头来判断当前请求是其它Eureka节点的同步请求还是服务的注册请求。我们假定当前请求是上一个Eureka发来的同步请求，那么这里第二个参数的值应该为true。
     */
    public static final String HEADER_REPLICATION = "x-netflix-discovery-replication";


    //#Eureka客户端向服务端发送心跳的时间间隔，单位为秒（客户端告诉服务端自己会按照该规则）
    //eureka.instance.lease-renewal-interval-in-seconds=90
    @Value("${eureka.instance.lease-renewal-interval-in-seconds:90}")
    private int leaseRenewalIntervalInSeconds;
    //      #Eureka服务端在收到最后一次心跳之后等待的时间上限，单位为秒，超过则剔除（客户端告诉服务端按照此规则等待自己）
    //eureka.instance.lease-expiration-duration-in-seconds=30
    @Value("${eureka.instance.lease-expiration-duration-in-seconds:30}")
    private int leaseExpirationDurationInSeconds;
    private static boolean init = false;
    private ObjectMapper objectMapper = null;
    private Environment environment;
    private static ScheduledExecutorService scheduledExecutorService;
    private static long lastDirtyTimestamp = new Date().getTime();
    private static long lastUpdatedTimestamp = new Date().getTime();

    @PostConstruct
    public void init() throws Exception {

        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        }
        if (init) {
            return;
        } else {
            init = true;
        }
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        log.error("000000000000000000000000");


        String statusPageUrlPath = homePageUrl + "/actuator/info";
        String healthCheckUrlPath = homePageUrl + "/actuator/health";
        RegisterInfo registerInfo = new RegisterInfo();


        InstanceInfo instanceInfo = new InstanceInfo();
        instanceInfo.setHostName(hostname);
        instanceInfo.setIpAddr(ipAddress);
        instanceInfo.setHomePageUrl(homePageUrl);
        instanceInfo.setStatusPageUrl(statusPageUrlPath);
        instanceInfo.setHealthCheckUrl(healthCheckUrlPath);
        instanceInfo.setStatus(InstanceStatus.UP);
        instanceInfo.setOverriddenStatus(InstanceStatus.UNKNOWN);
        instanceInfo.setInstanceId(instanceId);
        instanceInfo.setActionType(ActionType.ADDED);
        instanceInfo.setApp(appName);
        instanceInfo.setLastDirtyTimestamp(lastDirtyTimestamp);
        instanceInfo.setDataCenterInfo(new MyDataCenterInfo());
        LeaseInfo leaseInfo = new LeaseInfo();
        leaseInfo.setRenewalIntervalInSecs(leaseExpirationDurationInSeconds);
        leaseInfo.setDurationInSecs(leaseRenewalIntervalInSeconds);
        instanceInfo.setLeaseInfo(leaseInfo);
        registerInfo.setInstance(instanceInfo);
        RestTemplate restTemplate = new RestTemplate();
        String registerUrl = defaultZone + "apps/" + appName;

        String body = objectMapper.writeValueAsString(registerInfo);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        RequestEntity requestEntity = new RequestEntity<>(body, headers, HttpMethod.POST, new URI(registerUrl));
        log.debug("register----url:{}", registerUrl);

        log.debug("register---body:{}", body);
        ResponseEntity<String> result = restTemplate.exchange(requestEntity, String.class);
        log.debug("register---http code:{} result:{}", result.getStatusCodeValue(), result.getBody());

        //leaseExpirationDurationInSeconds
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                updateStatus();
            } catch (Exception e) {
                log.error("更新失败，原因:{}", e.getMessage(), e);
            }

        }, 10L, 10L, TimeUnit.SECONDS);
    }

    //PUT /eureka/apps/FMCP/192.168.1.100:fmcp:8988?status=UP&lastDirtyTimestamp=1564223484618 HTTP/1.1
    @SneakyThrows
    private void updateStatus() {
        RestTemplate restTemplate = new RestTemplate();
        long updateDateTime = new Date().getTime();
        String putUrl = defaultZone + "apps/" + appName.toUpperCase() + "/" + instanceId + "?status=UP&lastDirtyTimestamp=" + lastDirtyTimestamp;
        lastDirtyTimestamp = updateDateTime;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add(HEADER_REPLICATION, "false");
        RequestEntity requestEntity = new RequestEntity<>(null, headers, HttpMethod.PUT, new URI(putUrl));
        log.debug("put----url:{}", putUrl);
        ResponseEntity<String> putResult = restTemplate.exchange(requestEntity, String.class);
        log.debug("更新结果,httpCode:{},返回信息:{}", putResult.getStatusCodeValue(), putResult.getBody());
    }


    @PreDestroy
    public void destory() {

    }

    @SneakyThrows
    private void processPropFile(Environment environment) {
        ConfigurableEnvironment c = (ConfigurableEnvironment) environment;
        MutablePropertySources m = c.getPropertySources();
        Properties p = new Properties();
        if (StringUtils.isBlank(hostname)) {
            hostname = InetAddress.getLocalHost().getHostName();
        }
        if (StringUtils.isBlank(ipAddress)) {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        }
        if (preferIpAddress) {
            if (StringUtils.isBlank(homePageUrl)) {
                homePageUrl = "http://" + ipAddress + ":" + port + contextPath;
            }
            if (StringUtils.isBlank(instanceId)) {
                instanceId = ipAddress + ":" + appName + ":" + port;
            }
        } else {
            if (StringUtils.isBlank(instanceId)) {
                instanceId = hostname + ":" + appName + ":" + port;
            }
            if (StringUtils.isBlank(homePageUrl)) {
                homePageUrl = "http://" + hostname + ":" + port + contextPath;
            }
        }

        p.put("eureka.instance.ip-address", ipAddress);
        p.put("eureka.instance.hostname", hostname);
        p.put("server.port", port);
        p.put("spring.application.name", appName);
        p.put("server.servlet.context-path", contextPath);
        p.put("eureka.instance.id", instanceId);
        p.put("eureka.instance.home-page-url", homePageUrl);
        m.addFirst(new PropertiesPropertySource("defaultProperties", p));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        processPropFile(environment);
    }

    public String loadBalance(String serviceId) {
        String getUrl = defaultZone + "apps/" + serviceId.toUpperCase();
        RestTemplate restTemplate = new RestTemplate();
        Apps apps = restTemplate.getForObject(getUrl, Apps.class);
        if (apps != null && apps.getApplication() != null) {
            List<InstanceInfo> instances = apps.getApplication().getInstance();
            return instances.get(0).getHomePageUrl();
        } else {
            return null;
        }
    }
}
