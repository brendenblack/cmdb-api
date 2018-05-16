package ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories;

import ca.bc.gov.nrs.infra.cmdb.domain.models.Component;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface ComponentRepository extends Neo4jRepository<Component, Long>
{
    Optional<Component> findByName(String componentName);
}
