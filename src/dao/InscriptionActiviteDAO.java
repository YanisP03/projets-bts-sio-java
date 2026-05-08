package dao;
import java.sql.*;

public class InscriptionActiviteDAO {
    private Connection connection;

    public InscriptionActiviteDAO(Connection connection) {
        this.connection = connection;
    }

    // Méthode pour inscrire un participant (utilisateur ou enfant) à une activité
    public boolean inscrireParticipant(int participantId, int calendrierId) throws SQLException {
        String query = "INSERT INTO inscription_activite (participant_id, calendrier_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, participantId); // participantId peut être utilisateurId ou enfantId
            stmt.setInt(2, calendrierId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Récupérer la clé générée 
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        System.out.println("Inscription réussie avec l'ID : " + id);
                    }
                }
                return true;
            }
            return false;
        }
    }
}