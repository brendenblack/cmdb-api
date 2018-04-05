package ca.bc.gov.nrs.cmdb.api.mediator;

public interface IRequestHandler<IRequest, TResponse>
{
    TResponse handle(IRequest message);

    Class getRequestType();

    Class getReturnType();
}
