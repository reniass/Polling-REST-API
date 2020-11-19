package com.reinke.polls.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    private RoleName name;

    public Role(String grantedAuthority) {
        this.name = RoleName.valueOf(grantedAuthority);
    }
}
