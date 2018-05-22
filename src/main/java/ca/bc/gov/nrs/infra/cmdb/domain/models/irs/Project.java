package ca.bc.gov.nrs.infra.cmdb.domain.models.irs;

import ca.bc.gov.nrs.infra.cmdb.domain.models.Entity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.sourceforge.plantuml.StringUtils;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Required;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "key", callSuper = false)
@ToString(of = {"key", "name", "description"})
@NodeEntity
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

    //region builder
    public static OptionalParameters withKey(String key)
    {
        if (StringUtils.isEmpty(key))
        {
            // TODO
        }
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
        private final Logger log = LoggerFactory.getLogger(Builder.class);

        private final String key;
        private String name;
        private String description;

        Builder(String key)
        {
            log.debug("Instantiating project builder with key {}", key);
            this.key = key.toUpperCase();
            log.debug("Builder created");
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
    //endregion
}
