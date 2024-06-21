package Curilef.utilizacion;

import Curilef.framework.Accion;

public class AccionTres implements Accion {
    @Override
    public void ejecutar() {
        System.out.println("Ejecutando AccionTres...");
    }

    @Override
    public String nombreItemMenu() {
        return "Accion 3";
    }

    @Override
    public String descripcionItemMenu() {
        return "Esto Reproduce una cancion";
    }
}
