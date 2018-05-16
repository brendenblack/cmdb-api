package ca.bc.gov.nrs.infra.cmdb.services;

import ca.bc.gov.nrs.infra.cmdb.repositories.ServerRepository;
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
