package websocketserver;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Session;
import gamelogic.Lobby;

public class ServerSender implements Runnable {

    private Session session;
    private Lobby lobby;

    public ServerSender(Session session, Lobby lobby) {
        this.session = session;
        this.lobby = lobby;
    }

    @Override
    public void run() {
        try {
            System.out.println(lobby);
            System.out.println("ENTRO RUN SENDER");
            //envia estados estaticos por unica vez
            String staticStates = lobby.getStaticState();
            session.getAsyncRemote().sendText(staticStates);
            System.out.println("Send statics states to player " + session.getId());
            //envia todos los estados dinamicos por unica vez
            String fullStates = lobby.getFullState();
            session.getAsyncRemote().sendText(fullStates);
            System.out.println("Send full states to player " + session.getId());
            //repite hasta que el juego termina
            while (!lobby.isEndGame()) {
                //envia los estados que cambiaron en cada ciclo del juego
                String states = lobby.getState();
                session.getAsyncRemote().sendText(states);
                System.out.println("Send state changes to player " + session.getId());
                //String estado = "{\"cell\":{\"x\":1,\"y\":1,\"jugador\":\"" + i + "\"}}";//juego.getEstado();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
