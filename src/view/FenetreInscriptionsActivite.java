package view;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

/**
 * Fenêtre permettant à un responsable de visualiser les inscriptions
 * aux différentes activités (par utilisateur ou par enfant) et de modifier leur statut.
 */
public class FenetreInscriptionsActivite extends JFrame {
    private Connection connexion;
    private JTable tableInscriptions;
    private DefaultTableModel tableModel;
    private JComboBox<String> menuStatut;
    private JButton boutonModifierStatut;

    public FenetreInscriptionsActivite(Connection connexion) {
        super("Liste des Inscriptions");
        this.connexion = connexion;

        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Titre
        JLabel titre = new JLabel("Inscriptions aux Activités", JLabel.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 20));
        add(titre, BorderLayout.NORTH);

        // Table des inscriptions
        tableModel = new DefaultTableModel(new String[]{
                "ID", "Activité", "Début", "Lieu", "Nom du participant", "Type", "Statut"
        }, 0);
        tableInscriptions = new JTable(tableModel);
        tableInscriptions.removeColumn(tableInscriptions.getColumnModel().getColumn(0)); // cacher l'ID
        JScrollPane scrollPane = new JScrollPane(tableInscriptions);
        add(scrollPane, BorderLayout.CENTER);

        // Panel en bas avec le menu déroulant et le bouton
        JPanel panelBas = new JPanel(new FlowLayout());

        menuStatut = new JComboBox<>(new String[]{"À valider", "Validé", "Refusé"});
        boutonModifierStatut = new JButton("Modifier le statut");

        panelBas.add(new JLabel("Modifier le statut de l'inscription sélectionnée :"));
        panelBas.add(menuStatut);
        panelBas.add(boutonModifierStatut);

        add(panelBas, BorderLayout.SOUTH);

        // Action du bouton
        boutonModifierStatut.addActionListener(this::modifierStatutInscription);

        // Chargement des données
        chargerInscriptions();

        setVisible(true);
    }

    /**
     * Récupère les inscriptions depuis la base et les affiche dans la table.
     */
    private void chargerInscriptions() {
        try {
            String sql = """
                SELECT ia.inscription_id AS inscription_id,
                       a.nom AS activite,
                       c.debut,
                       c.lieu,
                       u.nom AS nom_utilisateur,
                       u.prenom AS prenom_utilisateur,
                       e.nom AS nom_enfant,
                       e.prenom AS prenom_enfant,
                       ia.statut
                FROM inscription_activite ia
                JOIN calendrier c ON ia.calendrier_id = c.calendrier_id
                JOIN activite a ON c.activite_id = a.activite_id
                LEFT JOIN utilisateur u ON ia.utilisateur_id = u.utilisateur_id
                LEFT JOIN enfant e ON ia.enfant_id = e.enfant_id
                ORDER BY c.debut ASC
            """;

            PreparedStatement stmt = connexion.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idInscription = rs.getInt("inscription_id");
                String activite = rs.getString("activite");
                Timestamp debut = rs.getTimestamp("debut");
                String lieu = rs.getString("lieu");
                String statut = rs.getString("statut");

                String nomParticipant;
                String type;

                // Determine if the participant is an adult or a child
                if (rs.getString("nom_enfant") != null) {
                    nomParticipant = rs.getString("prenom_enfant") + " " + rs.getString("nom_enfant");
                    type = "Enfant";
                } else {
                    nomParticipant = rs.getString("prenom_utilisateur") + " " + rs.getString("nom_utilisateur");
                    type = "Adulte";
                }

                // Add the data to the table model
                tableModel.addRow(new Object[]{
                        idInscription,
                        activite,
                        debut.toLocalDateTime().toString().replace("T", " "),
                        lieu,
                        nomParticipant,
                        type,
                        statut
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des inscriptions", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Met à jour le statut de l'inscription sélectionnée dans la base.
     */
    private void modifierStatutInscription(ActionEvent e) {
        int selectedRow = tableInscriptions.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une inscription.", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // L'ID est dans le modèle, même si la colonne est masquée
        int idInscription = (int) tableModel.getValueAt(selectedRow, 0);
        String nouveauStatut = (String) menuStatut.getSelectedItem();

        try {
            String sql = "UPDATE inscription_activite SET statut = ? WHERE inscription_id = ?";
            PreparedStatement stmt = connexion.prepareStatement(sql);
            stmt.setString(1, nouveauStatut);
            stmt.setInt(2, idInscription);
            stmt.executeUpdate();

            // Mettre à jour l'affichage
            tableModel.setValueAt(nouveauStatut, selectedRow, 6);
            JOptionPane.showMessageDialog(this, "Statut mis à jour avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du statut.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
