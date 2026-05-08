package view;

import javax.swing.*;
// Imports des DAO et du modèle
import model.Utilisateur;
import dao.UtilisateurDAO;
import dao.ActiviteDAO;
import dao.NotificationDAO;
import dao.CalendrierDAO;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

// Classe représentant la fenêtre principale pour l'administrateur
public class FenetreAdmin extends JFrame {
    // Accès aux différentes couches DAO
    private UtilisateurDAO utilisateurDAO;
    private ActiviteDAO activiteDAO;
    private NotificationDAO notificationDAO;
    private CalendrierDAO calendrierDAO;

    // Panel destiné à afficher dynamiquement du contenu en bas de la fenêtre
    private JPanel panelContenu;

    // Constructeur principal
    public FenetreAdmin(Connection connexion) {
        super("Interface Administrateur"); // Titre de la fenêtre

        // Initialisation des DAO avec la connexion SQL
        this.utilisateurDAO = new UtilisateurDAO(connexion);
        this.activiteDAO = new ActiviteDAO(connexion);
        this.notificationDAO = new NotificationDAO(connexion);
        this.calendrierDAO = new CalendrierDAO(connexion);

        // Paramètres de la fenêtre
        setSize(700, 600);
        setLocationRelativeTo(null); // Centrer la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Titre principal
        JLabel titre = new JLabel("Panel d'administration", JLabel.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 20));
        add(titre, BorderLayout.NORTH);

        // Création des boutons dans un layout en grille
        JPanel panelCentre = new JPanel(new GridLayout(7, 1, 10, 10));
        JButton boutonVoirUtilisateurs = new JButton("Voir tous les utilisateurs");
        JButton boutonSupprimerUtilisateur = new JButton("Supprimer un utilisateur par email");
        JButton boutonVoirNotifications = new JButton("Voir les notifications");
        JButton boutonVoirCalendrier = new JButton("Voir calendrier des activités");
        JButton boutonAjouterUtilisateur = new JButton("Ajouter un utilisateur");
        JButton boutonQuitter = new JButton("Quitter");

        // Ajout des boutons
        panelCentre.add(boutonVoirUtilisateurs);
        panelCentre.add(boutonSupprimerUtilisateur);
        panelCentre.add(boutonVoirNotifications);
        panelCentre.add(boutonVoirCalendrier);
        panelCentre.add(boutonAjouterUtilisateur);
        panelCentre.add(boutonQuitter);
        add(panelCentre, BorderLayout.CENTER);

        // Zone de contenu en bas de l'écran
        panelContenu = new JPanel(new BorderLayout());
        add(panelContenu, BorderLayout.SOUTH);

        // 🔹 Afficher tous les utilisateurs
        boutonVoirUtilisateurs.addActionListener(e -> {
            List<Utilisateur> utilisateurs = utilisateurDAO.recupererTousLesUtilisateurs();
            if (utilisateurs.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucun utilisateur trouvé.");
                return;
            }

            StringBuilder sb = new StringBuilder("Liste des utilisateurs :\n\n");
            for (Utilisateur u : utilisateurs) {
                sb.append("ID: ").append(u.getId()).append("\n");
                sb.append("Nom: ").append(u.getNom()).append(" ").append(u.getPrenom()).append("\n");
                sb.append("Email: ").append(u.getEmail()).append("\n");
                sb.append("Rôle: ").append(u.getRole()).append("\n");
                sb.append("Age: ").append(u.getAge()).append("\n");
                sb.append("--------------------------------------------------\n");
            }

            afficherContenu(sb.toString(), "Utilisateurs enregistrés");
        });

        // 🔹 Supprimer un utilisateur via son email
        boutonSupprimerUtilisateur.addActionListener(e -> {
            String email = JOptionPane.showInputDialog(this, "Email de l'utilisateur à supprimer :");
            if (email != null && !email.isEmpty()) {
                boolean success = utilisateurDAO.supprimerUtilisateurParEmail(email);
                JOptionPane.showMessageDialog(this, success ? "Utilisateur supprimé." : "Échec de la suppression.");
            }
        });

        // 🔹 Afficher les notifications pour un utilisateur
        boutonVoirNotifications.addActionListener(e -> {
            String email = JOptionPane.showInputDialog(this, "Email de l'utilisateur pour les notifications :");
            if (email != null && !email.isEmpty()) {
                int utilisateurId = utilisateurDAO.getIdParEmail(email);
                if (utilisateurId != -1) {
                    try {
                        List<String> notifications = notificationDAO.getNotificationsProchainesActivites(utilisateurId);
                        if (notifications.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "Aucune activité à venir.");
                        } else {
                            StringBuilder sb = new StringBuilder("Notifications :\n\n");
                            notifications.forEach(notif -> sb.append(notif).append("\n"));
                            afficherContenu(sb.toString(), "Notifications");
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des notifications.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Utilisateur non trouvé.");
                }
            }
        });

        // 🔹 Afficher le calendrier des activités
        boutonVoirCalendrier.addActionListener(e -> {
            try {
                String calendrier = calendrierDAO.afficherCalendrier();
                if (calendrier.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Aucune activité programmée.");
                } else {
                    afficherContenu(calendrier, "Calendrier des Activités");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la récupération du calendrier.");
            }
        });

        // 🔹 Formulaire d'ajout d'utilisateur
        boutonAjouterUtilisateur.addActionListener(e -> {
            panelContenu.removeAll(); // Nettoyage du contenu précédent

            JPanel panelFormulaire = new JPanel(new GridLayout(7, 2, 10, 5));
            panelFormulaire.setBorder(BorderFactory.createTitledBorder("Ajouter un utilisateur"));

            // Champs de formulaire
            JTextField champNom = new JTextField();
            JTextField champPrenom = new JTextField();
            JTextField champEmail = new JTextField();
            JPasswordField champMotDePasse = new JPasswordField();

            // Liste déroulante pour le rôle
            String[] roles = {"utilisateur", "admin"};
            JComboBox<String> comboRole = new JComboBox<>(roles);

            // Spinner pour l’âge (min 10, max 120)
            SpinnerNumberModel ageModel = new SpinnerNumberModel(18, 10, 120, 1);
            JSpinner spinnerAge = new JSpinner(ageModel);

            // Ajout des champs au formulaire
            panelFormulaire.add(new JLabel("Nom :"));
            panelFormulaire.add(champNom);
            panelFormulaire.add(new JLabel("Prénom :"));
            panelFormulaire.add(champPrenom);
            panelFormulaire.add(new JLabel("Email :"));
            panelFormulaire.add(champEmail);
            panelFormulaire.add(new JLabel("Mot de passe :"));
            panelFormulaire.add(champMotDePasse);
            panelFormulaire.add(new JLabel("Rôle :"));
            panelFormulaire.add(comboRole);
            panelFormulaire.add(new JLabel("Âge :"));
            panelFormulaire.add(spinnerAge);

            JButton boutonEnregistrer = new JButton("Enregistrer");
            panelFormulaire.add(new JLabel()); // cellule vide
            panelFormulaire.add(boutonEnregistrer);

            // Affichage dans le panel principal
            panelContenu.add(panelFormulaire, BorderLayout.CENTER);
            panelContenu.revalidate();
            panelContenu.repaint();

            // 🔸 Action pour enregistrer un nouvel utilisateur
            boutonEnregistrer.addActionListener(ev -> {
                try {
                    String nom = champNom.getText().trim();
                    String prenom = champPrenom.getText().trim();
                    String email = champEmail.getText().trim();
                    String motDePasse = new String(champMotDePasse.getPassword()).trim();
                    int age = (int) spinnerAge.getValue();
                    String role = (String) comboRole.getSelectedItem();

                    // Vérification des champs
                    if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || motDePasse.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis.");
                        return;
                    }

                    if (utilisateurDAO.utilisateurExiste(email)) {
                        JOptionPane.showMessageDialog(this, "Cet utilisateur existe déjà.");
                        return;
                    }

                    if (age < 18) {
                        JOptionPane.showMessageDialog(this, "L'utilisateur doit être majeur.");
                        return;
                    }

                    // Vérification de la robustesse du mot de passe
                    if (!motDePasse.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{12,}$")) {
                        JOptionPane.showMessageDialog(this, "Mot de passe trop faible (min 12 caractères, 1 maj, 1 min, 1 chiffre, 1 symbole).");
                        return;
                    }

                    // Création du compte utilisateur
                    boolean success = utilisateurDAO.inscrireUtilisateurComplet(
                        email, motDePasse, nom, prenom, age,
                        false, // handicap = false
                        "Non spécifié", // sexe
                        "" // téléphone vide
                    );

                    JOptionPane.showMessageDialog(this, success ? "Utilisateur ajouté avec succès." : "Échec de l'ajout.");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de l'utilisateur.");
                }
            });
        });

        // 🔹 Bouton quitter : ferme la fenêtre et ouvre l'écran de connexion
        boutonQuitter.addActionListener(e -> {
            dispose(); // Ferme la fenêtre actuelle
            new FenetreConnexion(utilisateurDAO.getConnexion()); // Retour à la connexion
        });

        setVisible(true); // Affiche la fenêtre
    }

    /**
     * Méthode pour afficher dynamiquement un texte formaté dans le bas de la fenêtre
     */
    private void afficherContenu(String contenu, String titre) {
        panelContenu.removeAll(); // Vide l'ancien contenu

        JPanel panelTexte = new JPanel(new BorderLayout());
        panelTexte.setBorder(BorderFactory.createTitledBorder(titre));

        JTextArea textArea = new JTextArea(contenu);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(650, 250));

        panelTexte.add(scrollPane, BorderLayout.CENTER);
        panelContenu.add(panelTexte, BorderLayout.CENTER);
        panelContenu.revalidate();
        panelContenu.repaint();
    }
}
