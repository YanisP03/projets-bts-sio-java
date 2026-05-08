package view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Notification;
import dao.NotificationDAO;

/**
 * Fenêtre permettant d'afficher les notifications de tous les utilisateurs.
 * Chaque utilisateur est listé avec ses prochaines notifications d'activités.
 */
public class FenetreNotificationsGlobales extends JFrame {
    private Connection connexion; // Connexion à la base de données

    /**
     * Constructeur de la fenêtre globale des notifications.
     * @param connexion Connexion à la base de données.
     */
    public FenetreNotificationsGlobales(Connection connexion) {
        this.connexion = connexion;

        // Configuration de la fenêtre
        setTitle("🔔 Notifications de tous les utilisateurs");
        setSize(600, 400);
        setLocationRelativeTo(null); // Centre la fenêtre
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fermer uniquement cette fenêtre

        // Création de la zone de texte pour afficher les notifications
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false); // L'utilisateur ne peut pas modifier ce texte
        JScrollPane scrollPane = new JScrollPane(textArea); // Ajout d'une barre de défilement

        // Récupération des notifications depuis la base
        ArrayList<String> notifications = recupererToutesLesNotifications();

        // Affichage des notifications dans la zone de texte
        for (String notif : notifications) {
            textArea.append(notif + "\n\n");
        }

        // Ajout de la zone de texte à la fenêtre
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true); // Rend la fenêtre visible
    }

    /**
     * Récupère les notifications de tous les utilisateurs depuis la base de données.
     * @return Une liste de chaînes formatées contenant les notifications par utilisateur.
     */
    private ArrayList<String> recupererToutesLesNotifications() {
        ArrayList<String> liste = new ArrayList<>();
        String queryUtilisateurs = "SELECT utilisateur_id, nom, prenom FROM utilisateur";

        try (
            PreparedStatement stmt = connexion.prepareStatement(queryUtilisateurs);
            ResultSet rs = stmt.executeQuery()
        ) {
            NotificationDAO notificationDAO = new NotificationDAO(connexion);

            // Pour chaque utilisateur, on récupère les notifications
            while (rs.next()) {
                int utilisateurId = rs.getInt("utilisateur_id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String nomComplet = prenom + " " + nom;

                // Récupération des notifications de l'utilisateur
                List<String> notifs = notificationDAO.getNotificationsProchainesActivites(utilisateurId);

                // Si des notifications existent, on les ajoute à la liste
                if (!notifs.isEmpty()) {
                    liste.add("👤 Notifications pour " + nomComplet + " :");
                    for (String notif : notifs) {
                        liste.add("  - " + notif);
                    }
                    liste.add(""); // Ligne vide pour séparer les utilisateurs
                }
            }

        } catch (SQLException e) {
            // En cas d'erreur SQL, on ajoute un message dans la liste
            liste.add("Erreur lors de la récupération : " + e.getMessage());
        }

        return liste;
    }
}
