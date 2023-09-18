package com.example.jobeserver.jobs;

import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.example.model.Link;
import org.example.service.LinkService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.List;

@Component
@AllArgsConstructor
public class CommandLineRunnerImpl implements CommandLineRunner {
    private static final Logger log = Logger.getLogger(CommandLineRunnerImpl.class);
    private LinkService linkService;

    @Override
    public void run(String... args) {
        StopWatch watch = new StopWatch();
        watch.start();
        log.info("database check");
        long countActiveLinks = linkService.getCountActiveLinks();
        long limit = 1000000;
        long redisSize = linkService.getSizeRedis();
        if (redisSize == countActiveLinks){
            log.info("database check successfully");
            watch.stop();
            System.out.println("Total execution time: "
                    + watch.getTotalTimeMillis());
            return;
        }
        log.warn("database check error in SQL " + countActiveLinks + " in Redis " + redisSize);
        linkService.delAllFromRedis();
        for (int i = 0; i < (int)(countActiveLinks/limit)+1; i++){
            List<Link> links = linkService.getActiveLinksByLimit(limit , limit * i);
            for (Link link:links) {
                linkService.saveLinkToRedis(link);
            }
        }
        watch.stop();
        System.out.println("Total execution time: "
                + watch.getTotalTimeMillis());
    }
}
