package Curilef.framework;

import java.util.concurrent.Callable;

public class AccionCallableAdapter implements Callable<Void> {
    private final Accion accion;

    public AccionCallableAdapter(Accion accion) {
        this.accion = accion;
    }

    @Override
    public Void call() throws Exception {
        accion.ejecutar();
        return null;
    }
}
