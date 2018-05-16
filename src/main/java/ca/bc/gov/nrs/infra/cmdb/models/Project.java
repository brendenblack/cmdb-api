package ca.bc.gov.nrs.infra.cmdb.models;

import ca.bc.gov.nrs.infra.cmdb.models.components.Component;
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
@EqualsAndHashCode(of = "key", callSuper = false)
public class Project extends Entity
{
    public static final String RELATIONSHIP_HAS_COMPONENTS = "HAS_COMPONENTS";

    private Project() {}

    Project(String key)
    {
        this.key = key;
    }

    @Required
    @Index(unique = true)
    private String key;

    private String name;

    private String description;

    @Relationship(type = RELATIONSHIP_HAS_COMPONENTS)
    private Set<Component> components = new HashSet<>();

    public void addComponent(Component component)
    {
        this.components.add(component);
    }

    public static ProjectBuilder createProject(String key)
    {
        return new ProjectBuilder(key);
    }

    public static class ProjectBuilder
    {
        private String key;
        private String name;

        ProjectBuilder(String key)
        {
            this.key = key;
        }

        public ProjectBuilder name(String name)
        {
            this.name = name;
            return this;
        }

        public Project build()
        {
            Project project = new Project(this.key);
            project.setName(this.name);
            return project;
        }

    }
}
