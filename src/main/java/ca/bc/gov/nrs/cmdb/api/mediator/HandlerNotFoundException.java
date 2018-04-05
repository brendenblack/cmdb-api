package ca.bc.gov.nrs.cmdb.api.mediator;

public class HandlerNotFoundException extends RuntimeException
{
    public HandlerNotFoundException(String message)
    {
        super(message);
    }

}
