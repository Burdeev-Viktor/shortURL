package org.example.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column(name = "`generated`")
    private String generated;
    @Column(name = "origin")
    private String origin;
    @Column(name = "date_del")
    private Date dateDel;
    public String getGeneratedLink(){
        if(origin != null)
            return Const.urlRedirect + generated;
        return null;
    }

}
