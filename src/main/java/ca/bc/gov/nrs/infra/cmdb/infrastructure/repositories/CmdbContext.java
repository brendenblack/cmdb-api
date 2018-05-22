package ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Data context that aggregates all repositories used by the CMDB.
 */
@Repository
@Getter
public class CmdbContext
{
    private final Logger log = LoggerFactory.getLogger(CmdbContext.class);

    private final JenkinsBuildRepository jenkinsBuildRepository;
    private final JenkinsPromotionRepository jenkinsPromotionRepository;
    private final ComponentInstanceRepository componentInstanceRepository;
    private final ComponentRepository componentRepository;
    private final ConnectionRepository connectionRepository;
    private final FileSystemRepository fileSystemRepository;
    private final OperatingSystemRepository operatingSystemRepository;
    private final ProjectRepository projectRepository;
    private final SecretsRepository secretsRepository;
    private final ServerRepository serverRepository;

    @Autowired
    CmdbContext(JenkinsBuildRepository buildRepository,
                JenkinsPromotionRepository jenkinsPromotionRepository,
                ComponentInstanceRepository componentInstanceRepository,
                ComponentRepository componentRepository,
                ConnectionRepository connectionRepository,
                FileSystemRepository fileSystemRepository,
                OperatingSystemRepository operatingSystemRepository,
                ProjectRepository projectRepository,
                SecretsRepository secretsRepository,
                ServerRepository serverRepository)
    {
        log.debug("Initializing CMDB context");

        this.jenkinsBuildRepository = buildRepository;
        log.debug("Jenkins build repository: {}", jenkinsBuildRepository);

        this.jenkinsPromotionRepository = jenkinsPromotionRepository;
        log.debug("Jenkins promotion repository: {}", jenkinsPromotionRepository);

        this.componentInstanceRepository = componentInstanceRepository;
        log.debug("Component repository: {}", componentRepository);

        this.componentRepository = componentRepository;
        log.debug("Component repository: {}", componentRepository);

        this.connectionRepository = connectionRepository;
        log.debug("Connection repository: {}", connectionRepository);

        this.fileSystemRepository = fileSystemRepository;
        log.debug("File system repository: {}", fileSystemRepository);

        this.operatingSystemRepository = operatingSystemRepository;
        log.debug("Operating system repository: {}", operatingSystemRepository);

        this.projectRepository = projectRepository;
        log.debug("Project repository: {}", projectRepository);

        this.secretsRepository = secretsRepository;
        log.debug("Secrets repository: {}", secretsRepository);

        this.serverRepository = serverRepository;
        log.debug("Server repository: {}", serverRepository);

        log.debug("Finished initializing CMDB context");
    }
}
