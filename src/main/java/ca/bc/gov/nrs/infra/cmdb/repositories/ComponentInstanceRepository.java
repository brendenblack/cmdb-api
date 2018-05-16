package ca.bc.gov.nrs.infra.cmdb.repositories;

import ca.bc.gov.nrs.infra.cmdb.models.FileSystem;
import ca.bc.gov.nrs.infra.cmdb.models.components.ComponentInstance;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ComponentInstanceRepository extends Neo4jRepository<ComponentInstance, Long>
{
    Iterable<ComponentInstance> findAllByFilesystem(FileSystem filesystem);
}
