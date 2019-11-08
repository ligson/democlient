package democlient.eureka.vo;

import lombok.Data;

@Data
public class LeaseInfo {
    public static final int DEFAULT_LEASE_RENEWAL_INTERVAL = 30;
    public static final int DEFAULT_LEASE_DURATION = 90;

    // Client settings
    private int renewalIntervalInSecs = DEFAULT_LEASE_RENEWAL_INTERVAL;
    private int durationInSecs = DEFAULT_LEASE_DURATION;

    // Server populated
    private long registrationTimestamp;
    private long lastRenewalTimestamp;
    private long evictionTimestamp;
    private long serviceUpTimestamp;
}
