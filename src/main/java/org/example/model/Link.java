package org.example.model;


import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.Const;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "links")
public class Link {
    @Id
    @Column(name = "id_link")
    private String id;
    @Column(name = "origin")
    private String origin;
    @Column(name = "date_del")
    private Date dateDel;
    public String getGeneratedLink(){
        if(origin != null)
            return Const.urlRedirect + id;
        return null;
    }
    @ManyToOne
    @JoinColumn(name="id_user")
    private User user;

}
