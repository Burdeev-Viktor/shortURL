package org.example.jobs;

import org.apache.log4j.Logger;
import org.example.service.LinkService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@EnableScheduling
public class Job {
    private final LinkService linkService;
    private static final Logger log = Logger.getLogger(Job.class);
    @Value("${links.free.min}")
    private long minCountFreeLinks;
    @Value("${links.free.create}")
    private long createOfOneJob;

    public Job(LinkService linkService) {
        this.linkService = linkService;
    }

    @Scheduled(cron = "0 0 12 * * *")
    public void deleteOldLinks(){
        log.info("start of deleting old links ");
        long count = linkService.deleteOldLinks(new Date());
        log.info("end of deleting old links total links removed:" + count);
    }
    @Scheduled(cron = "0 0 * * * *")
    public void createFreeLinks(){
        log.info("start of creating free links ");
        long count = linkService.getCountFreeLinks();
        if(count > minCountFreeLinks){
            log.info("SQL store free links:" + count);
            return;
        }
        for (long i = 0;i < createOfOneJob;i++){
            linkService.createFreeLink();
        }
        log.info("end of creating free links created:" + createOfOneJob);
    }
    
}
