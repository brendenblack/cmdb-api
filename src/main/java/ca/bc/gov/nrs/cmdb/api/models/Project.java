package ca.bc.gov.nrs.cmdb.api.models;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Required;

import java.util.Set;

@Getter
@Setter
public class Project extends Entity
{
    public static final String RELATIONSHIP_HAS_COMPONENTS = "HAS_COMPONENTS";

    @Required
    private String acronym;

    private String name;

    private String description;

    @Relationship(type = RELATIONSHIP_HAS_COMPONENTS)
    private Set<Component> components;

}
