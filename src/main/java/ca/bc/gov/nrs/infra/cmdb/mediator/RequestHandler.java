package ca.bc.gov.nrs.infra.cmdb.mediator;

public interface RequestHandler<IRequest, TResponse>
{
    TResponse handle(IRequest message);

    Class getRequestType();

    Class getReturnType();
}
