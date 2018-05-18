package ca.bc.gov.nrs.infra.cmdb.domain.services;

import ca.bc.gov.nrs.infra.cmdb.infrastructure.repositories.CmdbContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl
{
    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final CmdbContext context;

    @Autowired
    UserServiceImpl(CmdbContext context)
    {
        this.context = context;
    }
}
