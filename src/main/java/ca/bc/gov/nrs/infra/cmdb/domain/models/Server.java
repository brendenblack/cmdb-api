package ca.bc.gov.nrs.infra.cmdb.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
@EqualsAndHashCode(of={"fqdn"}, callSuper = false)
public class Server extends Entity
{
    public static final String RELATIONSHIP_HAS_FILESYSTEM = "HAS_FILESYSTEM";
    public static final String RELATIONSHIP_RUNS_OPERATING_SYSTEM = "RUNS_OPERATING_SYSTEM";

    @Index(unique = true)
    @Getter
    @Setter
    private String fqdn;

    @Getter
    @Setter
    private String architecture;

    @JsonIgnore
    @Getter
    @Relationship(type = RELATIONSHIP_HAS_FILESYSTEM)
    private Set<FileSystem> fileSystems = new HashSet<>();

    @Relationship(type = RELATIONSHIP_RUNS_OPERATING_SYSTEM)
    @Getter
    @Setter
    private OperatingSystem operatingSystem;

    /**
     * Adds a given {@link FileSystem} to this server's filesystem collection
     *
     * @param fs
     */
    public boolean hasFileSystem(FileSystem fs)
    {
        return this.fileSystems.add(fs);
    }

    /**
     * Replaces the existing collection of {@link FileSystem}s with the provided set.
     *
     * @param fs
     */
    public void hasFileSystems(Set<FileSystem> fs)
    {
        if (fs == null)
        {
            fs = new HashSet<>();
        }

        this.fileSystems = fs;
    }


}
