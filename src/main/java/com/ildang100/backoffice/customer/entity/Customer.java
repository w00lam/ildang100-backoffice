package com.ildang100.backoffice.customer.entity;

import com.ildang100.backoffice.common.entity.BaseEntity;
import com.ildang100.backoffice.common.enums.CustomerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "customers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Email
    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 30)
    private String tele;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerStatus status;

    public void update(String name, String email, String tele) {
        if (name != null) {
            this.name = name;
        }

        if (email != null) {
            this.email = email;
        }

        if (tele != null) {
            this.tele = tele;
        }
    }
}
