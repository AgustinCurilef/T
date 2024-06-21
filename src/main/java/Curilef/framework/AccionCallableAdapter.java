package Curilef.framework;

import java.util.concurrent.Callable;

public class AccionCallableAdapter implements Callable<Void> {
    private final Accion accion;

    public AccionCallableAdapter(Accion accion) {
        this.accion = accion;
    }

    @Override
    public Void call() throws Exception {
        Thread.sleep(1000);
        accion.ejecutar();

        return null;
    }
}
