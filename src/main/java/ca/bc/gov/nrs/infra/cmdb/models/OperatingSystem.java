package ca.bc.gov.nrs.infra.cmdb.models;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class OperatingSystem extends Entity
{
    private String name;
    private String family;
    private String prettyName;
    private String variantId;
    private String version;
    private String versionName;

    @Relationship(type = Server.RELATIONSHIP_RUNS_OPERATING_SYSTEM, direction = Relationship.INCOMING)
    private Set<Server> servers = new HashSet<>();
}
