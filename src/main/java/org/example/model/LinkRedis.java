package org.example.model;


import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@RedisHash("Links")
public class LinkRedis implements Serializable {
    @Id
    private String id;
    private String origin;

    public LinkRedis(Link link) {
        this.id = link.getGenerated();
        this.origin = link.getOrigin();
    }

    public LinkRedis() {
    }
}
