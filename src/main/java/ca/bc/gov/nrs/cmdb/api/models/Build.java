package ca.bc.gov.nrs.cmdb.api.models;

import ca.bc.gov.nrs.cmdb.api.models.components.Component;
import ca.bc.gov.nrs.cmdb.api.models.components.ComponentInstance;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Relationship;

import java.text.SimpleDateFormat;
import java.util.Set;

@Getter
@Setter
public class Build extends Entity
{
    public static final String RELATIONSHIP_BUILD_OF = "BUILD_OF";
    public static final String RELATIONSHIP_BUILT_ON = "BUILT_ON";

    @Relationship(type = RELATIONSHIP_BUILD_OF)
    private Component component;

    @Relationship(type = ComponentInstance.RELATIONSHIP_MANIFESTS_BUILD)
    private Set<ComponentInstance> instances;

    private int number;

    private String jobClass;

    private int duration;

    private String displayName;

    private String url;

    private int queueId;

    private int timestamp;

    @Relationship(type = RELATIONSHIP_BUILT_ON)
    private Server server;
}
