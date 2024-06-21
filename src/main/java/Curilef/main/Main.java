package Curilef.main;

import Curilef.framework.framework;

public class Main {
    public static void main(String[] args) {
        try {
            framework framework = new framework("src/main/resources/config.json");
            framework.mostrarMenu();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
