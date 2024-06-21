package Curilef.main;

import Curilef.framework.MenuDeAcciones;

public class Main {
    public static void main(String[] args) {
        try {
            MenuDeAcciones framework = new MenuDeAcciones("src/main/resources/config.json");
            framework.mostrarMenu();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
