package ca.bc.gov.nrs.infra.cmdb.domain.models;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Required;


@Getter
@Setter
public class ComponentInstance extends Entity
{
    public static final String RELATIONSHIP_MANIFESTS_COMPONENT = "MANIFESTS_COMPONENT";
    public static final String RELATIONSHIP_MANIFESTS_BUILD = "MANIFESTS_BUILD";
    public static final String RELATIONSHIP_INSTALLED_ON = "INSTALLED_ON";

    @Relationship(type = RELATIONSHIP_MANIFESTS_COMPONENT)
    @Required
    private Component component;

    @Relationship(type = RELATIONSHIP_MANIFESTS_BUILD)
    private JenkinsBuild build;

    @Relationship(type = RELATIONSHIP_INSTALLED_ON)
    private FileSystem filesystem;

    private String version;

    private String installPath;
}
