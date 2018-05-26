package ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories;

import ca.bc.gov.nrs.infra.cmdb.domain.models.FileSystem;
import ca.bc.gov.nrs.infra.cmdb.domain.models.ComponentInstance;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ComponentInstanceRepository extends Neo4jRepository<ComponentInstance, Long>
{
    Iterable<ComponentInstance> findAllByFilesystem(FileSystem filesystem);
}
