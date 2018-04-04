package ca.bc.gov.nrs.cmdb.api.repositories;

import ca.bc.gov.nrs.cmdb.api.models.FileSystem;
import ca.bc.gov.nrs.cmdb.api.models.components.ComponentInstance;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ComponentInstanceRepository extends Neo4jRepository<ComponentInstance, Long>
{
    Iterable<ComponentInstance> findAllByFilesystem(FileSystem filesystem);
}
