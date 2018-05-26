package ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator;

public class HandlerNotFoundException extends RuntimeException
{
    public HandlerNotFoundException(String message)
    {
        super(message);
    }

}
