package org.example.service;

import org.apache.log4j.Logger;
import org.example.dtos.link.IdLinkRequest;
import org.example.dtos.link.LinkResponse;
import org.example.dtos.link.NewLinkRequest;
import org.example.jwt.JwtTokenUtils;
import org.example.model.Link;
import org.example.repository.LinkSQLRepo;
import org.example.repository.LinkRedisRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LinkService {
    private static final long UPPER_RANGE = 3579345993194L;
    private static final long LOWER_RANGE = 56800235584L;
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE62_BASE = BASE62_ALPHABET.length();
    private static  LinkSQLRepo linkSQLRepo;
    private final LinkRedisRepo linkRedisRepo;
    private final  UserService userService;
    private static final Logger log = Logger.getLogger(LinkService.class);
    private final JwtTokenUtils jwtTokenUtils;

    public LinkService(LinkSQLRepo linkSQLRepo, LinkRedisRepo linkRedisRepo, UserService userService, JwtTokenUtils jwtTokenUtils) {
        LinkService.linkSQLRepo = linkSQLRepo;
        this.linkRedisRepo = linkRedisRepo;
        this.userService = userService;
        this.jwtTokenUtils = jwtTokenUtils;
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
        return linkRedisRepo.get(key);
    }
    private static void createLink(Link link){
        link.setActive(true);
        do {
            link.setId(getRandomKey());
        }while (!checkUniqueness(link));
        setDateDel(link);
    }
    private static void setDateDel(Link link){
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR,3);
        link.setDateDel(calendar.getTime());
    }
    private static String getRandomKey(){
        StringBuilder result = new StringBuilder();
        long number = getRandomLong();
        while (number >= BASE62_BASE) {
            int i = (int)(number % BASE62_BASE);
            result.append(BASE62_ALPHABET.charAt(i));
            number = number / BASE62_BASE;
        }
        result.append(BASE62_ALPHABET.charAt((int) number));
        return result.reverse().toString();
    }
    private static boolean checkUniqueness(Link link){
     return linkSQLRepo.findById(link.getId()).isEmpty();
    }
    private static long getRandomLong(){
        Random random = new Random();
        return random.nextLong(LOWER_RANGE,UPPER_RANGE);
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
        Link newLink = new Link();
        newLink.setUser(userService.findUserByLogin(jwtTokenUtils.getUsernameWhitsBearer(token)));
        newLink.setOrigin(newLinkRequest.getLink());
        createLink(newLink);
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
    public List<Link> getAllEnable(){
        return linkSQLRepo.findAllByActive(true);
    }
    public long getSizeRedis(){
        return linkRedisRepo.count();
    }
    public void delAllFromRedis(){
        linkRedisRepo.delAll();
    }
}
