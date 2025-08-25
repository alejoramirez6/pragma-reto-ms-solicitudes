package com.pragma.crediya.solicitudes.domain.model;

public enum TipoPrestamo {
    PERSONAL("Personal"),
    VEHICULAR("Vehicular"), 
    HIPOTECARIO("Hipotecario"),
    EDUCATIVO("Educativo"),
    EMPRESARIAL("Empresarial");
    
    private final String descripcion;
    
    TipoPrestamo(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
