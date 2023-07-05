package org.example.Service;

import org.example.model.Link;
import org.example.model.LinkRedis;
import org.example.reposytory.LinkRedisRepo;
import org.example.reposytory.LinkSQLRepo;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class LinkSQLService {

    private LinkSQLRepo linkSQLRepo;
    private LinkRedisRepo linkRedisRepo;

    public LinkSQLService(LinkSQLRepo linkSQLRepo, LinkRedisRepo linkRedisRepo) {
        this.linkSQLRepo = linkSQLRepo;
        this.linkRedisRepo = linkRedisRepo;
    }
    public Link create(Link link){
        GeneratedLinkService.start();
        Link free = linkSQLRepo.getFreeLink();
        free.setOrigin(link.getOrigin());
        free.setDateDel(link.getDateDel());
        linkSQLRepo.save(free);
        linkRedisRepo.save(new LinkRedis(free));
        return free;
    }
    public String findFastLink(String id){
        LinkRedis optionalLinkRedis = linkRedisRepo.findtById(id);
        return optionalLinkRedis.getOrigin();
    }
    public void delLink(Link link){

    }
}
