package ca.bc.gov.nrs.cmdb.api.services;

import ca.bc.gov.nrs.cmdb.api.repositories.ServerRepository;
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
