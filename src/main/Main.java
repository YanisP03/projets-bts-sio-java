// === Main.java ===
package main;

import javax.swing.SwingUtilities;
import java.sql.Connection;

import view.FenetreConnexion;
import bdd.DatabaseConnection;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Connection connexion = DatabaseConnection.getConnection();
                new FenetreConnexion(connexion);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}