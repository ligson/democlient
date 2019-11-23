package democlient.eureka.vo;

import democlient.eureka.enums.InstanceStatus;
import lombok.Data;

@Data
public class StatusWrapper {
    private InstanceStatus status;
}
