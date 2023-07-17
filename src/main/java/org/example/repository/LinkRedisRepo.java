package org.example.repository;

import org.apache.log4j.Logger;
import org.example.model.Link;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class LinkRedisRepo {
    private static final Logger log = Logger.getLogger(LinkRedisRepo.class);
    private static JedisPool pool;
    static {
        try{
            pool = new JedisPool("localhost", 6379);
        }catch (Exception e){
            log.fatal("Error connecting to Redis");
        }
    }
    private static Jedis jedis = pool.getResource();
    public void set(Link link){
        jedis.set(link.getId(), link.getOrigin());
    }
    public String get(String key){
        return jedis.get(key);
    }
    public void del(String key){
        jedis.del(key);
    }
}
