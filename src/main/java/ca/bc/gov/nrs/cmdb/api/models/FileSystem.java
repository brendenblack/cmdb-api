package ca.bc.gov.nrs.cmdb.api.models;

import org.neo4j.ogm.annotation.Relationship;

public class FileSystem extends Entity
{
    private String name;
    private String type;
    private long size;
    private long used;
    private long available;
    private String mountedOn;

    @Relationship(type = Server.RELATIONSHIP_HAS_FILESYSTEM, direction = Relationship.OUTGOING)
    private Server server;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    public long getUsed()
    {
        return used;
    }

    public void setUsed(long used)
    {
        this.used = used;
    }

    public long getAvailable()
    {
        return available;
    }

    public void setAvailable(long available)
    {
        this.available = available;
    }

    public String getMountedOn()
    {
        return mountedOn;
    }

    public void setMountedOn(String mountedOn)
    {
        this.mountedOn = mountedOn;
    }

    public int getUsagePercent()
    {
        double usage = this.used / (this.used + this.available) * 100;
        return (int)Math.ceil(usage);
    }

    public Server getServer()
    {
        return server;
    }

    public void setServer(Server server)
    {
        this.server = server;
    }
}
