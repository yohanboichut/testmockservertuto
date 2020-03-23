package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import services.DTO.UtilisateurDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;


public class GestionUtilisateursProxyImpl implements GestionUtilisateursProxy {
//    private static final String URI_SERVICE="http://127.0.0.1:8080/gestionutilisateursws";
private static final String URI_SERVICE="http://localhost:8089/gestionutilisateursws";
    private static final String UTILISATEUR = "/utilisateurs";
    public static final int CREATED = 201;
    public static final int CONFLICT = 409;
    public static final int OK = 200;
    public static final int NOT_FOUND = 404;

    private HttpClient httpClient = HttpClient.newHttpClient();

    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public int creerUtilisateur(String nom, String prenom) throws ExceptionUtilisateurDejaEnregistre {

        UtilisateurDTO utilisateurDTO = UtilisateurDTO.creer(nom,prenom);
        try {
            String utilisateurDTOJSON = objectMapper.writeValueAsString(utilisateurDTO);
            HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(URI_SERVICE+UTILISATEUR))
                    .header("Content-Type","application/json").POST(HttpRequest.BodyPublishers.ofString(utilisateurDTOJSON)).build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == CREATED) {
                String resourceUri = response.headers().firstValue("Location").get();
                String[] splitIt = resourceUri.split("/");
                return Integer.parseInt(splitIt[splitIt.length-1]);
            }

            if (response.statusCode() == CONFLICT) {
                throw new ExceptionUtilisateurDejaEnregistre();
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Erreur innatendue !");
    }

    @Override
    public UtilisateurDTO getUtilisateur(int id) throws ExceptionUtilisateurInexistant {
        HttpRequest request = HttpRequest.newBuilder(URI.create(URI_SERVICE+UTILISATEUR+"/"+id)).header("Accept","application/json").build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == OK) {
                UtilisateurDTO utilisateurDTO = objectMapper.readerFor(UtilisateurDTO.class).readValue(response.body());
                return utilisateurDTO;
            }
            if (response.statusCode() == NOT_FOUND) {
                throw new ExceptionUtilisateurInexistant();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Erreur innatendue !");
    }

    @Override
    public List<UtilisateurDTO> getUtilisateurs() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(URI_SERVICE+UTILISATEUR)).header("Accept","application/json").build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == OK) {
                String jsonUtilisateurDTOArray = response.body();
                List<UtilisateurDTO> utilisateurDTOList = objectMapper.readValue(jsonUtilisateurDTOArray, new TypeReference<List<UtilisateurDTO>>(){});
                return utilisateurDTOList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Erreur innatendue !");
    }

    @Override
    public void reset() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(URI_SERVICE)).DELETE().build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException  | InterruptedException e) {
            throw new RuntimeException("Erreur innatendue !");
        }

    }
}
