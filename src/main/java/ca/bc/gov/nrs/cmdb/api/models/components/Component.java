package ca.bc.gov.nrs.cmdb.api.models.components;

import ca.bc.gov.nrs.cmdb.api.models.Build;
import ca.bc.gov.nrs.cmdb.api.models.Entity;
import ca.bc.gov.nrs.cmdb.api.models.Project;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@Getter
@Setter
public class Component extends Entity
{
    public final static String RELATIONSHIP_IS_OF_TYPE = "IS_OF_TYPE";

    @Relationship(type = Project.RELATIONSHIP_HAS_COMPONENTS, direction = Relationship.INCOMING)
    private Project project;

    @Relationship(type = Build.RELATIONSHIP_BUILD_OF, direction = Relationship.INCOMING)
    private Set<Build> builds;

    @Relationship(type = RELATIONSHIP_IS_OF_TYPE)
    private ComponentType componentType;

    @Index(unique = true)
    private String name;
}
