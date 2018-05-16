package ca.bc.gov.nrs.infra.cmdb.domain.models;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@Getter
@Setter
public abstract class Secret extends Entity
{
    public final static String RELATIONSHIP_GRANTS_ACCESS_TO = "GRANTS_ACCESS_TO";

    private String description;

    @Relationship(type = RELATIONSHIP_GRANTS_ACCESS_TO)
    private Set<Connection> connections;
}
