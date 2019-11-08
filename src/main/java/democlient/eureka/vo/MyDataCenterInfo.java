package democlient.eureka.vo;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import democlient.eureka.enums.DataCenterInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class MyDataCenterInfo implements DataCenterInfo {
    @Override
    public Name getName() {
        return Name.MyOwn;
    }
}
