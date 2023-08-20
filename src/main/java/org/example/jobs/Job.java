package org.example.jobs;

import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.example.service.LinkService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@EnableScheduling
@AllArgsConstructor
public class Job {
    private LinkService linkService;
    private static final Logger log = Logger.getLogger(Job.class);
    @Scheduled(cron = "0 12 * * * *")
    public void deleteOldLinks(){
        log.info("start of deleting old links ");
        long count = linkService.deleteOldLinks(new Date());
        log.info("end of deleting old links total links removed:" + count);
    }
    
}
