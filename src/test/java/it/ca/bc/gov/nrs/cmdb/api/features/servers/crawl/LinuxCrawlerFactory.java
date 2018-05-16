package it.ca.bc.gov.nrs.cmdb.api.features.servers.crawl;

import ca.bc.gov.nrs.infra.cmdb.features.servers.crawl.LinuxCrawler;

import java.util.ArrayList;

public class LinuxCrawlerFactory
{
    public static LinuxCrawler getLinuxCrawler()
    {
        return new LinuxCrawler(new ArrayList<>());
    }
}
