package democlient.eureka.vo;

import democlient.eureka.enums.ActionType;
import democlient.eureka.enums.DataCenterInfo;
import democlient.eureka.enums.InstanceStatus;
import lombok.Data;

import java.util.HashMap;

@Data
public class InstanceInfo {
    private String instanceId;
    private String app;
    private String appGroupName;
    private String ipAddr;
    private String sid;
    private PortWrapper port;
    private PortWrapper securePort;
    private String vipAddress;
    private int countryId;
    private String hostName;
    private InstanceStatus status;
    private InstanceStatus overriddenStatus;
    private InstanceStatus overriddenStatusAlt;
    private LeaseInfo leaseInfo;
    private Boolean isCoordinatingDiscoveryServer;

    private HashMap<String, String> metadata;
    private Long lastUpdatedTimestamp;
    private Long lastDirtyTimestamp;
    private ActionType actionType;
    private String asgName;
    private String homePageUrl;
    private String statusPageUrl;
    private String healthCheckUrl;
    private DataCenterInfo dataCenterInfo;
}
