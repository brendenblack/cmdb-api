package ca.bc.gov.nrs.infra.cmdb.infrastructure;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpExceptionModel
{
    private String message;
    private String timestamp;
    private String path;
}
