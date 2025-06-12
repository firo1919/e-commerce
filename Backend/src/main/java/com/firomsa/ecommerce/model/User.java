package com.firomsa.ecommerce.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@AllArgsConstructor
@ToString
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String userName;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;

    @NotNull
    private Role role;

    @NotNull
    private boolean isActive = Boolean.TRUE;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

}
