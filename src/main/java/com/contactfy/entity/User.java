package com.contactfy.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario", indexes = {
    @Index(name = "idx_usuario_nome_usuario", columnList = "nome_usuario"),
    @Index(name = "idx_usuario_email", columnList = "email"),
    @Index(name = "idx_usuario_identificador", columnList = "identificador")
})
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "identificador", nullable = false, unique = true)
    private String identifier;

    @Column(name = "nome_usuario", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "senha", nullable = false)
    private String password;

    @Column(name = "excluido", nullable = false)
    private boolean deleted;

    @Column(name = "excluido_em")
    private LocalDateTime deletedOn;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "atualizado_em")
    private LocalDateTime updatedOn;

    @PrePersist
    protected void onCreate() {
        this.createdOn = LocalDateTime.now();
    }

    public void logicDelete() {
        this.deleted = true;
        this.deletedOn = LocalDateTime.now();
        this.updatedOn = LocalDateTime.now();
    }

    public void activeUser() {
        this.deleted = false;
        this.deletedOn = null;
        this.updatedOn = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>();
    }
    
    @Override
    public boolean isEnabled() {
	return !deleted;
    }
}
