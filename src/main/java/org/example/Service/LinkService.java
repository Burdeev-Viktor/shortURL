package org.example.Service;

import org.example.model.Link;
import org.example.repository.LinkSQLRepo;
import org.example.repository.LinkRedisRepo;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

@Service
public class LinkService {
    private static final long UPPER_RANGE = 3579345993194L;
    private static final long LOWER_RANGE = 56800235584L;
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE62_BASE = BASE62_ALPHABET.length();

    private static  LinkSQLRepo linkSQLRepo;
    private final LinkRedisRepo linkRedisRepo;

    public LinkService(LinkSQLRepo linkSQLRepo, LinkRedisRepo linkRedisRepo) {
        LinkService.linkSQLRepo = linkSQLRepo;
        this.linkRedisRepo = linkRedisRepo;
    }

    public Link save(Link link){
        createLink(link);
        linkSQLRepo.save(link);
        linkRedisRepo.set(link);
        return link;
    }
    public String findFastLink(String key){
        return linkRedisRepo.get(key);
    }
    private static void createLink(Link link){
        do {
            link.setGenerated(getRandomKey());
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
     return linkSQLRepo.findById(link.getGenerated()).isEmpty();
    }
    private static long getRandomLong(){
        Random random = new Random();
        return random.nextLong(LOWER_RANGE,UPPER_RANGE);
    }
    public void delLink(Link link){
        linkSQLRepo.delLink(link.getGenerated());
        linkRedisRepo.del(link.getGenerated());
    }
}
