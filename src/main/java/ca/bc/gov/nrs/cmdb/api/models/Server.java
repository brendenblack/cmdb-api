package ca.bc.gov.nrs.cmdb.api.models;

import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Server extends ConnectableEntity
{
    public static final String RELATIONSHIP_HAS_FILESYSTEM = "HAS_FILESYSTEM";

    @Index(unique = true)
    private String fqdn;

    @Relationship(type = RELATIONSHIP_HAS_FILESYSTEM)
    private Set<FileSystem> filesystems = new HashSet<>();

    public String getFqdn()
    {
        return fqdn;
    }

    public void setFqdn(String fqdn)
    {
        this.fqdn = fqdn;
    }

    public void hasFileSystem(FileSystem fs)
    {
        this.filesystems.add(fs);
    }

    public Set<FileSystem> getFileSystems()
    {
        return this.filesystems;
    }
}
