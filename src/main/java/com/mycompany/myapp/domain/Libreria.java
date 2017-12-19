package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Libreria.
 */
@Entity
@Table(name = "libreria")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Libreria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "cod_libreria", nullable = false)
    private String codLibreria;

    @Column(name = "capacidad")
    private Integer capacidad;

    @OneToMany(mappedBy = "libreria")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Ejemplar> ejemplars = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodLibreria() {
        return codLibreria;
    }

    public Libreria codLibreria(String codLibreria) {
        this.codLibreria = codLibreria;
        return this;
    }

    public void setCodLibreria(String codLibreria) {
        this.codLibreria = codLibreria;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public Libreria capacidad(Integer capacidad) {
        this.capacidad = capacidad;
        return this;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public Set<Ejemplar> getEjemplars() {
        return ejemplars;
    }

    public Libreria ejemplars(Set<Ejemplar> ejemplars) {
        this.ejemplars = ejemplars;
        return this;
    }

    public Libreria addEjemplar(Ejemplar ejemplar) {
        this.ejemplars.add(ejemplar);
        ejemplar.setLibreria(this);
        return this;
    }

    public Libreria removeEjemplar(Ejemplar ejemplar) {
        this.ejemplars.remove(ejemplar);
        ejemplar.setLibreria(null);
        return this;
    }

    public void setEjemplars(Set<Ejemplar> ejemplars) {
        this.ejemplars = ejemplars;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Libreria libreria = (Libreria) o;
        if (libreria.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), libreria.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Libreria{" +
            "id=" + getId() +
            ", codLibreria='" + getCodLibreria() + "'" +
            ", capacidad=" + getCapacidad() +
            "}";
    }
}
