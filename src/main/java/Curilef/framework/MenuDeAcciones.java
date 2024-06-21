package Curilef.framework;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

public class MenuDeAcciones {
    private static final String CLASES_ACCIONES_KEY = "acciones";
    private static final String MAX_THREADS = "max-threads";
    private final List<Accion> acciones = new ArrayList<>();
    private final List<Accion> accionesSeleccionadas = new ArrayList<>();
    private int maxThreads = 1;

    public MenuDeAcciones(String configFile) throws Exception {
        if (!configFile.endsWith(".properties") && !configFile.endsWith(".json")) {
            throw new IllegalArgumentException("El archivo de configuración debe ser un archivo .properties o .json");
        }
        if (configFile.endsWith(".properties")) cargarDesdeProperties(configFile);
        else cargarDesdeJson(configFile);
    }

    private void cargarDesdeProperties(String configFile) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream(configFile));
        String accionesConfiguracion = properties.getProperty(CLASES_ACCIONES_KEY);
        if (accionesConfiguracion != null) {
            String[] clasesAcciones = accionesConfiguracion.split(";");
            for (String claseAccion : clasesAcciones) {
                // Eliminar espacios en blanco y comillas
                claseAccion = claseAccion.trim();
                crearAccion(claseAccion);
            }
        }
    }

    private void crearAccion(String claseAccion) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // Cargar la clase utilizando su nombre completo mediante reflexión de Java
        Class<?> clase = Class.forName(claseAccion);
        // Crear una instancia de la clase y le agrega la descripción de la acción
        Accion accion = (Accion) clase.getDeclaredConstructor().newInstance();
        acciones.add(accion);
    }

    private void cargarDesdeJson(String configFile) throws Exception {
        try (Reader reader = new FileReader(configFile)) {
            JSONObject jsonObject = new JSONObject(new JSONTokener(reader));
            JSONArray accionesArray = jsonObject.getJSONArray(CLASES_ACCIONES_KEY);
            maxThreads = jsonObject.getInt(MAX_THREADS);
            for (int i = 0; i < accionesArray.length(); i++) {
                String claseAccion = accionesArray.getString(i);
                crearAccion(claseAccion);
            }
        }
    }

    public void mostrarMenu() {
        try {
            // Crea una terminalFactory por defecto
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            // Crea una pantalla utilizando la fábrica de terminales
            Screen screen = terminalFactory.createScreen();
            // Inicia la pantalla
            screen.startScreen();
            // Crea un panel utilizando el layout de grilla
            Panel panel = new Panel();
            panel.setLayoutManager(new GridLayout(1));
            // Agrega los botones a la panel
            for (int i = 0; i < acciones.size(); i++) {
                Accion accion = acciones.get(i);
                // Crea un botón con el nombre del item del menú y la acción asociada
                Button button = new Button(accion.nombreItemMenu() + accion.descripcionItemMenu(), () -> {
                    accionesSeleccionadas.add(accion);
                });
                // Agrega el botón a la panel
                panel.addComponent(button);
            }
            Button ejecutarButton = new Button("Ejecutar", () -> {
                ejecutarMultiThreaded();
            });
            // Crea un botón para salir
            Button salirButton = new Button("Salir", () -> {
                try {
                    // Define lo que pasa cuando el botón es presionado: detiene la pantalla
                    screen.stopScreen();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            panel.addComponent(ejecutarButton);
            panel.addComponent(salirButton);

            // Crea una ventana básica
            BasicWindow window = new BasicWindow();
            // Establece el panel como el componente de la ventana
            window.setComponent(panel);
            // Crea un gestor de interfaces de texto para manejar la pantalla y la ventana
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace());
            // Añade la ventana al gestor de interfaces y espera hasta que se cierre
            gui.addWindowAndWait(window);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ejecutarMultiThreaded() {
        // Crea un executor con un número máximo de hilos
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        List<Future<Void>> futures = new ArrayList<>();

        for (Accion accion : accionesSeleccionadas) {
            Callable<Void> callable = new AccionCallableAdapter(accion);
            Future<Void> future = executor.submit(callable);
            futures.add(future);
        }

        // Esperar a que todas las tareas se completen
        for (Future<Void> future : futures) {
            try {
                future.get(); // Espera hasta que la tarea se complete
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
        accionesSeleccionadas.clear();
    }
     /* public void mostrarMenu() {
         Scanner scanner = new Scanner(System.in);
         while (true) {
             System.out.println("Bienvenido, estas son sus opciones:");
             for (int i = 0; i < acciones.size(); i++) {
                 Accion accion = acciones.get(i);
                 System.out.println((i + 1) + ". " + accion.nombreItemMenu() + " (" + accion.descripcionItemMenu() + ")");
             }
             System.out.println((acciones.size() + 1) + ". Salir");

             System.out.print("Ingrese su opción: ");
             int opcion = scanner.nextInt();

             if (opcion == acciones.size() + 1) {
                 System.out.println("Saliendo...");
                 break;
             }
             if (opcion <= 0 || opcion > acciones.size()) {
                 System.out.println("Opción inválida, intente nuevamente.");
             }
             Accion accion = acciones.get(opcion - 1);
             accion.ejecutar();
         }
         scanner.close();
     }*/
}