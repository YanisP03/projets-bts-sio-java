package view;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Panneau pour afficher les tickets d'incident soumis par les utilisateurs.
 */
public class FenetreTicketsIncidents extends JPanel {
    // Zone de texte pour afficher les tickets
    private JTextArea zoneTickets;

    public FenetreTicketsIncidents(Connection connexion) {
        // Définir le layout du panneau en BorderLayout
        setLayout(new BorderLayout());

        // Création de la zone de texte pour afficher les tickets
        zoneTickets = new JTextArea();
        zoneTickets.setEditable(false); // La zone n'est pas modifiable par l'utilisateur

        // Chargement des tickets depuis la base de données
        loadTicketsFromDatabase(connexion);

        // Ajout de la zone de texte dans un JScrollPane pour permettre le défilement
        add(new JScrollPane(zoneTickets), BorderLayout.CENTER);
    }

    // Méthode privée pour charger les tickets depuis la base de données
    private void loadTicketsFromDatabase(Connection connexion) {
        StringBuilder ticketsText = new StringBuilder(); // Pour stocker les tickets sous forme de texte

        // Requête SQL pour récupérer les tickets et les informations des utilisateurs associés
        String query = """
        	    SELECT t.id, t.description, u.nom, u.prenom
        	    FROM tickets t
        	    JOIN utilisateur u ON t.utilisateur_id = u.utilisateur_id
        	    ORDER BY t.id DESC
        	""";

        try (PreparedStatement stmt = connexion.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

        	// Parcours des résultats de la requête
        	while (rs.next()) {
        	    int id = rs.getInt("id"); // ID du ticket
        	    String description = rs.getString("description"); // Description de l'incident
        	    String nom = rs.getString("nom"); // Nom de l'utilisateur
        	    String prenom = rs.getString("prenom"); // Prénom de l'utilisateur

        	    // Formatage du ticket et ajout au StringBuilder
        	    ticketsText.append("▶ Ticket #").append(id)
        	               .append(" | ").append(prenom).append(" ").append(nom)
        	               .append(" : ").append(description).append("\n");
        	}

			// Si aucun ticket n'est trouvé, afficher un message approprié
			if (ticketsText.length() == 0) {
				ticketsText.append("Aucun ticket trouvé.");
            }

        } catch (SQLException e) {
            // En cas d'erreur SQL, afficher un message d'erreur
            ticketsText.append("Erreur lors du chargement des tickets : ").append(e.getMessage());
        }

        // Affichage du contenu dans la zone de texte
        zoneTickets.setText(ticketsText.toString());
    }
}
