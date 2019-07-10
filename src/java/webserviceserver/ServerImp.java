package webserviceserver;

import engine.Lobby;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Stateless
@Path("/server")
public class ServerImp {

    /*
    Acciones:
    "move" en un sentido (x,y)
    "fire"
    "stop"
     */
    private Lobby lobby;
    int session;

    @GET
    @Path("/enter")
    public String playerEnter(@QueryParam("rol") String rol) {
        lobby = Lobby.startGame();
        SecureRandom random = new SecureRandom();
        rol = rol + random.nextInt(1000000000);
        // String session = bytes.toString();
        lobby.addAction(rol, "enter");
        lobby.addAction(rol, "start");
        return rol;
    }

    @GET
    @Path("/test")
    public String test() {
        return "prueba";
    }

    @GET
    @Path("/exit")
    public void playerExit(@QueryParam("session") String session) {
        lobby = Lobby.startGame();
        lobby.addAction(session, "leave");
    }

    @GET
    @Path("/action")
    public String receiveAction(@QueryParam("action") String action, @QueryParam("session") String session) {
        System.out.println(action + " del jugador" + session);

        lobby = Lobby.startGame();
        if (action.equalsIgnoreCase("move")) { // Si es un "move" hay que indicar la dirección/velocidad
            String move = "{\"name\": \"move\", \"priority\": \"1\",\"parameters\": [{\"name\": \"x\", \"value\": \""
                    + 1 + "\"},{\"name\": \"y\", \"value\": \"" + 1 + "\"}]}";

            System.out.println(move);
            lobby.addAction(session, move);
        } else if (action.equalsIgnoreCase("respuesta")) { // Si es un "fire" no hay que hacer nada sobre la accion
            String res = "{\"name\": \"respuesta\", \"priority\": \"0\",\"parameters\": [{\"name\": \"opcionElegida\", \"value\": \"" + 1 + "\"}]}";
            System.out.println(res);
            lobby.addAction(session, res);
        } else {
            lobby.addAction(session, action);
        }
        return "okey";
    }

    @GET
    @Path("/actionMove")
    public String move(@QueryParam("x") String x, @QueryParam("y") String y,
            @QueryParam("session") String session) {

        lobby = Lobby.startGame();
        String move = "{\"name\": \"move\", \"priority\": \"0\",\"parameters\": [{\"name\": \"x\", \"value\": \"" + x + "\"},{\"name\": \"y\", \"value\": \"" + y + "\"}]}";

        System.out.println(move);
        lobby.addAction(session, move);
        return "okey";
    }

    @GET
    @Path("/actionAnswer")
    public String answer(@QueryParam("respuesta") String x, @QueryParam("session") String session) {

        lobby = Lobby.startGame();
        //String move = "{\"name\": \"move\", \"priority\": \"0\",\"parameters\": [{\"name\": \"x\", \"value\": \"" + x + "\"},{\"name\": \"y\", \"value\": \"" + y + "\"}]}";
        String res = "{\"name\": \"respuesta\", \"priority\": \"0\",\"parameters\": [{\"name\": \"opcionElegida\", \"value\": \"" + x + "\"}]}";

        System.out.println(res);
        lobby.addAction(session, res);
        return "okey";
    }

    @GET
    @Path("/getFullState")
    public String getFullState() throws InterruptedException {
        lobby = Lobby.startGame();
        String state = "error";
        try {
            state = lobby.getFullState();

        } catch (InterruptedException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return state;
    }

    @GET
    @Path("/getFullStaticState")
    public String getFullStaticState() {
        lobby = Lobby.startGame();
        String state = "error";
        try {
            state = lobby.getStaticState();

        } catch (InterruptedException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return state;
    }

    @GET
    @Path("/getState")
    public String getState() {
        lobby = Lobby.startGame();
        String state = "error";
        try {
            state = lobby.getState();

        } catch (InterruptedException ex) {
            Logger.getLogger(ServerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return state;
    }

}
