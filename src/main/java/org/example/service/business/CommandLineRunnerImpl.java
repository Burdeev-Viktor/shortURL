package org.example.service.business;

import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.example.model.Link;
import org.example.service.LinkService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CommandLineRunnerImpl implements CommandLineRunner {
    private static final Logger log = Logger.getLogger(CommandLineRunnerImpl.class);
    private LinkService linkService;

    @Override
    public void run(String... args) {
        log.info("database check");
        List<Link> links = linkService.getAllEnable();
        long redisSize = linkService.getSizeRedis();
        if (redisSize == links.size()){
            log.info("database check successfully");
            return;
        }
        log.warn("database check error in SQL " + links.size() + " in Redis " + redisSize);
        linkService.delAllFromRedis();
        for (Link link:links) {
            linkService.saveLinkToRedis(link);
        }

    }
}
