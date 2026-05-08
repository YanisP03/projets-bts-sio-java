package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;

import dao.UtilisateurDAO;
import model.Utilisateur;

/**
 * Fenêtre d'interface graphique pour la connexion d'un utilisateur.
 * Permet à un utilisateur de saisir ses identifiants pour accéder à l'application.
 * Redirige vers l'interface appropriée selon le rôle de l'utilisateur (admin, responsable, utilisateur).
 */
public class FenetreConnexion extends JFrame {
    // Champs pour la saisie de l'email et du mot de passe
    private JTextField champEmail;
    private JPasswordField champMotDePasse;
    private JLabel labelStatut; // Label pour afficher les messages d'erreur ou de statut
    private UtilisateurDAO utilisateurDAO; // Accès aux méthodes de la base pour les utilisateurs

    /**
     * Constructeur de la fenêtre de connexion.
     * @param connexion Connexion à la base de données, transmise au DAO.
     */
    public FenetreConnexion(Connection connexion) {
        super("Connexion");
        this.utilisateurDAO = new UtilisateurDAO(connexion);

        // Configuration de base de la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Centrer la fenêtre à l'écran
        setLayout(new GridBagLayout()); // Utilisation d'un layout flexible

        // Initialisation des composants
        champEmail = new JTextField(20);
        champMotDePasse = new JPasswordField(20);
        JCheckBox afficherMotDePasse = new JCheckBox("Afficher le mot de passe");
        labelStatut = new JLabel(" ");

        JButton boutonConnexion = new JButton("Se connecter");
        JButton boutonVersInscription = new JButton("Créer un compte");

        // Configuration du layout avec GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espacement entre les composants

        // Ligne 0 : Email
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Email :"), gbc);

        gbc.gridx = 1;
        add(champEmail, gbc);

        // Ligne 1 : Mot de passe
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Mot de passe :"), gbc);

        gbc.gridx = 1;
        add(champMotDePasse, gbc);

        // Ligne 2 : Case à cocher pour afficher le mot de passe
        gbc.gridx = 1; gbc.gridy = 2;
        add(afficherMotDePasse, gbc);

        // Ligne 3 : Message de statut
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(labelStatut, gbc);

        // Ligne 4 : Boutons
        gbc.gridy = 4; gbc.gridwidth = 1;
        add(boutonConnexion, gbc);

        gbc.gridx = 1;
        add(boutonVersInscription, gbc);

        // Gestion de l'affichage/masquage du mot de passe
        afficherMotDePasse.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                champMotDePasse.setEchoChar((char) 0); // Afficher le texte
            } else {
                champMotDePasse.setEchoChar('•'); // Masquer avec des points
            }
        });

        // Action lors du clic sur "Se connecter"
        boutonConnexion.addActionListener(e -> {
            String email = champEmail.getText().trim();
            String mdp = new String(champMotDePasse.getPassword());

            // Vérification que les champs ne sont pas vides
            if (email.isEmpty() || mdp.isEmpty()) {
                labelStatut.setText("Veuillez remplir tous les champs.");
                return;
            }

            // Vérification des identifiants via le DAO
            if (utilisateurDAO.verifierIdentifiants(email, mdp)) {
                labelStatut.setText("Connexion réussie !");
                dispose(); // Ferme la fenêtre actuelle

                // Récupération des infos utilisateur
                String role = utilisateurDAO.getRoleParEmail(email);
                int utilisateurId = utilisateurDAO.getIdParEmail(email);
                Utilisateur utilisateur = utilisateurDAO.recupererUtilisateurParId(utilisateurId);

                // Redirection en fonction du rôle
                switch (role.toLowerCase()) {
                    case "admin":
                        new FenetreAdmin(utilisateurDAO.getConnexion());
                        break;
                    case "responsable":
                        new FenetreResponsable(utilisateurDAO.getConnexion(), utilisateur);
                        break;
                    case "utilisateur":
                        new FenetrePrincipale(utilisateurDAO.getConnexion(), utilisateur);
                        break;
                    default:
                        labelStatut.setText("Rôle non reconnu.");
                        break;
                }
            } else {
                labelStatut.setText("Email ou mot de passe incorrect.");
            }
        });

        // Action lors du clic sur "Créer un compte"
        boutonVersInscription.addActionListener(e -> {
            dispose(); // Ferme la fenêtre actuelle
            new FenetreInscription(utilisateurDAO); // Ouvre la fenêtre d'inscription
        });

        setVisible(true); // Rend la fenêtre visible
    }
}
