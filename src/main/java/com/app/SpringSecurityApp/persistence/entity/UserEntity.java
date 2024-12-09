package com.app.SpringSecurityApp.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private String email;

    @Column()
    private String password;

    @Column()
    private String fullName;

    @Column()
    private String ci;

    @Column()
    private String nationality;

    @Column()
    private String phone;


    @Column()
    private Date dateOfBirth;

    @Column(updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAtDate ;

    @Column()
    private Date updateAtDate;

    @Column()
    private Date deleteAtDate;

    @Enumerated(EnumType.STRING)
    @Column()
    private UserStatus statusAccount;

    @Column()
    private Boolean isEnabled;

    @Column()
    private Boolean isAccountNonExpired;

    @Column()
    private Boolean isAccountNonLocked;

    @Column()
    private Boolean isCredentialsNonExpired;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_role"
            , joinColumns = @JoinColumn(name = "user_id")
            ,inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();

    @OneToMany( mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<TokenEntity> tokens = new ArrayList<>() ;

    @PrePersist
    protected void onCreate() {
        this.createAtDate = new Date();
        if(this.statusAccount == null) {
            this.statusAccount = UserStatus.ACTIVE;
        }
    }


}