package ca.bc.gov.nrs.infra.cmdb.domain.services;

import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.ServerRepository;
import org.springframework.stereotype.Service;

@Service
public class ServerService
{
    private final ServerRepository serverRepository;

    public ServerService(ServerRepository serverRepository)
    {
        this.serverRepository = serverRepository;
    }


}
