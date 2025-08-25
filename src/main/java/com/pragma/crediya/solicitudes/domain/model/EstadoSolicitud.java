package com.pragma.crediya.solicitudes.domain.model;

public enum EstadoSolicitud {
    PENDIENTE_REVISION("Pendiente de revisión"),
    EN_EVALUACION("En evaluación"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada"),
    DESEMBOLSADA("Desembolsada");
    
    private final String descripcion;
    
    EstadoSolicitud(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
