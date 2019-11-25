package democlient.eureka;

import democlient.eureka.enums.InstanceStatus;
import democlient.eureka.vo.StatusWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class EurekaController {
    @Autowired
    private EurekaRestTemplate eurekaRestTemplate;

    @RequestMapping("/actuator/info")
    public Map info(HttpServletRequest request) {
        log.debug(request.getRequestURI());
        return new HashMap();
    }

    @RequestMapping("/actuator/health")
    public StatusWrapper health(HttpServletRequest request) {
        log.debug(request.getRequestURI());
        StatusWrapper statusWrapper = new StatusWrapper();
        statusWrapper.setStatus(InstanceStatus.UP);
        return statusWrapper;
    }

    @GetMapping("/api/function")
    public Object getAppFunction() {
        return eurekaRestTemplate.getRestTemplate().getForObject("http://crux-uc/crux-uc/api/user", Object.class);
    }
}
