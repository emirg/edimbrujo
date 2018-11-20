/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserviceserver;

import gamelogic.Lobby;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 *
 * @author joan
 */
@Stateless
@Path("/server")
public class ServerImp {

    private Lobby lobby;
    int session;

    @GET
    @Path("/enter")
    public String playerEnter(@QueryParam("rol") String rol) {
        lobby = Lobby.startGame();
        lobby.addPlayer("una session");
        lobby.addAction("un session", "enter");
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        return bytes.toString();
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
    public void receiveAction(@QueryParam("action") String action, @QueryParam("session") String session) {
        lobby = Lobby.startGame();
        lobby.addAction(session, action);
    }

    @GET
    @Path("/getFullState")
    public String getFullState() {
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
