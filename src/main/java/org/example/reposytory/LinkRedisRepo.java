package org.example.reposytory;


import org.example.model.LinkRedis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LinkRedisRepo  {
    public static final String HASH_KEY = "Product";

    private RedisTemplate template;

    public LinkRedisRepo(RedisTemplate template) {
        this.template = template;
    }

    public LinkRedis save(LinkRedis LinkRedis){
        template.opsForHash().put(HASH_KEY,LinkRedis.getId(),LinkRedis);
        return LinkRedis;
    }
    public LinkRedis findtById(String id){
        return (LinkRedis) template.opsForHash().get(HASH_KEY,id);
    }

}
