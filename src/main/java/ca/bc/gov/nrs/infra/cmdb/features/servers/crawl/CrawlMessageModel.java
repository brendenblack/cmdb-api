package ca.bc.gov.nrs.infra.cmdb.features.servers.crawl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonAutoDetect
public class CrawlMessageModel
{
    private String fqdn;
    private String status;
    private String message;
}
