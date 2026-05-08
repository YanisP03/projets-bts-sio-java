package dao;

import java.sql.*; // JDBC pour la connexion à la BDD
import model.Activite; // Classe modèle Activite
import java.util.ArrayList;
import java.util.List;

public class ActiviteDAO {
    private final Connection connection; // Connexion à la base de données

    public ActiviteDAO(Connection connection) {
        this.connection = connection;
    }

    // Récupère les activités correspondant à une tranche d'âge donnée
    public List<Activite> getActivitesParTrancheAge(int age) throws SQLException {
        String query = (age == 0)
            ? "SELECT * FROM activite" // Si age = 0, retourne toutes les activités
            : "SELECT * FROM activite WHERE age_min <= ? AND age_max >= ?"; // Sinon, filtre par âge
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            if (age != 0) {
                stmt.setInt(1, age);
                stmt.setInt(2, age);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                List<Activite> activites = new ArrayList<>();
                while (rs.next()) {
                    activites.add(mapToActivite(rs)); // Mapper les résultats en objets Activite
                }
                return activites;
            }
        }
    }

    // Récupère toutes les activités sans filtre
    public List<Activite> getToutesActivites() throws SQLException {
        String query = "SELECT * FROM activite";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            List<Activite> activites = new ArrayList<>();
            while (rs.next()) {
                activites.add(mapToActivite(rs));
            }
            return activites;
        }
    }

    // Ajoute une nouvelle activité dans la base de données
    public boolean ajouterActivite(Activite activite) throws SQLException {
        String query = "INSERT INTO activite (nom, description, age_min, age_max) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, activite.getNom());
            stmt.setString(2, activite.getDescription());
            stmt.setInt(3, activite.getAgeMin());
            stmt.setInt(4, activite.getAgeMax());
            return stmt.executeUpdate() > 0;
        }
    }

    // Met à jour les informations d'une activité existante
    public boolean modifierActivite(Activite activite) throws SQLException {
        String query = "UPDATE activite SET nom = ?, description = ?, age_min = ?, age_max = ? WHERE activite_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, activite.getNom());
            stmt.setString(2, activite.getDescription());
            stmt.setInt(3, activite.getAgeMin());
            stmt.setInt(4, activite.getAgeMax());
            stmt.setInt(5, activite.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    // Supprime une activité de la base via son ID
    public boolean supprimerActivite(int activiteId) throws SQLException {
        String query = "DELETE FROM activite WHERE activite_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, activiteId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Supprime une activité ainsi que ses dépendances (calendrier, évaluation)
    public boolean supprimerActiviteComplet(int activiteId) throws SQLException {
        String deleteEvaluations = "DELETE FROM evaluation WHERE activite_id = ?";
        String deleteCalendrier = "DELETE FROM calendrier WHERE activite_id = ?";
        String deleteActivite = "DELETE FROM activite WHERE activite_id = ?";

        try (
            PreparedStatement stmtEvaluations = connection.prepareStatement(deleteEvaluations);
            PreparedStatement stmtCalendrier = connection.prepareStatement(deleteCalendrier);
            PreparedStatement stmtActivite = connection.prepareStatement(deleteActivite)
        ) {
            connection.setAutoCommit(false); // Démarre une transaction

            stmtEvaluations.setInt(1, activiteId);
            stmtEvaluations.executeUpdate();

            stmtCalendrier.setInt(1, activiteId);
            stmtCalendrier.executeUpdate();

            stmtActivite.setInt(1, activiteId);
            boolean result = stmtActivite.executeUpdate() > 0;

            connection.commit(); // Valide la transaction
            return result;
        } catch (SQLException e) {
            connection.rollback(); // Annule la transaction en cas d'erreur
            throw e;
        } finally {
            connection.setAutoCommit(true); // Restaure le mode auto-commit
        }
    }

    // Liste toutes les activités (fonction redondante avec getToutesActivites)
    public List<Activite> listerActivites() throws SQLException {
        String query = "SELECT * FROM activite";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            List<Activite> activites = new ArrayList<>();
            while (rs.next()) {
                activites.add(mapToActivite(rs));
            }
            return activites;
        }
    }

    // Inscription d'un participant adulte à une activité
    public boolean inscrireParticipant(int participantId, int calendrierId) throws SQLException {
        String query = "INSERT INTO inscription_activite (utilisateur_id, calendrier_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, participantId);
            stmt.setInt(2, calendrierId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Inscription d'un enfant à une activité
    public boolean inscrireEnfant(int enfantId, int calendrierId) throws SQLException {
        String query = "INSERT INTO inscription_activite (enfant_id, calendrier_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, enfantId);
            stmt.setInt(2, calendrierId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Enregistre une évaluation d'activité par un utilisateur
    public boolean evaluerActivite(int participantId, int activiteId, int note, String commentaire) throws SQLException {
        String query = "INSERT INTO evaluation (utilisateur_id, activite_id, note, commentaire) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, participantId);
            stmt.setInt(2, activiteId);
            stmt.setInt(3, note);
            stmt.setString(4, commentaire);
            return stmt.executeUpdate() > 0;
        }
    }

    // Récupère les activités auxquelles un utilisateur est inscrit
    public List<Activite> getActivitesInscrites(int utilisateurId) throws SQLException {
        String query = """
            SELECT DISTINCT a.*
            FROM activite a
            JOIN calendrier c ON a.activite_id = c.activite_id
            JOIN inscription_activite ia ON c.calendrier_id = ia.calendrier_id
            WHERE ia.utilisateur_id = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, utilisateurId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Activite> activites = new ArrayList<>();
                while (rs.next()) {
                    activites.add(mapToActivite(rs));
                }
                return activites;
            }
        }
    }

    // Récupère les activités auxquelles les enfants d’un utilisateur sont inscrits
    public List<String> getActivitesEnfantsAvecPrenoms(int utilisateurId) throws SQLException {
        String query = """
            SELECT e.prenom, e.nom AS nom_enfant, a.nom AS activite_nom
            FROM inscription_activite ia
            JOIN enfant e ON ia.enfant_id = e.enfant_id
            JOIN calendrier c ON ia.calendrier_id = c.calendrier_id
            JOIN activite a ON c.activite_id = a.activite_id
            WHERE e.utilisateur_id = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, utilisateurId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<String> resultats = new ArrayList<>();
                while (rs.next()) {
                    String prenom = rs.getString("prenom");
                    String nom = rs.getString("nom_enfant");
                    String activite = rs.getString("activite_nom");
                    resultats.add(prenom + " " + nom + " est inscrit à : " + activite);
                }
                return resultats;
            }
        }
    }

    // Récupère toutes les évaluations avec infos utilisateur et activité
    public List<String> getToutesEvaluations() throws SQLException {
        String query = """
            SELECT e.note, e.commentaire, u.nom AS nom_utilisateur, a.nom AS nom_activite
            FROM evaluation e
            JOIN utilisateur u ON e.utilisateur_id = u.utilisateur_id
            JOIN activite a ON e.activite_id = a.activite_id
            ORDER BY a.nom, e.note DESC
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            List<String> evaluations = new ArrayList<>();
            while (rs.next()) {
                String ligne = String.format(
                    "Activité: %s | Utilisateur: %s | Note: %d | Commentaire: %s",
                    rs.getString("nom_activite"),
                    rs.getString("nom_utilisateur"),
                    rs.getInt("note"),
                    rs.getString("commentaire")
                );
                evaluations.add(ligne);
            }
            return evaluations;
        }
    }

    // Convertit un ResultSet en objet Activite
    private Activite mapToActivite(ResultSet rs) throws SQLException {
        return new Activite(
            rs.getInt("activite_id"),
            rs.getString("nom"),
            rs.getString("description"),
            rs.getInt("age_min"),
            rs.getInt("age_max")
        );
    }
}
