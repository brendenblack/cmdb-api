package ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

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
