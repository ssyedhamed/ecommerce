package com.syedhamed.ecommerce.model;

import com.syedhamed.ecommerce.enums.SellerApplicationStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@Table(name = "users")

public class User  {


    public User() {
        this.accountNonLocked = true;
        this.enabled = true;
        this.emailVerified = false;
        this.createdAt = LocalDateTime.now();
        this.deleted = false;
        this.updatedAt = LocalDateTime.now();
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String email;    // unique
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String profileImage;
    @Enumerated(EnumType.STRING)
    @Column(name = "seller_application_status", nullable = false)
    private SellerApplicationStatus sellerApplicationStatus = SellerApplicationStatus.NOT_REQUESTED;
    /*
    accountNonLocked
        🔒 This typically means the user has violated something:
        Entered wrong password too many times
        Triggered a fraud detection rule
        🚫 It’s a temporary block — user might get unlocked later manually or automatically
        ✅ Usually used in authentication logic like:
     */
    private boolean accountNonLocked;
    /*
            ✅ enabled
        📴 This is more of a global on/off switch
        ❌ If false, it’s like saying: “This user is not allowed to use the app at all”
        Maybe they haven’t verified their email yet
        Maybe admin disabled them
        ✅ Used by Spring Security for account validity checks:
     */
    private boolean enabled;
    private boolean emailVerified;
    //"soft delete" and restore mechanism for it's related entities
    private boolean deleted;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private boolean deactivated;
    private LocalDateTime deactivatedAt;

//    @ToString.Exclude
//    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

//    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

//    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "seller", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Set<Product> products;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

}
