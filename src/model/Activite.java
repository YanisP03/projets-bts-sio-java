// === Classe Activite ===
package model;

public class Activite {
    private int activiteId;
    private String nom;
    private String description;
    private int ageMin;
    private int ageMax;

    public Activite(int activiteId, String nom, String description, int ageMin, int ageMax) {
        this.activiteId = activiteId;
        this.nom = nom;
        this.description = description;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
    }

    public int getId() {
        return activiteId;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public int getAgeMin() {
        return ageMin;
    }

    public int getAgeMax() {
        return ageMax;
    }

    @Override
    public String toString() {
        return nom + " (\u00e2ge " + ageMin + " \u00e0 " + ageMax + ")";
    }
}