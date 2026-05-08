package dao;
import java.sql.*;

import model.Calendrier;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CalendrierDAO {
    private final Connection connection;

    public CalendrierDAO(Connection connection) {
        this.connection = connection;
    }

    // Méthode pour afficher tout le calendrier, y compris les activités sans participants
    public String afficherCalendrier() throws SQLException {
        StringBuilder sb = new StringBuilder();
        String query = """
            SELECT c.calendrier_id, a.nom AS activite_nom, c.debut, c.fin, c.lieu
            FROM calendrier c
            JOIN activite a ON c.activite_id = a.activite_id
            ORDER BY c.debut
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            while (rs.next()) {
                sb.append("• Activité : ").append(rs.getString("activite_nom")).append("\n")
                  .append("   ➤ Début : ").append(sdf.format(rs.getTimestamp("debut"))).append("\n")
                  .append("   ➤ Fin   : ").append(sdf.format(rs.getTimestamp("fin"))).append("\n")
                  .append("   ➤ Lieu  : ").append(rs.getString("lieu")).append("\n\n");
            }
        }
        return sb.toString();
    }

    // Retourne tous les objets calendrier pour choix dans JComboBox
    public List<Calendrier> getCalendrier() throws SQLException {
        List<Calendrier> calendriers = new ArrayList<>();
        String query = """
            SELECT c.calendrier_id, a.nom AS activite_nom, c.debut, c.fin, c.lieu, a.activite_id
            FROM calendrier c
            JOIN activite a ON c.activite_id = a.activite_id
            ORDER BY c.debut
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Calendrier calendrier = new Calendrier(
                    rs.getInt("calendrier_id"),
                    rs.getInt("activite_id"),
                    rs.getTimestamp("debut"),
                    rs.getTimestamp("fin"),
                    rs.getString("lieu"),
                    rs.getString("activite_nom")
                );
                calendriers.add(calendrier);
            }
        }
        return calendriers;
    }

    // Récupère un calendrier spécifique lié à une activité
    public Calendrier getCalendrierByActiviteId(int activiteId) throws SQLException {
        String query = """
            SELECT c.calendrier_id, c.debut, c.fin, c.lieu
            FROM calendrier c
            WHERE c.activite_id = ?
            LIMIT 1
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, activiteId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Calendrier(
                    rs.getInt("calendrier_id"),
                    activiteId,
                    rs.getTimestamp("debut"),
                    rs.getTimestamp("fin"),
                    rs.getString("lieu")
                );
            }
        }
        return null;
    }

    // Méthode pour ajouter un calendrier
    public void ajouterCalendrier(String nomActivite, Timestamp debut, Timestamp fin, String lieu) throws SQLException {
        String getIdQuery = "SELECT activite_id FROM activite WHERE nom = ?";
        String insertQuery = "INSERT INTO calendrier (activite_id, debut, fin, lieu) VALUES (?, ?, ?, ?)";

        connection.setAutoCommit(false);

        try (PreparedStatement stmt1 = connection.prepareStatement(getIdQuery);
             PreparedStatement stmt2 = connection.prepareStatement(insertQuery)) {

            stmt1.setString(1, nomActivite);
            ResultSet rs = stmt1.executeQuery();

            if (rs.next()) {
                int activiteId = rs.getInt("activite_id");

                stmt2.setInt(1, activiteId);
                stmt2.setTimestamp(2, debut);
                stmt2.setTimestamp(3, fin);
                stmt2.setString(4, lieu);

                stmt2.executeUpdate();
                connection.commit();
            } else {
                throw new SQLException("Activité non trouvée.");
            }

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // Méthode pour récupérer les noms des activités
    public List<String> getNomsActivites() {
        List<String> noms = new ArrayList<>();
        String query = "SELECT nom FROM activite ORDER BY nom";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                noms.add(rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return noms;
    }

    public Connection getConnection() {
        return connection;
    }
}