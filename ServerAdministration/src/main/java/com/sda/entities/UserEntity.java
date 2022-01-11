package com.sda.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(length = 20, unique = true)
    private String username;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 100)
    private String password;

    @Column(length = 50)
    private String firstName;

    @Column(length = 50)
    private String lastName;

    @Column(length = 1000)
    private byte[] profilePicture;
    
    private boolean enabled;

    @OneToMany(fetch = FetchType.EAGER ,mappedBy = "user")
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonManagedReference
    private List<AuthorityEntity> authorityEntity;

    @OneToMany(fetch = FetchType.EAGER ,mappedBy = "userEntity")
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonManagedReference
    private List<ServerCredentialsEntity> servers;

}
