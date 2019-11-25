package democlient.eureka.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Application {
    private List<InstanceInfo> instance = new ArrayList<>();
    private String name;
}
