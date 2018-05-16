package ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories;

import ca.bc.gov.nrs.infra.cmdb.domain.models.JenkinsBuild;
import ca.bc.gov.nrs.infra.cmdb.domain.models.components.Component;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface JenkinsBuildRepository extends Neo4jRepository<JenkinsBuild, Long>
{
    Optional<JenkinsBuild> findByComponentAndNumber(Component component, int number);

    Optional<JenkinsBuild> findByUrl(String url);

    boolean existsByComponentAndNumber(Component component, int number);
}
