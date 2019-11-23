package democlient.eureka;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import java.net.InetAddress;
import java.util.Properties;

@Slf4j
public class CruxPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements InitializingBean {
    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        logger.debug("---");
    }

    @Override
    @SneakyThrows
    protected String resolvePlaceholder(String placeholder, Properties props) {
        String value = super.resolvePlaceholder(placeholder, props);
        if (StringUtils.isBlank(value)) {
            if ("eureka.instance.ip-address".equals(placeholder)) {
                return InetAddress.getLocalHost().getHostAddress();
            } else if ("eureka.instance.hostname".equals(placeholder)) {
                return InetAddress.getLocalHost().getHostName();
            } else if ("server.port".equals(placeholder)) {

            } else if ("spring.application.name".equals(placeholder)) {

            } else if (("server.servlet.context-path").equals(placeholder)) {

            }
        }
        return value;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        setLocation(new ClassPathResource("eureka-client.properties"));
    }
}
