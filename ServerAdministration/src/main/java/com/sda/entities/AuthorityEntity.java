package com.sda.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "authorities")
public class AuthorityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer authoritiesId;

    @Column(length = 20)
    private String username;

    @Column(length = 30)
    private String authority;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonBackReference
    private UserEntity user;

}
