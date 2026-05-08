package view;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import model.Notification;
import dao.NotificationDAO;

/**
 * Fenêtre permettant d'afficher les notifications pour un utilisateur.
 * Elle récupère les notifications à venir depuis la base de données via {@link NotificationDAO}.
 */
public class FenetreNotification extends JFrame {
    private JTextArea textArea; // Zone de texte pour afficher les notifications
    private NotificationDAO notificationDAO; // DAO pour interagir avec les notifications

    /**
     * Constructeur de la fenêtre de notifications.
     *
     * @param connection Connexion à la base de données.
     * @param participantId ID de l'utilisateur dont on souhaite afficher les notifications.
     */
    public FenetreNotification(Connection connection, int participantId) {
        // Configuration de la fenêtre
        setTitle("Notifications");
        setSize(400, 300);
        setLocationRelativeTo(null); // Centre la fenêtre à l'écran
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Ferme uniquement cette fenêtre

        // Initialisation du DAO pour les notifications
        notificationDAO = new NotificationDAO(connection);

        // Création de la zone de texte non éditable
        textArea = new JTextArea();
        textArea.setEditable(false);

        // Ajout de la zone de texte à la fenêtre avec une barre de défilement
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        try {
            // Récupération des notifications depuis la base de données
            List<String> notifications = notificationDAO.getNotificationsProchainesActivites(participantId);

            // Construction de la chaîne de texte à afficher
            StringBuilder sb = new StringBuilder();
            for (String notif : notifications) {
                sb.append("- ").append(notif).append("\n");
            }

            // Affichage dans la zone de texte
            textArea.setText(sb.toString());
        } catch (SQLException e) {
            // Affichage du message d'erreur en cas d'exception SQL
            textArea.setText("Erreur : " + e.getMessage());
        }

        // Rend la fenêtre visible
        setVisible(true);
    }
}
