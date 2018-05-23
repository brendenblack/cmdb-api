package ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories;

import ca.bc.gov.nrs.infra.cmdb.domain.models.jenkins.JenkinsPromotion;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;
import java.util.Optional;

public interface JenkinsPromotionRepository extends Neo4jRepository<JenkinsPromotion, Long>
{
    Optional<JenkinsPromotion> findByComponentNameAndBuildNumberAndNumber(String componentName, int buildNumber, int number);


    @Query(value = "MATCH (p:JenkinsPromotion)-[r:PROMOTION_OF]->(b:JenkinsBuild)-[r_b:BUILD_OF]->(c:Component) WITH p, b, c WHERE c.name={0} AND p.environment={1} RETURN p, b, c")
    Iterable<JenkinsPromotion> findByEnvironment(String componentName, String environment);

    @Query(value = "MATCH (p:JenkinsPromotion)-[r:PROMOTION_OF]->(b:JenkinsBuild)-[r_b:BUILD_OF]->(c:Component) WITH p, b, c WHERE c.name={0} AND b.number={1} RETURN p, b, c")
    Iterable<JenkinsPromotion> findByBuild(String componentName, int buildNumber);

    @Query(value = "MATCH (p:JenkinsPromotion)-[r:PROMOTION_OF]->(b:JenkinsBuild)-[r_b:BUILD_OF]->(c:Component) WITH p, b, c WHERE c.name={0} AND b.number={1} AND p.environment={2} RETURN p, b, c")
    Iterable<JenkinsPromotion> findByBuildNumberAndEnvironment(String componentName, int buildNumber, String environment);

    @Query(value = "MATCH (p:JenkinsPromotion)-[r:PROMOTION_OF]->(b:JenkinsBuild)-[r_b:BUILD_OF]->(c:Component) WITH p, b, c WHERE c.name={0} AND b.number={1} AND p.environment={2} AND p.number={3} RETURN p, b, c")
    Optional<JenkinsPromotion> findPromotion(String componentName, int buildNumber, String environment, int number);
}
