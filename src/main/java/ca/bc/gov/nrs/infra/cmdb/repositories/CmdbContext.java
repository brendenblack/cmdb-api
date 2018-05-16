package ca.bc.gov.nrs.infra.cmdb.repositories;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Data context that aggregates all repositories used by the CMDB.
 */
@Repository
@Getter
public class CmdbContext
{
    private final BuildRepository buildRepository;
    private final ComponentInstanceRepository componentInstanceRepository;
    private final ComponentRepository componentRepository;
    private final ConnectionRepository connectionRepository;
    private final FileSystemRepository fileSystemRepository;
    private final OperatingSystemRepository operatingSystemRepository;
    private final ProjectRepository projectRepository;
    private final SecretsRepository secretsRepository;
    private final ServerRepository serverRepository;

    @Autowired
    public CmdbContext(BuildRepository buildRepository,
                       ComponentInstanceRepository componentInstanceRepository,
                       ComponentRepository componentRepository,
                       ConnectionRepository connectionRepository,
                       FileSystemRepository fileSystemRepository,
                       OperatingSystemRepository operatingSystemRepository,
                       ProjectRepository projectRepository,
                       SecretsRepository secretsRepository,
                       ServerRepository serverRepository)
    {
        this.buildRepository = buildRepository;
        this.componentInstanceRepository = componentInstanceRepository;
        this.componentRepository = componentRepository;
        this.connectionRepository = connectionRepository;
        this.fileSystemRepository = fileSystemRepository;
        this.operatingSystemRepository = operatingSystemRepository;
        this.projectRepository = projectRepository;
        this.secretsRepository = secretsRepository;
        this.serverRepository = serverRepository;
    }
}
