package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;

import model.Utilisateur;
import model.Activite;
import model.Calendrier;
import dao.NotificationDAO;
import dao.ActiviteDAO;
import dao.CalendrierDAO;

public class FenetrePrincipale extends JFrame {
    // Composant pour afficher du texte (résultats, messages)
    private JTextArea outputArea;

    // DAO pour accéder aux données
    private ActiviteDAO activiteDAO;
    private CalendrierDAO calendrierDAO;
    private NotificationDAO notificationDAO;

    // Utilisateur connecté
    private Utilisateur utilisateur;

    // Bouton pour signaler un incident
    private JButton boutonCreerIncident;

    // Connexion à la base de données
    private Connection connection;

    // Constructeur de la fenêtre principale
    public FenetrePrincipale(Connection connection, Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        this.connection = connection;
        setTitle("Espace Participant - Activités");
        setSize(700, 500);
        setLocationRelativeTo(null); // Centre la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialisation des DAO
        this.activiteDAO = new ActiviteDAO(connection);
        this.calendrierDAO = new CalendrierDAO(connection);
        this.notificationDAO = new NotificationDAO(connection);

        // Création du panneau principal
        JPanel panel = new JPanel(new BorderLayout());

        // Zone de sortie pour afficher les messages et résultats
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Panneau contenant les boutons d'action
        JPanel boutonsPanel = new JPanel(new GridLayout(5, 2, 5, 5)); // Grille de 5 lignes, 2 colonnes

        // Création et ajout des boutons avec leurs listeners respectifs
        JButton boutonAfficherActivites = new JButton("Afficher activités (âge)");
        boutonAfficherActivites.addActionListener(this::afficherActivitesPourAge);

        JButton boutonCalendrier = new JButton("Voir calendrier");
        boutonCalendrier.addActionListener(this::afficherCalendrier);

        JButton boutonInscription = new JButton("S'inscrire à une activité");
        boutonInscription.addActionListener(this::inscrireActivite);

        JButton boutonInscriptionEnfant = new JButton("Inscrire un enfant à une activité");
        boutonInscriptionEnfant.addActionListener(this::inscrireEnfant);

        JButton boutonEvaluer = new JButton("Évaluer une activité");
        boutonEvaluer.addActionListener(this::evaluerActivite);

        JButton boutonNotifications = new JButton("Notifications");
        boutonNotifications.addActionListener(this::afficherNotifications);

        JButton boutonVoirEvaluations = new JButton("Voir toutes les évaluations");
        boutonVoirEvaluations.addActionListener(this::afficherToutesEvaluations);

        boutonCreerIncident = new JButton("Créer un incident");
        boutonCreerIncident.addActionListener(this::creerIncident);

        // Bouton pour se déconnecter
        JButton boutonDeconnexion = new JButton("Se déconnecter");
        boutonDeconnexion.addActionListener(this::deconnecter);

        // Ajout des boutons au panneau
        boutonsPanel.add(boutonAfficherActivites);
        boutonsPanel.add(boutonCalendrier);
        boutonsPanel.add(boutonInscription);
        boutonsPanel.add(boutonInscriptionEnfant);
        boutonsPanel.add(boutonEvaluer);
        boutonsPanel.add(boutonNotifications);
        boutonsPanel.add(boutonVoirEvaluations);
        boutonsPanel.add(boutonCreerIncident);
        boutonsPanel.add(boutonDeconnexion);

        panel.add(boutonsPanel, BorderLayout.NORTH);
        add(panel);
        setVisible(true);
    }

    // Action pour déconnecter l'utilisateur
    private void deconnecter(ActionEvent e) {
        int confirmation = JOptionPane.showConfirmDialog(
            this,
            "Voulez-vous vraiment vous déconnecter ?",
            "Confirmation de déconnexion",
            JOptionPane.YES_NO_OPTION
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            this.dispose(); // Ferme la fenêtre actuelle
            new FenetreConnexion(this.connection); // Réouvre la fenêtre de connexion
        }
    }

    // Permet de créer un ticket d'incident dans la base
    private void creerIncident(ActionEvent e) {
        String description = JOptionPane.showInputDialog(this, "Entrez la description de l'incident :");
        if (description == null || description.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La description ne peut pas être vide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String sql = "INSERT INTO tickets (utilisateur_id, description) VALUES (?, ?)";
            PreparedStatement stmt = calendrierDAO.getConnection().prepareStatement(sql);
            stmt.setInt(1, utilisateur.getId());
            stmt.setString(2, description);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Incident créé avec succès !");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la création de l'incident.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Affiche les activités disponibles pour un âge donné
    private void afficherActivitesPourAge(ActionEvent e) {
        try {
            String input = JOptionPane.showInputDialog(this, "Entrez l'âge :");
            int age = Integer.parseInt(input);
            List<Activite> activites = activiteDAO.getActivitesParTrancheAge(age);

            StringBuilder sb = new StringBuilder("Activités pour l'âge " + age + " :\n");
            for (Activite a : activites) {
                sb.append("- ").append(a.toString()).append("\n");
            }

            outputArea.setText(sb.toString());
        } catch (Exception ex) {
            outputArea.setText("Erreur : " + ex.getMessage());
        }
    }

    // Affiche le calendrier général et les activités personnelles
    private void afficherCalendrier(ActionEvent e) {
        try {
            String calendrierGeneral = calendrierDAO.afficherCalendrier();
            List<Activite> activitesInscrites = activiteDAO.getActivitesInscrites(utilisateur.getId());
            List<String> activitesEnfants = activiteDAO.getActivitesEnfantsAvecPrenoms(utilisateur.getId());

            StringBuilder sb = new StringBuilder("📅 Calendrier général des activités :\n\n")
                .append(calendrierGeneral)
                .append("\n");

            // Affichage des activités de l'utilisateur
            if (!activitesInscrites.isEmpty()) {
                sb.append("🧑 Vos activités inscrites :\n");
                for (Activite a : activitesInscrites) {
                    sb.append("- ").append(a.getNom()).append("\n");
                }
            } else {
                sb.append("🧑 Vous n'êtes inscrit à aucune activité.\n");
            }

            // Affichage des activités des enfants
            if (!activitesEnfants.isEmpty()) {
                sb.append("\n👧👦 Activités de vos enfants :\n");

                Map<String, List<String>> enfantsActivitesMap = new LinkedHashMap<>();
                for (String ligne : activitesEnfants) {
                    String[] parts = ligne.split(" est inscrit à : ");
                    if (parts.length == 2) {
                        String nomComplet = parts[0].trim();
                        String activite = parts[1].trim();
                        enfantsActivitesMap
                            .computeIfAbsent(nomComplet, k -> new ArrayList<>())
                            .add(activite);
                    }
                }

                for (Map.Entry<String, List<String>> entry : enfantsActivitesMap.entrySet()) {
                    sb.append("- ").append(entry.getKey()).append(" :\n");
                    for (String act : entry.getValue()) {
                        sb.append("    • ").append(act).append("\n");
                    }
                }
            }

            outputArea.setText(sb.toString());
        } catch (SQLException ex) {
            outputArea.setText("Erreur lors de l'affichage du calendrier : " + ex.getMessage());
        }
    }

    // Permet à l'utilisateur de s'inscrire à une activité
    private void inscrireActivite(ActionEvent e) {
        try {
            List<Activite> activites = activiteDAO.getToutesActivites();
            if (activites.isEmpty()) {
                outputArea.setText("Aucune activité trouvée dans la base de données.");
                return;
            }

            JComboBox<Activite> combo = new JComboBox<>(activites.toArray(new Activite[0]));
            int result = JOptionPane.showConfirmDialog(this, combo, "Sélectionner une activité", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                Activite selectedActivite = (Activite) combo.getSelectedItem();
                int activiteId = selectedActivite.getId();

                Calendrier calendrier = calendrierDAO.getCalendrierByActiviteId(activiteId);
                if (calendrier == null) {
                    outputArea.setText("Aucun calendrier trouvé pour cette activité.");
                    return;
                }

                // Vérifie si l'utilisateur est dans la bonne tranche d'âge
                if (utilisateur.getAge() < selectedActivite.getAgeMin() || utilisateur.getAge() > selectedActivite.getAgeMax()) {
                    outputArea.setText("Vous n'êtes pas dans la tranche d'âge pour cette activité.");
                    return;
                }

                boolean success = activiteDAO.inscrireParticipant(utilisateur.getId(), calendrier.getCalendrierId());
                outputArea.setText(success ? "Inscription réussie !" : "Échec de l'inscription.");
            }
        } catch (SQLException ex) {
            outputArea.setText("Erreur lors de l'inscription : " + ex.getMessage());
        }
    }

    // Permet d'inscrire un enfant à une activité (ou d'en créer un nouveau)
    private void inscrireEnfant(ActionEvent e) {
        try {
            List<String> enfants = new ArrayList<>();
            enfants.add("Ajouter un nouvel enfant");

            // Récupère les enfants existants
            String sql = "SELECT enfant_id, prenom, nom FROM enfant WHERE utilisateur_id = ?";
            PreparedStatement stmt = calendrierDAO.getConnection().prepareStatement(sql);
            stmt.setInt(1, utilisateur.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("enfant_id");
                String nomComplet = rs.getString("prenom") + " " + rs.getString("nom");
                enfants.add(id + " - " + nomComplet);
            }

            // Menu de sélection d'enfant
            JComboBox<String> menuEnfants = new JComboBox<>(enfants.toArray(new String[0]));
            int result = JOptionPane.showConfirmDialog(this, menuEnfants, "Sélectionnez un enfant", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String selection = (String) menuEnfants.getSelectedItem();

                int enfantId;
                if (selection.equals("Ajouter un nouvel enfant")) {
                    // Création d'un nouvel enfant
                    String prenom = JOptionPane.showInputDialog(this, "Entrez le prénom de l'enfant :");
                    String nom = JOptionPane.showInputDialog(this, "Entrez le nom de l'enfant :");
                    String ageStr = JOptionPane.showInputDialog(this, "Entrez l'âge de l'enfant :");
                    String sexe = JOptionPane.showInputDialog(this, "Entrez le sexe de l'enfant (H/F) :");

                    if (prenom == null || nom == null || ageStr == null || sexe == null) {
                        outputArea.setText("Inscription annulée.");
                        return;
                    }

                    int age = Integer.parseInt(ageStr);
                    String insertSql = "INSERT INTO enfant (utilisateur_id, prenom, nom, age, sexe) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement insertStmt = calendrierDAO.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                    insertStmt.setInt(1, utilisateur.getId());
                    insertStmt.setString(2, prenom);
                    insertStmt.setString(3, nom);
                    insertStmt.setInt(4, age);
                    insertStmt.setString(5, sexe);
                    insertStmt.executeUpdate();

                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        enfantId = generatedKeys.getInt(1);
                    } else {
                        outputArea.setText("Erreur lors de l'ajout de l'enfant.");
                        return;
                    }
                } else {
                    // Enfant existant sélectionné
                    enfantId = Integer.parseInt(selection.split(" - ")[0]);
                }

                // Récupération de l'âge de l'enfant
                String ageQuery = "SELECT age FROM enfant WHERE enfant_id = ?";
                PreparedStatement ageStmt = calendrierDAO.getConnection().prepareStatement(ageQuery);
                ageStmt.setInt(1, enfantId);
                ResultSet ageRs = ageStmt.executeQuery();
                if (!ageRs.next()) {
                    outputArea.setText("Erreur : âge de l'enfant introuvable.");
                    return;
                }
                int age = ageRs.getInt("age");

                // Récupère les activités adaptées à l'âge
                List<Activite> activitesPourAge = activiteDAO.getActivitesParTrancheAge(age);
                if (activitesPourAge.isEmpty()) {
                    outputArea.setText("Aucune activité disponible pour cet âge.");
                    return;
                }

                // Choix de l'activité
                JComboBox<Activite> combo = new JComboBox<>(activitesPourAge.toArray(new Activite[0]));
                int activityResult = JOptionPane.showConfirmDialog(this, combo, "Choisir une activité", JOptionPane.OK_CANCEL_OPTION);

                if (activityResult == JOptionPane.OK_OPTION) {
                    Activite activiteChoisie = (Activite) combo.getSelectedItem();
                    Calendrier calendrier = calendrierDAO.getCalendrierByActiviteId(activiteChoisie.getId());

                    if (calendrier != null) {
                        boolean success = activiteDAO.inscrireEnfant(enfantId, calendrier.getCalendrierId());
                        outputArea.setText(success ? "Enfant inscrit avec succès !" : "Échec de l'inscription.");
                    } else {
                        outputArea.setText("Aucun calendrier trouvé pour cette activité.");
                    }
                } else {
                    outputArea.setText("Aucune activité sélectionnée.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            outputArea.setText("Erreur lors de l'inscription de l'enfant : " + ex.getMessage());
        } catch (NumberFormatException ex) {
            outputArea.setText("Entrée invalide pour l'âge.");
        }
    }

    // Permet d'évaluer une activité à laquelle l'utilisateur est inscrit
    private void evaluerActivite(ActionEvent e) {
        try {
            List<Activite> activitesInscrites = activiteDAO.getActivitesInscrites(utilisateur.getId());
            if (activitesInscrites.isEmpty()) {
                outputArea.setText("Vous n'êtes inscrit à aucune activité.");
                return;
            }

            JComboBox<Activite> combo = new JComboBox<>(activitesInscrites.toArray(new Activite[0]));
            int choix = JOptionPane.showConfirmDialog(this, combo, "Choisir une activité à évaluer", JOptionPane.OK_CANCEL_OPTION);

            if (choix == JOptionPane.OK_OPTION) {
                Activite activite = (Activite) combo.getSelectedItem();
                int note = Integer.parseInt(JOptionPane.showInputDialog("Note sur 5 :"));
                if (note < 1 || note > 5) {
                    outputArea.setText("Note invalide. Elle doit être entre 1 et 5.");
                    return;
                }
                String commentaire = JOptionPane.showInputDialog("Commentaire :");

                boolean success = activiteDAO.evaluerActivite(utilisateur.getId(), activite.getId(), note, commentaire);
                outputArea.setText(success ? "Évaluation enregistrée !" : "Erreur d'enregistrement de l'évaluation.");
            }
        } catch (Exception ex) {
            outputArea.setText("Erreur : " + ex.getMessage());
        }
    }

    // Affiche les notifications à venir de l'utilisateur
    private void afficherNotifications(ActionEvent e) {
        try {
            List<String> notifs = notificationDAO.getNotificationsProchainesActivites(utilisateur.getId());
            StringBuilder sb = new StringBuilder("Notifications :\n");
            for (String notif : notifs) {
                sb.append("- ").append(notif).append("\n");
            }
            outputArea.setText(sb.toString());
        } catch (SQLException ex) {
            outputArea.setText("Erreur lors de l'affichage des notifications : " + ex.getMessage());
        }
    }

    // Affiche toutes les évaluations d'activités
    private void afficherToutesEvaluations(ActionEvent e) {
        try {
            List<String> evaluations = activiteDAO.getToutesEvaluations();
            StringBuilder sb = new StringBuilder("Toutes les évaluations :\n");
            for (String eval : evaluations) {
                sb.append("- ").append(eval).append("\n");
            }
            outputArea.setText(sb.toString());
        } catch (SQLException ex) {
            outputArea.setText("Erreur lors de l'affichage des évaluations : " + ex.getMessage());
        }
    }
}
