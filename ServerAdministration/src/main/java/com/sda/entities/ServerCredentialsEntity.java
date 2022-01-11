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
@Table(name = "servers")
public class ServerCredentialsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer serverId;

    @Column(length = 20)
    private String serverName;

    @Column(length = 16)
    private String serverIp;

    @Column(length = 30)
    private String username;

    @Column(length = 150)
    private String password;

    @Column(columnDefinition = "TEXT")
    private String privateKey;

    @Column
    private Integer port;

    @Column
    private byte[] iv;

    @Transient
    private String masterPassword;

    @Column
    private boolean withPKey;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonBackReference
    private UserEntity userEntity;

}
