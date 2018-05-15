package ca.bc.gov.nrs.cmdb.api.models.components;

import ca.bc.gov.nrs.cmdb.api.models.Build;
import ca.bc.gov.nrs.cmdb.api.models.Entity;
import ca.bc.gov.nrs.cmdb.api.models.Project;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Required;

import java.util.Set;

@Getter
public class Component extends Entity
{
    public final static String RELATIONSHIP_IS_OF_TYPE = "IS_OF_TYPE";

    private Component() {}

    Component(Project project, String name)
    {
        this.project = project;
        this.name = name;
    }

    @Relationship(type = Project.RELATIONSHIP_HAS_COMPONENTS, direction = Relationship.INCOMING)
    @Required
    private Project project;

    @Relationship(type = Build.RELATIONSHIP_BUILD_OF, direction = Relationship.INCOMING)
    private Set<Build> builds;

    @Relationship(type = RELATIONSHIP_IS_OF_TYPE)
    private ComponentType componentType;

    @Index(unique = true)
    private String name;

    public static ComponentBuilder ofName(String name)
    {
        return new ComponentBuilder(name);
    }



    public static class ComponentBuilder
    {
        private String name;
        private Project project;

        ComponentBuilder(String name)
        {
            this.name = name;
        }

        public ComponentBuilder belongsTo(Project project)
        {
            this.project = project;
            return this;
        }

        public Component build()
        {
            return new Component(this.project, this.name);
        }


    }

}
