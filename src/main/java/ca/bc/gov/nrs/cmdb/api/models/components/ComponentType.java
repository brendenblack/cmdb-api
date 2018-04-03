package ca.bc.gov.nrs.cmdb.api.models.components;

import ca.bc.gov.nrs.cmdb.api.models.Entity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

public class ComponentType extends Entity
{
    private String suffix;

    @Relationship(type = Component.RELATIONSHIP_IS_OF_TYPE, direction = Relationship.INCOMING)
    private Set<Component> components;
}
