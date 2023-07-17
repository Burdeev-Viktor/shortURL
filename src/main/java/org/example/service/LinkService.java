package org.example.service;

import org.apache.log4j.Logger;
import org.example.model.Link;
import org.example.repository.LinkSQLRepo;
import org.example.repository.LinkRedisRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

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

    public LinkService(LinkSQLRepo linkSQLRepo, LinkRedisRepo linkRedisRepo, UserService userService) {
        LinkService.linkSQLRepo = linkSQLRepo;
        this.linkRedisRepo = linkRedisRepo;
        this.userService = userService;
    }

    public Link save(Link link){
        createLink(link);
        linkSQLRepo.save(link);
        linkRedisRepo.set(link);
        log.info("anonym saved link :\n" + link);
        return link;
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
        linkSQLRepo.delLink(link.getId());
        linkRedisRepo.del(link.getId());
    }
    public List<Link> findLinksByUser(UserDetails userDetails) {
        List<Link> links = linkSQLRepo.findByUser(userService.findUserByLogin(userDetails.getUsername()));
        links.stream().map(link -> {
            if(link.isActive()){
                link.setOrigin(linkRedisRepo.get(link.getId()));
            }
            return link;
        }).collect(Collectors.toList());
        return links;
    }
    public void saveByUser(Link newLink, UserDetails userDetails) {
        newLink.setUser(userService.findUserByLogin(userDetails.getUsername()));
        createLink(newLink);
        linkRedisRepo.set(newLink);
        newLink.setOrigin(null);
        linkSQLRepo.save(newLink);
        log.info("user saved link :\n" + newLink);
    }

    public void disableLink(String id, UserDetails userDetails) {
        Link link = linkSQLRepo.findById(id).orElse(null);
        if(link == null){
            log.warn("required link is missing");
            return;
        }
        if(!Objects.equals(link.getUser().getName(), userDetails.getUsername())){
            log.warn("attempt to disable someone link by the user:" + userService.findUserByLogin(userDetails.getUsername()));
            return;
        }
        disable(link);
    }

    private void disable(Link link) {
        String original = linkRedisRepo.get(link.getId());
        linkRedisRepo.del(link.getId());
        link.setOrigin(original);
        link.setActive(false);
        linkSQLRepo.save(link);
    }

    public void enableLink(String id, UserDetails userDetails) {
        Link link = linkSQLRepo.findById(id).orElse(null);
        if(link == null){
            log.warn("required link is missing");
            return;
        }
        if(!Objects.equals(link.getUser().getName(), userDetails.getUsername())){
            log.warn("attempt to enable someone link by the user:" + userService.findUserByLogin(userDetails.getUsername()));
            return;
        }
        enable(link);
    }

    private void enable(Link link) {
        linkRedisRepo.set(link);
        link.setOrigin(null);
        link.setActive(true);
        linkSQLRepo.save(link);
    }
}
