package democlient.eureka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class EurekaController {
    @RequestMapping("/actuator/info")
    public void info(HttpServletRequest request) {
        log.debug(request.getRequestURI());
    }

    @RequestMapping("/actuator/health")
    public void health(HttpServletRequest request) {
        log.debug(request.getRequestURI());
    }
}
