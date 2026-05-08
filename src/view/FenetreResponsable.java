package view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;

import model.Utilisateur;

/**
 * Fenêtre principale dédiée au rôle "Responsable".
 * Permet d'accéder à toutes les interfaces de gestion.
 */
public class FenetreResponsable extends JFrame {
    private Connection connexion;
    private Utilisateur utilisateur; // Add a field for the Utilisateur object

    // Existing constructor
    public FenetreResponsable(Connection connexion) {
        this(connexion, null); // Call the new constructor with a null Utilisateur
    }

    // New constructor
    public FenetreResponsable(Connection connexion, Utilisateur utilisateur) {
        super("Espace Responsable");
        this.connexion = connexion;
        this.utilisateur = utilisateur;

        // Configuration générale de la fenêtre
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Titre de la fenêtre
        JLabel titre = new JLabel("Tableau de bord du Responsable", JLabel.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 22));
        add(titre, BorderLayout.NORTH);

        // Panneau central avec les boutons d'action
        JPanel panelBoutons = new JPanel(new GridLayout(6, 1, 10, 10));
        panelBoutons.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        // Création des boutons
        JButton boutonGestionActivites = new JButton("📋 Gérer les Activités");
        JButton boutonGestionCalendrier = new JButton("📅 Gérer les Créneaux / Calendrier");
        JButton boutonTickets = new JButton("🛠️ Voir les Tickets / Incidents");
        JButton boutonVoirInscriptions = new JButton("👨‍👩‍👧‍👦 Voir les Inscriptions");
        JButton boutonNotifications = new JButton("🔔 Notifications de tous les utilisateurs");
        JButton boutonDeconnexion = new JButton("🚪 Se déconnecter");

        // Ajout des boutons au panneau
        panelBoutons.add(boutonGestionActivites);
        panelBoutons.add(boutonGestionCalendrier);
        panelBoutons.add(boutonTickets);
        panelBoutons.add(boutonVoirInscriptions);
        panelBoutons.add(boutonNotifications);
        panelBoutons.add(boutonDeconnexion);
        

        add(panelBoutons, BorderLayout.CENTER);

        // Actions des boutons
        boutonGestionActivites.addActionListener((ActionEvent e) -> {
            try {
                new FenetreGestionActivites(connexion); // Passer la connexion
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ouverture de la gestion des activités : " + ex.getMessage());
            }
        });
        // Fenêtre pour gérer le calendrier	
        boutonGestionCalendrier.addActionListener((ActionEvent e) -> {
            try {
                new FenetreCalendrier(connexion); // Passer la connexion
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ouverture du calendrier : " + ex.getMessage());
            }
        });
        // Fenêtre pour gérer les tickets/incidents
        boutonTickets.addActionListener(e -> {
            JFrame fenetre = new JFrame("Tickets d'incidents");
            fenetre.setContentPane(new FenetreTicketsIncidents(connexion)); // ← ta classe
            fenetre.setSize(500, 400);
            fenetre.setLocationRelativeTo(null);
            fenetre.setVisible(true);
        });

        // Fenêtre pour voir les inscriptions aux activités
        boutonVoirInscriptions.addActionListener((ActionEvent e) -> {
            try {
                new FenetreInscriptionsActivite(connexion); // Passer la connexion
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ouverture des inscriptions : " + ex.getMessage());
            }
        });
        // Fenêtre pour gérer les notifications
        boutonNotifications.addActionListener((ActionEvent e) -> {
            new FenetreNotificationsGlobales(connexion);
        });
        // Action de déconnexion
        boutonDeconnexion.addActionListener((ActionEvent e) -> {
            dispose();
            try {
                new FenetreConnexion(connexion); // Passer la connexion pour réinitialiser la connexion
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la déconnexion : " + ex.getMessage());
            }
        });

        // Rendre la fenêtre visible
        setVisible(true);
    }
}