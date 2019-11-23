package democlient.eureka.vo;

import lombok.Data;

/***
 * leaseInfo:
 * durationInSecs: 90
 * evictionTimestamp: 0
 * lastRenewalTimestamp: 0
 * registrationTimestamp: 0
 * renewalIntervalInSecs: 30
 * serviceUpTimestamp: 0
 */
@Data
public class LeaseInfo {
    public static final int DEFAULT_LEASE_RENEWAL_INTERVAL = 30;
    public static final int DEFAULT_LEASE_DURATION = 90;

    // Client settings
    private Integer renewalIntervalInSecs = DEFAULT_LEASE_RENEWAL_INTERVAL;
    private Integer durationInSecs = DEFAULT_LEASE_DURATION;

    // Server populated
    private Integer registrationTimestamp;
    private Integer lastRenewalTimestamp;
    private Integer evictionTimestamp;
    private Integer serviceUpTimestamp;
}
