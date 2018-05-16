package ca.bc.gov.nrs.infra.cmdb.repositories;

import ca.bc.gov.nrs.infra.cmdb.models.FileSystem;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface FileSystemRepository extends Neo4jRepository<FileSystem, Long>
{
}
