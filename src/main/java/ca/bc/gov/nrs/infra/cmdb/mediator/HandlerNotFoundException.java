package ca.bc.gov.nrs.infra.cmdb.mediator;

public class HandlerNotFoundException extends RuntimeException
{
    public HandlerNotFoundException(String message)
    {
        super(message);
    }

}
