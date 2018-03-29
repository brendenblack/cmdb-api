package ca.bc.gov.nrs.cmdb.api.models;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@Getter
@Setter
public class Build extends Entity
{
    public static final String RELATIONSHIP_BUILD_OF = "BUILD_OF";

    @Relationship(type = RELATIONSHIP_BUILD_OF)
    private Component component;

    @Relationship(type = ComponentInstance.RELATIONSHIP_MANIFESTS_BUILD)
    private Set<ComponentInstance> instances;


}
