package view;

import javax.swing.*; // Pour les composants graphiques Swing
import java.awt.*; // Pour la gestion de la mise en page
import java.awt.event.ActionEvent; // Pour les événements
import java.sql.*; // Pour les interactions avec la base de données
import java.text.SimpleDateFormat;
import java.time.LocalDateTime; // Pour manipuler la date et l'heure
import java.time.format.DateTimeFormatter;

import dao.CalendrierDAO; // Classe d'accès aux données (Data Access Object)

public class FenetreCalendrier extends JFrame {
    // Déclaration des composants de l'interface
    private CalendrierDAO calendrierDAO;
    private JTextArea zoneTexte;
    private JComboBox<String> comboActivites;
    private JTextField champDebut, champFin, champLieu;
    private JButton boutonAjouter;

    // Constructeur de la fenêtre
    public FenetreCalendrier(Connection connexion) {
        super("Calendrier des Activités"); // Titre de la fenêtre
        calendrierDAO = new CalendrierDAO(connexion); // Initialisation du DAO

        setSize(700, 500); // Taille de la fenêtre
        setLocationRelativeTo(null); // Centrer la fenêtre
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Fermer uniquement cette fenêtre
        setLayout(new BorderLayout()); // Définir le layout principal

        // Zone centrale : zone de texte non modifiable avec barre de défilement
        zoneTexte = new JTextArea();
        zoneTexte.setEditable(false);
        JScrollPane scroll = new JScrollPane(zoneTexte);
        add(scroll, BorderLayout.CENTER);

        // Panel bas pour l'ajout d'une activité
        JPanel panelAjout = new JPanel(new GridLayout(2, 5, 10, 10));
        comboActivites = new JComboBox<>();

        // Formatage des dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now(); // Date et heure actuelles
        LocalDateTime oneWeekLater = now.plusWeeks(1); // Une semaine plus tard

        // Pré-remplissage des champs de date avec l'heure actuelle et +1 semaine
        champDebut = new JTextField(now.format(formatter));
        champFin = new JTextField(oneWeekLater.format(formatter));
        champLieu = new JTextField("Salle Polyvalente");

        boutonAjouter = new JButton("Ajouter");

        // Remplir la liste déroulante avec les noms des activités depuis la BDD
        for (String nom : calendrierDAO.getNomsActivites()) {
            comboActivites.addItem(nom);
        }

        // Ajout des composants et labels dans le panel
        panelAjout.add(new JLabel("Activité :"));
        panelAjout.add(new JLabel("Début (yyyy-MM-dd HH:mm) :"));
        panelAjout.add(new JLabel("Fin :"));
        panelAjout.add(new JLabel("Lieu :"));
        panelAjout.add(new JLabel("")); // Cellule vide pour équilibrer le layout

        panelAjout.add(comboActivites);
        panelAjout.add(champDebut);
        panelAjout.add(champFin);
        panelAjout.add(champLieu);
        panelAjout.add(boutonAjouter);

        add(panelAjout, BorderLayout.SOUTH); // Ajout du panel en bas

        // Écouteur du bouton "Ajouter"
        boutonAjouter.addActionListener((ActionEvent e) -> {
            try {
                // Récupérer les infos du formulaire
                String nomActivite = (String) comboActivites.getSelectedItem();
                Timestamp debut = Timestamp.valueOf(champDebut.getText());
                Timestamp fin = Timestamp.valueOf(champFin.getText());
                String lieu = champLieu.getText();

                // Appel à la méthode DAO pour insérer dans la BDD
                calendrierDAO.ajouterCalendrier(nomActivite, debut, fin, lieu);

                // Mise à jour de l'affichage
                chargerCalendrier();
            } catch (Exception ex) {
                // Afficher un message d'erreur si problème
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            }
        });

        // Chargement initial du calendrier
        chargerCalendrier();

        setVisible(true); // Afficher la fenêtre
    }

    // Méthode pour afficher les activités dans la zone de texte
    private void chargerCalendrier() {
        try {
            String planning = calendrierDAO.afficherCalendrier();
            zoneTexte.setText(planning);
        } catch (SQLException e) {
            zoneTexte.setText("Erreur : " + e.getMessage());
        }
    }
}
