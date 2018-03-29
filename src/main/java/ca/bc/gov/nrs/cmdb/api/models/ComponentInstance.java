package ca.bc.gov.nrs.cmdb.api.models;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Relationship;

@Getter
@Setter
public class ComponentInstance
{
    public static final String RELATIONSHIP_MANIFESTS_COMPONENT = "MANIFESTS_COMPONENT";
    public static final String RELATIONSHIP_MANIFESTS_BUILD = "MANIFESTS_BUILD";

    @Relationship(type = RELATIONSHIP_MANIFESTS_COMPONENT)
    private Component component;

    @Relationship(type = RELATIONSHIP_MANIFESTS_BUILD)
    private Build build;
}
