/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserviceserver;

import gamelogic.Lobby;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;

/**
 *
 * @author joan
 */
@WebService(endpointInterface = "webserviceserver.ServerImp")
public class ServerImp implements Server {

    private Lobby lobby;
    int session;

    @Override
    public void playerEnter(String rol) {
        lobby = Lobby.startGame();
        lobby.addPlayer("una session");
        lobby.addAction("un session", "enter");
    }

    @Override
    public void playerExit(String session) {
        lobby = Lobby.startGame();
        lobby.addAction(session, "leave");
    }

    @Override
    public void receiveAction(String action,String session) {
        lobby = Lobby.startGame();
        lobby.addAction(session, action);
    }

    @Override
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

    @Override
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
