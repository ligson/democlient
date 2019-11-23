package democlient.eureka.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import democlient.eureka.enums.DataCenterInfo;

//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class MyDataCenterInfo implements DataCenterInfo {
    @JsonProperty(value = "@class")
    private String clazz = "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo";

    @Override
    public Name getName() {
        return Name.MyOwn;
    }
}
