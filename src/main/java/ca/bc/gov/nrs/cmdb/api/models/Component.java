package ca.bc.gov.nrs.cmdb.api.models;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@Getter
@Setter
public class Component extends Entity
{

    @Relationship(type = Project.RELATIONSHIP_HAS_COMPONENTS, direction = Relationship.INCOMING)
    private Project project;

    @Relationship(type = Build.RELATIONSHIP_BUILD_OF, direction = Relationship.INCOMING)
    private Set<Build> builds;
}
