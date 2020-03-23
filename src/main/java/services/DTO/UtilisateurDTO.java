package services.DTO;


public class UtilisateurDTO {

    private int identifiant;
    private String nom;
    private String prenom;






    public UtilisateurDTO() {

    }

    public static UtilisateurDTO creer(String nom, String prenom) {
        UtilisateurDTO utilisateurDTO = new UtilisateurDTO();

        utilisateurDTO.setNom(nom);
        utilisateurDTO.setPrenom(prenom);
        return utilisateurDTO;
    }


    public int getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(int identifiant) {
        this.identifiant = identifiant;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
}
