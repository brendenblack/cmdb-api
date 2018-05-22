package ca.bc.gov.nrs.infra.cmdb.features.jenkins;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
public class BuildInfo
{
    private String jobClass;
    private int duration;
    private String displayName;
    private String url;
    private int queueId;
    private int timestamp;
    private String builtOn;
    private String triggeredBy;
    private String sourceBranch;
    private String targetBranch;
    private String sha1;
    private Set<Promotion> promotions = new HashSet<>();
    private int number;

    public void addPromotion(String environment, int number, String url)
    {
        this.promotions.add(new Promotion(environment, number, url));
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Promotion
    {
        private String environment;
        private int number;
        private String url;
    }
 }
