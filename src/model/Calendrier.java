package model;
import java.sql.Timestamp;

public class Calendrier {
    private int calendrierId;
    private int activiteId;
    private Timestamp debut;
    private Timestamp fin;
    private String lieu;
    private String nomActivite; // facultatif pour affichage

    public Calendrier(int calendrierId, int activiteId, Timestamp debut, Timestamp fin, String lieu) {
        this.calendrierId = calendrierId;
        this.activiteId = activiteId;
        this.debut = debut;
        this.fin = fin;
        this.lieu = lieu;
    }

    public Calendrier(int calendrierId, int activiteId, Timestamp debut, Timestamp fin, String lieu, String nomActivite) {
        this(calendrierId, activiteId, debut, fin, lieu);
        this.nomActivite = nomActivite;
    }

    public int getCalendrierId() { return calendrierId; }
    public int getActiviteId() { return activiteId; }
    public Timestamp getDebut() { return debut; }
    public Timestamp getFin() { return fin; }
    public String getLieu() { return lieu; }
    public String getNomActivite() { return nomActivite; }

    @Override
    public String toString() {
        return nomActivite + " (" + debut.toLocalDateTime() + ")";
    }
}
