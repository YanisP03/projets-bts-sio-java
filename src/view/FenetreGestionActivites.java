package view;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import dao.ActiviteDAO;
import model.Activite;

public class FenetreGestionActivites extends JFrame {
    private JTable tableau;
    private DefaultTableModel modele;
    private ActiviteDAO activiteDAO;

    public FenetreGestionActivites(Connection connexion) throws SQLException {
        // Initialisation de l'ActiviteDAO avec la connexion à la base de données
        activiteDAO = new ActiviteDAO(connexion);

        setLayout(new BorderLayout());
        setTitle("Gestion des Activités");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // --- Création du tableau
        modele = new DefaultTableModel(new Object[]{"ID", "Nom", "Description", "Âge Min", "Âge Max"}, 0);
        tableau = new JTable(modele);
        JScrollPane scrollPane = new JScrollPane(tableau);
        add(scrollPane, BorderLayout.CENTER);

        // --- Boutons
        JPanel panelBoutons = new JPanel();

        JButton btnAjouter = new JButton("Ajouter");
        JButton btnModifier = new JButton("Modifier");
        JButton btnSupprimer = new JButton("Supprimer");

        panelBoutons.add(btnAjouter);
        panelBoutons.add(btnModifier);
        panelBoutons.add(btnSupprimer);

        add(panelBoutons, BorderLayout.SOUTH);

        // --- Actions des boutons

        // Bouton Ajouter
        btnAjouter.addActionListener(e -> {
            String nom = JOptionPane.showInputDialog(this, "Nom :");
            String description = JOptionPane.showInputDialog(this, "Description :");
            String ageMinStr = JOptionPane.showInputDialog(this, "Âge minimum :");
            String ageMaxStr = JOptionPane.showInputDialog(this, "Âge maximum :");

            try {
                int ageMin = Integer.parseInt(ageMinStr);
                int ageMax = Integer.parseInt(ageMaxStr);
                activiteDAO.ajouterActivite(new Activite(0, nom, description, ageMin, ageMax));
                rafraichirAffichage();
            } catch (NumberFormatException | SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            }
        });

        // Bouton Modifier
        btnModifier.addActionListener(e -> {
            int ligneSelectionnee = tableau.getSelectedRow();
            if (ligneSelectionnee != -1) {
                int id = (int) modele.getValueAt(ligneSelectionnee, 0);
                String nom = (String) modele.getValueAt(ligneSelectionnee, 1);
                String description = (String) modele.getValueAt(ligneSelectionnee, 2);
                int ageMin = (int) modele.getValueAt(ligneSelectionnee, 3);
                int ageMax = (int) modele.getValueAt(ligneSelectionnee, 4);

                String nouveauNom = JOptionPane.showInputDialog(this, "Nouveau nom :", nom);
                String nouvelleDesc = JOptionPane.showInputDialog(this, "Nouvelle description :", description);
                String nouvelAgeMinStr = JOptionPane.showInputDialog(this, "Âge minimum :", ageMin);
                String nouvelAgeMaxStr = JOptionPane.showInputDialog(this, "Âge maximum :", ageMax);

                try {
                    int nouvelAgeMin = Integer.parseInt(nouvelAgeMinStr);
                    int nouvelAgeMax = Integer.parseInt(nouvelAgeMaxStr);

                    activiteDAO.modifierActivite(new Activite(id, nouveauNom, nouvelleDesc, nouvelAgeMin, nouvelAgeMax));
                    rafraichirAffichage();
                } catch (NumberFormatException | SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez une ligne à modifier.");
            }
        });

        // Bouton Supprimer
        btnSupprimer.addActionListener(e -> {
            int ligneSelectionnee = tableau.getSelectedRow();
            if (ligneSelectionnee != -1) {
                int id = (int) modele.getValueAt(ligneSelectionnee, 0);
                try {
                	activiteDAO.supprimerActiviteComplet(id);
                    rafraichirAffichage();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez une ligne à supprimer.");
            }
        });

        rafraichirAffichage();
        setVisible(true);
    }

	/**
	 * Méthode pour rafraîchir l'affichage du tableau avec les activités de la base
	 * de données.
	 */
    private void rafraichirAffichage() {
        try {
            modele.setRowCount(0);
            List<Activite> activites = activiteDAO.listerActivites();
            for (Activite act : activites) {
                modele.addRow(new Object[]{
                        act.getId(), act.getNom(), act.getDescription(), act.getAgeMin(), act.getAgeMax()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement : " + e.getMessage());
        }
    }
}