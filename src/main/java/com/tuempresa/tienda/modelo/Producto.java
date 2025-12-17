package com.tuempresa.tienda.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
// 🛑 Asegura que la importación de Jackson esté presente
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "productos")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Corta la metadata de JPA/Hibernate
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private BigDecimal precio;

    @Column(nullable = true)
    private String urlImagen;

    @Column(nullable = false)
    private boolean oferta = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)

    @JsonIgnoreProperties("productos")
    private Categoria categoria;
}