package model;
public class Utilisateur {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private int age;  // Ajout de l'attribut age

    // Constructeur
    public Utilisateur(int id, String nom, String prenom, String email, String role, int age) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.age = age;  // Initialisation de l'âge
    }

    // Getter et Setter pour l'âge
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // Getter pour l'ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter et Setter pour le nom
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    // Getter et Setter pour le prénom
    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    // Getter et Setter pour l'email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter et Setter pour le role
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return nom + " " + prenom;
    }
}