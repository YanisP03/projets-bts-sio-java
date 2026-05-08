package view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;

import model.Utilisateur;
import dao.UtilisateurDAO;

public class FenetreInscription extends JFrame {

    // Champs du formulaire
    private JTextField champEmail, champNom, champPrenom, champTelephone;
    private JPasswordField champMotDePasse, champConfirmationMotDePasse;
    private JCheckBox checkHandicap, checkAfficherMDP, checkAfficherConfirmation;
    private JRadioButton radioHomme, radioFemme, radioAutre;
    private JLabel labelStatut;
    private UtilisateurDAO utilisateurDAO;
    private JSpinner spinnerAge;

    public FenetreInscription(UtilisateurDAO utilisateurDAO) {
        super("Inscription");
        this.utilisateurDAO = utilisateurDAO;

        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Initialisation des champs
        champEmail = new JTextField(20);
        champNom = new JTextField(20);
        champPrenom = new JTextField(20);
        champTelephone = new JTextField(15);
        champMotDePasse = new JPasswordField(20);
        champConfirmationMotDePasse = new JPasswordField(20);
        checkAfficherMDP = new JCheckBox("Afficher le mot de passe");
        checkAfficherConfirmation = new JCheckBox("Afficher la confirmation");
        checkHandicap = new JCheckBox("Handicap");

        // Spinners numériques
        spinnerAge = new JSpinner(new SpinnerNumberModel(18, 0, 120, 1));

        // Boutons radio pour le sexe
        radioHomme = new JRadioButton("Homme");
        radioFemme = new JRadioButton("Femme");
        radioAutre = new JRadioButton("Autre");
        ButtonGroup groupeSexe = new ButtonGroup();
        groupeSexe.add(radioHomme);
        groupeSexe.add(radioFemme);
        groupeSexe.add(radioAutre);

        labelStatut = new JLabel(" ");

        JButton boutonInscription = new JButton("Valider l'inscription");
        JButton boutonRetour = new JButton("Retour");

        int y = 0;
        ajouterLabelEtChamp("Email :", champEmail, gbc, y++);
        ajouterLabelEtChamp("Nom :", champNom, gbc, y++);
        ajouterLabelEtChamp("Prénom :", champPrenom, gbc, y++);
        ajouterLabelEtChamp("Âge :", spinnerAge, gbc, y++);
        ajouterLabelEtChamp("Téléphone :", champTelephone, gbc, y++);

        // Sexe
        gbc.gridx = 0; gbc.gridy = y; gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Sexe :"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JPanel panelSexe = new JPanel();
        panelSexe.add(radioHomme);
        panelSexe.add(radioFemme);
        panelSexe.add(radioAutre);
        add(panelSexe, gbc);
        y++;

        // Checkbox handicap
        gbc.gridx = 1; gbc.gridy = y++;
        add(checkHandicap, gbc);

        // Mot de passe
        ajouterLabelEtChamp("Mot de passe :", champMotDePasse, gbc, y++);
        gbc.gridx = 1; gbc.gridy = y++;
        add(checkAfficherMDP, gbc);

        // Confirmation mot de passe
        ajouterLabelEtChamp("Confirmer le mot de passe :", champConfirmationMotDePasse, gbc, y++);
        gbc.gridx = 1; gbc.gridy = y++;
        add(checkAfficherConfirmation, gbc);

        // Label d'état
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2;
        add(labelStatut, gbc);

        // Boutons
        gbc.gridy = y++;
        add(boutonInscription, gbc);
        gbc.gridy = y++;
        add(boutonRetour, gbc);

        // Listeners
        checkAfficherMDP.addActionListener(e -> {
            champMotDePasse.setEchoChar(checkAfficherMDP.isSelected() ? (char) 0 : '•');
        });

        checkAfficherConfirmation.addActionListener(e -> {
            champConfirmationMotDePasse.setEchoChar(checkAfficherConfirmation.isSelected() ? (char) 0 : '•');
        });

        boutonRetour.addActionListener(e -> {
            dispose();
            new FenetreConnexion(utilisateurDAO.getConnexion());
        });

        boutonInscription.addActionListener(e -> inscription());

        setVisible(true);
    }

    // Méthode utilitaire
    private void ajouterLabelEtChamp(String label, JComponent champ, GridBagConstraints gbc, int y) {
        gbc.gridx = 0; gbc.gridy = y; gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        add(champ, gbc);
    }

    private void inscription() {
        try {
            String email = champEmail.getText().trim();
            String nom = champNom.getText().trim();
            String prenom = champPrenom.getText().trim();
            int age = (int) spinnerAge.getValue();
            String telephone = champTelephone.getText().trim();
            boolean handicap = checkHandicap.isSelected();
            String motDePasse = new String(champMotDePasse.getPassword());
            String confirmation = new String(champConfirmationMotDePasse.getPassword());
            String sexe = radioHomme.isSelected() ? "Homme" : radioFemme.isSelected() ? "Femme" : "Autre";

            if (email.isEmpty() || nom.isEmpty() || prenom.isEmpty() || motDePasse.isEmpty() || confirmation.isEmpty()) {
                labelStatut.setText("Tous les champs obligatoires doivent être remplis.");
                return;
            }
            
            if (age < 18) {
                labelStatut.setText("Vous devez être majeur pour créer un compte.");
                return;
            }

            if (!motDePasse.equals(confirmation)) {
                labelStatut.setText("Les mots de passe ne correspondent pas.");
                return;
            }

            if (!motDePasse.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{12,}$")) {
                labelStatut.setText("Mot de passe trop faible (12 caractères, majuscule, chiffre, etc.).");
                return;
            }

            if (!telephone.matches("\\d+")) {
                labelStatut.setText("Le numéro de téléphone doit contenir uniquement des chiffres.");
                return;
            }

            if (utilisateurDAO.utilisateurExiste(email)) {
                labelStatut.setText("Un compte avec cet email existe déjà.");
                return;
            }

            boolean success = utilisateurDAO.inscrireUtilisateurComplet(
                email, motDePasse, nom, prenom, age, handicap, sexe, telephone
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Inscription réussie ! Vous pouvez maintenant vous connecter.",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new FenetreConnexion(utilisateurDAO.getConnexion());
            } else {
                labelStatut.setText("Échec de l'inscription.");
            }

        } catch (Exception e) {
            labelStatut.setText("Erreur dans le formulaire.");
        }
    }
}