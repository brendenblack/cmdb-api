package ca.bc.gov.nrs.infra.cmdb.features.servers.crawl;

public class CrawlException extends RuntimeException
{
    public CrawlException(Throwable e)
    {
        super(e);
    }

    public CrawlException(String message)
    {
        super(message);
    }
}
