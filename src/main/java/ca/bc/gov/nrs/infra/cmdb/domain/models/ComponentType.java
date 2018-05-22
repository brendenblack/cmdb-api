package ca.bc.gov.nrs.infra.cmdb.domain.models;

import ca.bc.gov.nrs.infra.cmdb.domain.models.irs.Component;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

public class ComponentType extends Entity
{
    private String suffix;

    @Relationship(type = Component.RELATIONSHIP_IS_OF_TYPE, direction = Relationship.INCOMING)
    private Set<Component> components;
}
