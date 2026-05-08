package model;
public class Notification {
    private int id;
    private Utilisateur utilisateur;
    private String message;
    private boolean etatEnvoye;

    public Notification(int id, Utilisateur utilisateur, String message) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.message = message;
        this.etatEnvoye = false;
    }

    public void envoyer() {
        System.out.println("Notification envoyée à " + utilisateur.getNom() + ": " + message);
        this.etatEnvoye = true;
    }
}
