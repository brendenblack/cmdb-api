package ca.bc.gov.nrs.cmdb.api.repositories;

import ca.bc.gov.nrs.cmdb.api.models.Build;
import ca.bc.gov.nrs.cmdb.api.models.components.Component;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface BuildRepository extends Neo4jRepository<Build, Long>
{
    Optional<Build> findByComponentAndNumber(Component component, int number);

    Optional<Build> findByUrl(String url);
}
