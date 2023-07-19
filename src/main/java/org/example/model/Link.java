package org.example.model;


import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.Const;

import java.text.SimpleDateFormat;
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
    @ManyToOne
    @JoinColumn(name="id_user")
    private User user;
    @Column(name = "active")
    private boolean active;

    public String getDate(){
        String pattern = "MM-dd-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(dateDel);
    }
    public String getGeneratedLink(){
        if(origin != null)
            return Const.urlRedirect + id;
        return null;
    }
    public String toString() {
        String var10000 = this.getId();
        return "Link(\n id=" + var10000 + ",\n origin=" + this.getOrigin() + ",\n dateDel=" + String.valueOf(this.getDateDel()) + ",\n user=" + String.valueOf(this.getUser()) + ")";
    }
}
