package ca.bc.gov.nrs.infra.cmdb.infrastructure.mediator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Mediator implements ApplicationContextAware
{
    private static final Logger log = LoggerFactory.getLogger(Mediator.class);

    private Map<String, RequestHandler> handlers = new HashMap<>();
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
        Map<String, RequestHandler> handlers = applicationContext.getBeansOfType(RequestHandler.class);
        for (String key : handlers.keySet())
        {
            RequestHandler handler = handlers.get(key);
            log.debug("Tracking handler {} with key {}", handler.getClass().getName(), handler.getRequestType().getName());
            this.handlers.put(handler.getRequestType().getName(), handler);
        }

        log.debug("Loaded {} handlers", this.handlers.size());
    }

    public <T> T send(Object message, Class<T> returnType)
    {
        RequestHandler handler = this.handlers.get(message.getClass().getName());

        if (handler == null)
        {
            log.error("No handler found for request of type {}", message.getClass().getName());
            throw new HandlerNotFoundException("No handler found for request of type " + message.getClass().getName());
        }
        else
        {
            log.info("Handling request of type {} using handler {}",
                     message.getClass().getName(),
                     handler.getClass().getName());

            return returnType.cast(handler.handle(message));
        }
    }
}
