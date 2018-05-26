package ca.bc.gov.nrs.infra.cmdb.domain.models;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;

public abstract class User
{
    @Getter
    @Id
    @GeneratedValue
    private Long id;

    @Getter
    @Setter
    private String displayName;
}
