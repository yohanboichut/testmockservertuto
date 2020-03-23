package services;

import org.junit.*;
import org.mockserver.junit.MockServerRule;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpResponse;
import services.DTO.UtilisateurDTO;

import java.util.List;

import static org.mockserver.model.HttpRequest.request;


public class TestGestionUtilisateursProxyImpl {

    private GestionUtilisateursProxy gestionUtilisateursProxy;

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, 8089);


    @Before
    public void initialisation(){
        this.gestionUtilisateursProxy = new GestionUtilisateursProxyImpl();
    }


    @After
    public void reinitilizeWS(){

        mockServerRule.getClient().when(
                request()
                        .withMethod("DELETE")
                        .withPath("/gestionutilisateursws"))
                .respond(HttpResponse.response().withStatusCode(200));

        this.gestionUtilisateursProxy.reset();
    }

    @Test
    public void testCreationUtilisateurOk() throws ExceptionUtilisateurDejaEnregistre {

        mockServerRule.getClient().when(
                request()
                        .withMethod("POST")
                        .withPath("/gestionutilisateursws/utilisateurs")
                        .withHeader("Content-Type","application/json")
                        .withBody("{\"identifiant\":0,\"nom\":\"Boichut\",\"prenom\":\"Yohan\"}"), Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(201).withHeader("Location","/gestionsutilisateursws/utilisateurs/1"));

        int id = this.gestionUtilisateursProxy.creerUtilisateur("Boichut","Yohan");
        Assert.assertEquals(id,1);
    }



    @Test(expected = ExceptionUtilisateurDejaEnregistre.class)
    public void testCreationUtilisateurKO() throws ExceptionUtilisateurDejaEnregistre {
        mockServerRule.getClient().when(
                request()
                        .withMethod("POST")
                        .withPath("/gestionutilisateursws/utilisateurs")
                        .withHeader("Content-Type","application/json")
                        .withBody("{\"identifiant\":0,\"nom\":\"Moal\",\"prenom\":\"Frederic\"}"), Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(201).withHeader("Location","/gestionsutilisateursws/utilisateurs/1"));

        mockServerRule.getClient().when(
                request()
                        .withMethod("POST")
                        .withPath("/gestionutilisateursws/utilisateurs")
                        .withHeader("Content-Type","application/json")
                        .withBody("{\"identifiant\":0,\"nom\":\"Moal\",\"prenom\":\"Frederic\"}"), Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(409));


        try {
            int id = this.gestionUtilisateursProxy.creerUtilisateur("Moal", "Frederic");
        }
        catch (ExceptionUtilisateurDejaEnregistre e) {
            Assert.fail();
        }

        this.gestionUtilisateursProxy.creerUtilisateur("Moal","Frederic");
    }



    @Test
    public void testGetUtilisateur() throws ExceptionUtilisateurInexistant {
        mockServerRule.getClient().when(
                request()
                        .withMethod("POST")
                        .withPath("/gestionutilisateursws/utilisateurs")
                        .withHeader("Content-Type","application/json")
                        .withBody("{\"identifiant\":0,\"nom\":\"Boichut\",\"prenom\":\"Yohan\"}"), Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(201).withHeader("Location","/gestionsutilisateursws/utilisateurs/1"));
        mockServerRule.getClient().when(
                request()
                        .withMethod("GET")
                        .withPath("/gestionutilisateursws/utilisateurs/1")
              , Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withHeader("Content-Type","application/json")
                        .withBody("{\"identifiant\":1,\"nom\":\"Boichut\",\"prenom\":\"Yohan\"}")
                        );


        Integer id = null;
        try {
            id = this.gestionUtilisateursProxy.creerUtilisateur("Boichut","Yohan");
        } catch (ExceptionUtilisateurDejaEnregistre exceptionUtilisateurDejaEnregistre) {
            Assert.fail();
        }
        UtilisateurDTO utilisateurDTO = this.gestionUtilisateursProxy.getUtilisateur(id);
        Assert.assertTrue(utilisateurDTO.getIdentifiant()==id);
        Assert.assertEquals(utilisateurDTO.getNom(),"Boichut");
        Assert.assertEquals(utilisateurDTO.getPrenom(),"Yohan");
    }



    @Test
    public void testGetUtilisateurs() {
        mockServerRule.getClient().when(
                request()
                        .withMethod("POST")
                        .withPath("/gestionutilisateursws/utilisateurs")
                        .withHeader("Content-Type","application/json")
                        .withBody("{\"identifiant\":0,\"nom\":\"Boichut\",\"prenom\":\"Yohan\"}"), Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(201).withHeader("Location","/gestionsutilisateursws/utilisateurs/1"));

        mockServerRule.getClient().when(
                request()
                        .withMethod("POST")
                        .withPath("/gestionutilisateursws/utilisateurs")
                        .withHeader("Content-Type","application/json")
                        .withBody("{\"identifiant\":0,\"nom\":\"Moal\",\"prenom\":\"Frederic\"}"), Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(201).withHeader("Location","/gestionsutilisateursws/utilisateurs/2"));



        mockServerRule.getClient().when(
                request()
                        .withMethod("GET")
                        .withPath("/gestionutilisateursws/utilisateurs")
                , Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(200)
                        .withHeader("Content-Type","application/json")
                        .withBody("[{\"identifiant\":1,\"nom\":\"Boichut\",\"prenom\":\"Yohan\"},{\"identifiant\":2,\"nom\":\"Moal\",\"prenom\":\"Frederic\"}]")
                );

        try {
            this.gestionUtilisateursProxy.creerUtilisateur("Boichut","Yohan");
            this.gestionUtilisateursProxy.creerUtilisateur("Moal","Frederic");
        } catch (ExceptionUtilisateurDejaEnregistre exceptionUtilisateurDejaEnregistre) {
            Assert.fail();
        }
        List<UtilisateurDTO> utilisateurDTOList = this.gestionUtilisateursProxy.getUtilisateurs();
        Assert.assertTrue(utilisateurDTOList.size() == 2);
    }


    @Test(expected = ExceptionUtilisateurInexistant.class)
    public void testGetUtilisateurKO() throws ExceptionUtilisateurInexistant {
        mockServerRule.getClient().when(
                request()
                        .withMethod("POST")
                        .withPath("/gestionutilisateursws/utilisateurs")
                        .withHeader("Content-Type","application/json")
                        .withBody("{\"identifiant\":0,\"nom\":\"Boichut\",\"prenom\":\"Yohan\"}"), Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(201).withHeader("Location","/gestionsutilisateursws/utilisateurs/1"));
        mockServerRule.getClient().when(
                request()
                        .withMethod("GET")
                        .withPath("/gestionutilisateursws/utilisateurs/2")
                , Times.exactly(1))
                .respond(HttpResponse.response().withStatusCode(404)
                );


        Integer id = null;
        try {
            id = this.gestionUtilisateursProxy.creerUtilisateur("Boichut","Yohan");
        } catch (ExceptionUtilisateurDejaEnregistre exceptionUtilisateurDejaEnregistre) {
            Assert.fail();
        }
        UtilisateurDTO utilisateurDTO = this.gestionUtilisateursProxy.getUtilisateur(id+1);
    }









}
