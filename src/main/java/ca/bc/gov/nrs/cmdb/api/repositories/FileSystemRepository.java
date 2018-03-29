package ca.bc.gov.nrs.cmdb.api.repositories;

import ca.bc.gov.nrs.cmdb.api.models.FileSystem;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface FileSystemRepository extends Neo4jRepository<FileSystem, Long>
{
}
