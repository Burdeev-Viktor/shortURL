package org.example.service;

import org.apache.log4j.Logger;
import org.example.dtos.link.IdLinkRequest;
import org.example.dtos.link.LinkResponse;
import org.example.dtos.link.NewLinkRequest;
import org.example.jwt.JwtTokenUtils;
import org.example.model.Link;
import org.example.repository.LinkSQLRepo;
import org.example.repository.LinkRedisRepo;
import org.example.service.business.RandomGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LinkService {

    private static  LinkSQLRepo linkSQLRepo;
    private final LinkRedisRepo linkRedisRepo;
    private final  UserService userService;
    private static final Logger log = Logger.getLogger(LinkService.class);
    private final JwtTokenUtils jwtTokenUtils;
    private final RandomGenerator randomGenerator;

    public LinkService(LinkSQLRepo linkSQLRepo, LinkRedisRepo linkRedisRepo, UserService userService, JwtTokenUtils jwtTokenUtils, RandomGenerator randomGenerator) {
        LinkService.linkSQLRepo = linkSQLRepo;
        this.linkRedisRepo = linkRedisRepo;
        this.userService = userService;
        this.jwtTokenUtils = jwtTokenUtils;
        this.randomGenerator = randomGenerator;
    }

    public Link save(Link link){
        createLink(link);
        linkSQLRepo.save(link);
        linkRedisRepo.set(link);
        log.info("anonym saved link :\n" + link);
        return link;
    }
    public void saveLinkToRedis(Link link){
        linkRedisRepo.set(link);
    }
    public String findFastLink(String key){
        String origin = linkRedisRepo.get(key);
        if(origin == null){
            return linkSQLRepo.findOriginById(key);
        }
        return origin;
    }
    private void createLink(Link link){
        link.setActive(true);
        do {
            link.setId(randomGenerator.getRandomKey());
        }while (!checkUniqueness(link));
        setDateDel(link);
    }
    public void createFreeLink(){
        Link link = new Link();
        link.setActive(false);
        do {
            link.setId(randomGenerator.getRandomKey());
        }while (!checkUniqueness(link));
        linkSQLRepo.save(link);
    }
    private static void setDateDel(Link link){
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR,3);
        link.setDateDel(calendar.getTime());
    }

    private static boolean checkUniqueness(Link link){
     return linkSQLRepo.findById(link.getId()).isEmpty();
    }

    public void delLink(Link link){
        linkSQLRepo.delete(link);
        linkRedisRepo.del(link.getId());
        log.info("Link deleted:" + link);
    }

    private void enable(Link link) {
        linkRedisRepo.set(link);
        link.setActive(true);
        linkSQLRepo.save(link);
    }

    public ResponseEntity<?> removeLink(IdLinkRequest idLinkRequest, String token) {
        Link link = linkSQLRepo.findById(idLinkRequest.getId()).orElse(null);
        if(link == null){
            log.warn("link with this id does not exist:"+idLinkRequest.getId());
            return ResponseEntity.status(203).body("link with this id does not exist:"+idLinkRequest.getId());
        }
        if(!Objects.equals(link.getUser().getLogin(), jwtTokenUtils.getUsernameWhitsBearer(token))){
            log.warn("link does not belong to user id link:" + idLinkRequest.getId() + ", username:" + jwtTokenUtils.getUsernameWhitsBearer(token));
            return ResponseEntity.status(203).body("link does not belong to user:"+idLinkRequest.getId());
        }
        delLink(link);
        return ResponseEntity.ok("Link deleted");
    }

    public ResponseEntity<?> createNewLink(NewLinkRequest newLinkRequest, String token) {
        Link newLink = linkSQLRepo.getFreeLink();
        newLink.setActive(true);
        newLink.setUser(userService.findUserByLogin(jwtTokenUtils.getUsernameWhitsBearer(token)));
        newLink.setOrigin(newLinkRequest.getLink());
        setDateDel(newLink);
        linkRedisRepo.set(newLink);
        linkSQLRepo.save(newLink);
        log.info("user saved link :\n" + newLink);
        return ResponseEntity.ok("Link saved");
    }

    public ResponseEntity<?> getLinks(String token) {
        List<LinkResponse> links = linkSQLRepo.findByUser(userService.findUserByLogin(jwtTokenUtils.getUsernameWhitsBearer(token))).stream().map(LinkResponse::new).toList();
        return ResponseEntity.ok(links);
    }

    public ResponseEntity<?> putLink(IdLinkRequest idLinkRequest, String token) {
        Link link = linkSQLRepo.findById(idLinkRequest.getId()).orElse(null);
        if(link == null){
            log.warn("link with this id does not exist:"+idLinkRequest.getId());
            return ResponseEntity.status(203).body("link with this id does not exist:"+idLinkRequest.getId());
        }
        if(!Objects.equals(link.getUser().getLogin(), jwtTokenUtils.getUsernameWhitsBearer(token))){
            log.warn("link does not belong to user id link:" + idLinkRequest.getId() + ", username:" + jwtTokenUtils.getUsernameWhitsBearer(token));
            return ResponseEntity.status(203).body("link does not belong to user:"+idLinkRequest.getId());
        }
        if(link.isActive()){
            link.setActive(false);
            linkRedisRepo.del(link.getId());
            linkSQLRepo.save(link);
            return ResponseEntity.ok("Link disable");
        }else {
            link.setActive(true);
            linkRedisRepo.set(link);
            linkSQLRepo.save(link);
            return ResponseEntity.ok("Link enable");
        }
    }
    public int deleteOldLinks(Date now){
        List<Link> oldLinks = linkSQLRepo.getOldLinks(now);
        oldLinks.stream().map(link -> {
         delLink(link);
         return link;
        }).toList();
        return oldLinks.size();
    }
    public List<Link> getActiveLinksByLimit(long lower,long upper){
        return linkSQLRepo.getActiveLinksByLimit(lower,upper);
    }
    public long getSizeRedis(){
        return linkRedisRepo.count();
    }
    public void delAllFromRedis(){
        linkRedisRepo.delAll();
    }
    public long getCountFreeLinks(){
        return linkSQLRepo.getCountFreeLink();
    }
    public long getCountActiveLinks(){
        return linkSQLRepo.getCountActiveLink();
    }
}
