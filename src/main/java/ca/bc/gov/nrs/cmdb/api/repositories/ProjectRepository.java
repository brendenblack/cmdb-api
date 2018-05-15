package ca.bc.gov.nrs.cmdb.api.repositories;

import ca.bc.gov.nrs.cmdb.api.models.Project;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface ProjectRepository extends Neo4jRepository<Project, Long>
{
    Optional<Project> findByKey(String key);
}
