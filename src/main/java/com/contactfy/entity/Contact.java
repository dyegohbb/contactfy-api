package com.contactfy.entity;

import java.time.LocalDateTime;

import com.contactfy.entity.converter.BooleanConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contato", schema = "desafio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contato_id")
    private Long id;

    @Column(name = "contato_nome", length = 100)
    private String name;

    @Column(name = "contato_email", length = 255)
    private String email;

    @Column(name = "contato_celular", length = 11)
    private String cellphone;

    @Column(name = "contato_telefone", length = 10)
    private String phone;

    @Convert(converter = BooleanConverter.class)
    @Column(name = "contato_sn_favorito")
    private Boolean favorite;

    @Convert(converter = BooleanConverter.class)
    @Column(name = "contato_sn_ativo", length = 1)
    private Boolean active;

    @Column(name = "contato_dh_cad")
    private LocalDateTime createdAt;
    
    @Column(name = "identificador", length = 50)
    private String identifier;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false)
    private User user;

}
