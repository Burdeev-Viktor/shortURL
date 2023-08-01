package org.example.dtos.link;

import lombok.Data;
import org.example.model.Link;

import java.util.Date;

@Data
public class LinkResponse {
    private String id;
    private String origin;
    private Date dateDel;
    private boolean active;
    public LinkResponse(){};
    public LinkResponse(Link link){
        id = link.getId();
        origin = link.getOrigin();
        dateDel = link.getDateDel();
        active = link.isActive();
    }
}
