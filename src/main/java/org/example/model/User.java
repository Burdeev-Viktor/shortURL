package org.example.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "users")
public class User implements Serializable {
    @Id
    @Column(name = "id_user")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "login")
    private String login;
    @Column(name = "password")
    private String password;
    @Column(name = "name")
    private String name;
    @OneToMany
    @JoinColumn(name = "id_link")
    private Set<Link> items;
    @Transient
    private String rPassword;
    public String toString() {
        Long var10000 = this.getId();
        return "User(\nid=" + var10000 + ",\n login=" + this.getLogin() + ",\n password=" + this.getPassword() + ",\n name=" + this.getName() + ")";
    }
}
