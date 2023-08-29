package org.example.repository;

import org.apache.log4j.Logger;
import org.example.model.Link;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class LinkRedisRepo {
    private static final Logger log = Logger.getLogger(LinkRedisRepo.class);
    private static final JedisPool pool = new JedisPool("localhost", 6379);
    private static Jedis jedis ;
    static {
        try{
            jedis = pool.getResource();
            jedis.configSet("timeout","0");
        }catch (Exception e){
            log.fatal("Error connecting to Redis");
            System.exit(1);
        } finally {
            log.info("Redis connect");
        }
    }

    public void set(Link link){
        jedis.set(link.getId(), link.getOrigin());
    }
    public String get(String key){
        Jedis jedisGet = pool.getResource();
        return jedisGet.get(key);
    }
    public void del(String key){
        jedis.del(key);
    }
    public long count(){ return jedis.dbSize(); }
    public void delAll(){
        try {
            jedis.flushAll();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        }
}
