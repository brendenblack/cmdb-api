package ca.bc.gov.nrs.infra.cmdb.features.webhooks.jenkins;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AddBuildModel
{
    private String projectKey;
    private String componentName;
    private int number;
    private String url;
    private long startedAt;
    private long duration;
    private String triggeredBy;
    private int queueId;
    private String jobType;
    private String displayName;
    private String result = "";
    private String performedOn;
}
