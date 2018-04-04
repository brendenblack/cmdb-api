package ca.bc.gov.nrs.cmdb.api.models;

import ca.bc.gov.nrs.cmdb.api.models.components.Component;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Required;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "acronym", callSuper = false)
public class Project extends Entity
{
    public static final String RELATIONSHIP_HAS_COMPONENTS = "HAS_COMPONENTS";

    @Required
    @Index(unique = true)
    private String acronym;

    private String name;

    private String description;

    @Relationship(type = RELATIONSHIP_HAS_COMPONENTS)
    private Set<Component> components = new HashSet<>();

    public void addComponent(Component component)
    {
        this.components.add(component);
    }
}
