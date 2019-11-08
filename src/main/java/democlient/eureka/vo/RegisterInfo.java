package democlient.eureka.vo;

import lombok.Data;

//{"instance":{"instanceId":"sdf","hostName":null,"app":"APPNAME","ipAddr":null,"status":"UP","overriddenStatus":"UNKNOWN","port":{"$":7001,"@enabled":"true"},"securePort":{"$":7002,"@enabled":"false"},"countryId":1,"metadata":{"@class":"java.util.Collections$EmptyMap"},"healthCheckUrl":"http://null:70011","isCoordinatingDiscoveryServer":"false","lastUpdatedTimestamp":"1559731857320","lastDirtyTimestamp":"1559731857320"}}
@Data
public class RegisterInfo {
    private InstanceInfo instance;

}
