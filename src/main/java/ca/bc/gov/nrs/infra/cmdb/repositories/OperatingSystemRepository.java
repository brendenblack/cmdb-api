package ca.bc.gov.nrs.infra.cmdb.repositories;

import ca.bc.gov.nrs.infra.cmdb.models.OperatingSystem;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface OperatingSystemRepository extends Neo4jRepository<OperatingSystem, Long>
{
    Optional<OperatingSystem> findByNameAndVersion(String name, String version);
}
