package ca.bc.gov.nrs.infra.cmdb.domain.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Required;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "key", callSuper = false)
public class Project extends Entity
{
    public static final String RELATIONSHIP_HAS_COMPONENTS = "HAS_COMPONENTS";

    /**
     * OGM requires a public no-args constructor
     *
     * @deprecated use the provided {@link Builder} instead ({@link #withKey(String)})
     */
    @Deprecated
    public Project() {}

    Project(String key)
    {
        this.key = key;
    }

    @Required
    @Index(unique = true)
    private String key;

    private String name;

    private String description;

    @Relationship(type = RELATIONSHIP_HAS_COMPONENTS, direction = Relationship.OUTGOING)
    @JsonManagedReference
    private Set<Component> components = new HashSet<>();

    public void addComponent(Component component)
    {
        this.components.add(component);
    }

    public static OptionalParameters withKey(String key)
    {
        return new Builder(key);
    }

    public interface OptionalParameters
    {
        OptionalParameters name(String name);

        OptionalParameters description(String description);


        Project build();
    }


    public static class Builder implements OptionalParameters
    {
        private final String key;
        private String name;
        private String description;

        Builder(String key)
        {
            this.key = key.toUpperCase();
        }

        @Override
        public OptionalParameters name(String name)
        {
            this.name = name;
            return this;
        }

        @Override
        public OptionalParameters description(String description)
        {
            this.description = description;
            return this;
        }

        @Override
        public Project build()
        {
            Project project = new Project(this.key);
            project.setName(Optional.ofNullable(this.name).orElse(this.key));
            project.setDescription(Optional.ofNullable(this.description).orElse(this.key + " project"));
            return project;
        }

    }
}
