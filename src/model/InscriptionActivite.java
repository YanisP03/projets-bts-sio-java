package model;
public class InscriptionActivite {
    private int id;
    private int participantId;  // Ce champ peut être soit utilisateurId, soit enfantId
    private int calendrierId;

    public InscriptionActivite(int id, int participantId, int calendrierId) {
        this.id = id;
        this.participantId = participantId;
        this.calendrierId = calendrierId;
    }

    // Getters
    public int getId() { return id; }
    public int getParticipantId() { return participantId; }
    public int getCalendrierId() { return calendrierId; }
}