package ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories;

import ca.bc.gov.nrs.infra.cmdb.domain.models.FileSystem;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface FileSystemRepository extends Neo4jRepository<FileSystem, Long>
{
}
