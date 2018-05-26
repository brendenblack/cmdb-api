package ca.bc.gov.nrs.infra.cmdb.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Relationship;

@EqualsAndHashCode(callSuper = false, of={ "name", "mountedOn", "type" })
@Getter
@Setter
public class FileSystem extends Entity
{
    private String name;

    private String type;

    private long size;

    private long used;

    private long available;

    private String mountedOn;

    @JsonIgnore
    @Relationship(type = Server.RELATIONSHIP_HAS_FILESYSTEM, direction = Relationship.INCOMING)
    private Server server;

    public int getUsagePercent()
    {
        double usage = this.used / (this.used + this.available) * 100;
        return (int)Math.ceil(usage);
    }
}
