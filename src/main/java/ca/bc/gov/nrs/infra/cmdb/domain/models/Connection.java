package ca.bc.gov.nrs.infra.cmdb.domain.models;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Connection extends Entity
{
    public final static String RELATIONSHIP_CONNECTS_TO = "CONNECTS_TO";

    private String protocol;

    private String uri;

    @Relationship(type = Secret.RELATIONSHIP_GRANTS_ACCESS_TO, direction =  Relationship.INCOMING)
    private Secret secret;

    @Relationship(type = RELATIONSHIP_CONNECTS_TO)
    private Set<Entity> targets = new HashSet<>();
}
