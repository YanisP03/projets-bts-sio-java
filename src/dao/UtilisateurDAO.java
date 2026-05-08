package dao;
import java.sql.*;

import model.Utilisateur;

import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class UtilisateurDAO {
    private final Connection connexion;

    public UtilisateurDAO(Connection connexion) {
        this.connexion = connexion;
    }

    public Connection getConnexion() {
        return connexion;
    }

    // Vérifie les identifiants d'un utilisateur
    public boolean verifierIdentifiants(String email, String motDePasse) {
        String sql = "SELECT mdp FROM utilisateur WHERE email = ?";
        try (PreparedStatement statement = connexion.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultat = statement.executeQuery();

            if (resultat.next()) {
                String motDePasseHash = resultat.getString("mdp");
                return BCrypt.checkpw(motDePasse, motDePasseHash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Récupère le rôle d'un utilisateur par email
    public String getRoleParEmail(String email) {
        String sql = "SELECT role FROM utilisateur WHERE email = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Récupère tous les utilisateurs
    public List<Utilisateur> recupererTousLesUtilisateurs() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT utilisateur_id, nom, prenom, email, role, age FROM utilisateur";

        try (PreparedStatement stmt = connexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Utilisateur utilisateur = new Utilisateur(
                    rs.getInt("utilisateur_id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getInt("age")
                );
                utilisateurs.add(utilisateur);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return utilisateurs;
    }

    // Supprime un utilisateur par email
    public boolean supprimerUtilisateurParEmail(String email) {
        String sql = "DELETE FROM utilisateur WHERE email = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setString(1, email);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Ajoute un utilisateur simple (nom, prénom, email, rôle, âge)
    public boolean ajouterUtilisateur(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateur (nom, prenom, email, role, age) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setString(1, utilisateur.getNom());
            stmt.setString(2, utilisateur.getPrenom());
            stmt.setString(3, utilisateur.getEmail());
            stmt.setString(4, utilisateur.getRole());
            stmt.setInt(5, utilisateur.getAge());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Inscrit un utilisateur complet (sans le nombre d'enfants)
    public boolean inscrireUtilisateurComplet(String email, String motDePasse, String nom, String prenom,
            int age, boolean handicap, String sexe, String telephone) {
        String sql = "INSERT INTO utilisateur (email, mdp, nom, prenom, age, handicap, sexe, telephone, role) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, BCrypt.hashpw(motDePasse, BCrypt.gensalt()));
            stmt.setString(3, nom);
            stmt.setString(4, prenom);
            stmt.setInt(5, age);
            stmt.setBoolean(6, handicap);
            stmt.setString(7, sexe);
            stmt.setString(8, telephone);
            stmt.setString(9, "utilisateur");
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Vérifie si un utilisateur existe par email
    public boolean utilisateurExiste(String email) {
        String sql = "SELECT 1 FROM utilisateur WHERE email = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Récupère l'ID d'un utilisateur par email
    public int getIdParEmail(String email) {
        int id = -1;
        String sql = "SELECT utilisateur_id FROM utilisateur WHERE email = ?";

        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt("utilisateur_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    // Récupère un utilisateur par ID
    public Utilisateur recupererUtilisateurParId(int id) {
        String sql = "SELECT utilisateur_id, nom, prenom, email, role, age FROM utilisateur WHERE utilisateur_id = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Utilisateur(
                    rs.getInt("utilisateur_id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getInt("age")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Récupère les utilisateurs avec un rôle spécifique
    public List<Utilisateur> recupererUtilisateursParRole(String role) {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT utilisateur_id, nom, prenom, email, role, age FROM utilisateur WHERE role = ?";

        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Utilisateur utilisateur = new Utilisateur(
                    rs.getInt("utilisateur_id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getInt("age")
                );
                utilisateurs.add(utilisateur);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return utilisateurs;
    }
}