package services;


import services.DTO.UtilisateurDTO;

import java.util.List;

public interface GestionUtilisateursProxy {

    /**
     * Creer un utilisateur
     * @param nom
     * @param prenom
     * @return identifiant de l'utilisateur enregistré par le Web-service
     * @throws ExceptionUtilisateurDejaEnregistre : Un utilisateur avec les mêmes attributs a déjà été enregistré
     */
    int creerUtilisateur(String nom, String prenom) throws ExceptionUtilisateurDejaEnregistre;

    /**
     *
     * @param id : Identifiant de l'utilisateur que l'on veut récupérer
     * @return l'utilisateur correspondant à l'identifiant donné
     * @throws ExceptionUtilisateurInexistant : aucun utilisateur ne correspond à l'identifiant donné
     */
    UtilisateurDTO getUtilisateur(int id) throws ExceptionUtilisateurInexistant;

    /**
     * Permet de récupérer tous les utilisateurs
     * @return la liste des utilisateurs enregistrés dans le Web-services
     */
    List<UtilisateurDTO> getUtilisateurs();

    /**
     * Efface toutes les données du Web-service
     */
    void reset();
}
