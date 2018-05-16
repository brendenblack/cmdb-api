package ca.bc.gov.nrs.infra.cmdb.features.servers.crawl;

import ca.bc.gov.nrs.infra.cmdb.infrastructure.HttpException;
import ca.bc.gov.nrs.infra.cmdb.mediator.IRequest;
import ca.bc.gov.nrs.infra.cmdb.mediator.RequestHandler;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class Cancel
{
    @Getter
    @Setter
    public static class Command implements IRequest
    {
        private String crawlId;
    }

    @Getter
    @Setter
    public static class Model
    {
        private boolean cancelled;
    }

    public static class Handler implements RequestHandler<Command, Model>
    {
        private static Logger log = LoggerFactory.getLogger(Handler.class);
        private final CrawlManager crawlManager;

        public Handler(CrawlManager crawlManager)
        {
            this.crawlManager = crawlManager;
        }

        @Override
        public Model handle(Command message)
        {
            CrawlRunnable runnable = this.crawlManager.getCrawlsInProgress().get(message.getCrawlId());
            if (runnable == null)
            {
                throw new HttpException(HttpStatus.NOT_FOUND, "Unable to find an in-progress crawl with id " + message.getCrawlId());
            }

            runnable.cancel();;

            Model model = new Model();
            model.setCancelled(true);
            return model;
        }

        @Override
        public Class getRequestType()
        {
            return Command.class;
        }

        @Override
        public Class getReturnType()
        {
            return Model.class;
        }
    }
}
