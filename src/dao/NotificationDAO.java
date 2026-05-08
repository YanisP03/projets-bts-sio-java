package dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    // Connexion à la base de données
    private Connection connection;

    // Constructeur qui initialise la connexion
    public NotificationDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Méthode qui retourne une liste de notifications concernant les prochaines activités
     * pour un utilisateur donné (y compris ses enfants).
     *
     * @param utilisateurId l'ID de l'utilisateur
     * @return liste de notifications (sous forme de chaînes de caractères)
     */
    public List<String> getNotificationsProchainesActivites(int utilisateurId) throws SQLException {
        List<String> notifications = new ArrayList<>();
        LocalDate aujourdHui = LocalDate.now(); // Date du jour pour comparaison

        // 🔔 Première requête : récupérer les activités futures pour l'utilisateur lui-même
        String query = """
            SELECT a.nom AS nom_activite, c.debut AS date_activite
            FROM inscription_activite ia
            JOIN calendrier c ON ia.calendrier_id = c.calendrier_id
            JOIN activite a ON c.activite_id = a.activite_id
            WHERE ia.utilisateur_id = ? AND c.debut >= NOW()
            ORDER BY c.debut
        """;

        // Exécution de la requête pour l'utilisateur
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, utilisateurId); // Injection du paramètre utilisateur
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nomActivite = rs.getString("nom_activite");
                    Timestamp debut = rs.getTimestamp("date_activite");

                    // Conversion de la date SQL vers LocalDate
                    LocalDate dateActivite = debut.toLocalDateTime().toLocalDate();
                    long joursRestants = ChronoUnit.DAYS.between(aujourdHui, dateActivite);

                    // Construction du message de notification
                    String notif = String.format(
                        "📅 Rappel : Vous êtes inscrit(e) à l'activité \"%s\" prévue le %s (dans %d jour%s).",
                        nomActivite, dateActivite, joursRestants, (joursRestants == 1 ? "" : "s")
                    );
                    notifications.add(notif);
                }
            }
        }

        // 🔔 Deuxième requête : récupérer les activités futures des enfants de l'utilisateur
        String queryEnfants = """
            SELECT e.prenom, a.nom AS nom_activite, c.debut AS date_activite
            FROM inscription_activite ia
            JOIN calendrier c ON ia.calendrier_id = c.calendrier_id
            JOIN activite a ON c.activite_id = a.activite_id
            JOIN enfant e ON ia.enfant_id = e.enfant_id
            WHERE e.utilisateur_id = ? AND c.debut >= NOW()
            ORDER BY c.debut
        """;

        // Exécution de la requête pour les enfants de l'utilisateur
        try (PreparedStatement stmt = connection.prepareStatement(queryEnfants)) {
            stmt.setInt(1, utilisateurId); // Injection de l'ID utilisateur
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String prenom = rs.getString("prenom");
                    String nomActivite = rs.getString("nom_activite");
                    Timestamp debut = rs.getTimestamp("date_activite");

                    // Conversion de la date SQL vers LocalDate
                    LocalDate dateActivite = debut.toLocalDateTime().toLocalDate();
                    long joursRestants = ChronoUnit.DAYS.between(aujourdHui, dateActivite);

                    // Construction du message de notification pour l'enfant
                    String notif = String.format(
                        "📌 Rappel : %s est inscrit(e) à l'activité \"%s\" le %s (dans %d jour%s).",
                        prenom, nomActivite, dateActivite, joursRestants, (joursRestants == 1 ? "" : "s")
                    );
                    notifications.add(notif);
                }
            }
        }

        return notifications;
    }
}
