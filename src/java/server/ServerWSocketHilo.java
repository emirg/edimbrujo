/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Session;
import logicajuego.Game;

/**
 *
 * @author Martin
 */
public class ServerWSocketHilo implements Runnable {

    Session session;
    Game game;

    public ServerWSocketHilo(Session s, Game g) {
        session = s;
        game = g;
    }

    @Override
    public void run() {
        try {
            int i = 0;
            while (!game.terminaJuego()) {
                String estado = "{\"cell\":{\"x\":1,\"y\":1,\"jugador\":\""+i+"\"}}";//juego.getEstado();
                Thread.sleep(5000);
                System.out.println("envia "+i);
                session.getAsyncRemote().sendText(estado);
                i++;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerWSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
