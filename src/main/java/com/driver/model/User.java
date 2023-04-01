package com.driver.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String originalIp;
    private String maskedIp;
    private Boolean connected;

    @ManyToMany
    @JoinColumn
    List<ServiceProvider> serviceProviders = new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    List<Connection> connectionList = new ArrayList<>();

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    Country originalCountry;


}
