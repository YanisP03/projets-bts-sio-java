package model;
public class Evaluation {
    private int utilisateurId;
    private int activiteId;
    private int note;
    private String commentaire;

    public Evaluation(int utilisateurId, int activiteId, int note, String commentaire) {
        this.utilisateurId = utilisateurId;
        this.activiteId = activiteId;
        this.note = note;
        this.commentaire = commentaire;
    }

    public int getUtilisateurId() { return utilisateurId; }
    public int getActiviteId() { return activiteId; }
    public int getNote() { return note; }
    public String getCommentaire() { return commentaire; }
}
