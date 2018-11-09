package server;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import logicajuego.Game;

@ApplicationScoped
@ServerEndpoint("/serverWSocket")
public class ServerWSocket {

    private Set<Session> sessions = new HashSet<>();
    private Game game;

    @OnOpen
    public void open(Session session) {
        System.out.println("Session opened ==>");
        sessions.add(session);
        game = Game.iniciarJuego();
        game.addPlayers(session.getId());
        ServerWSocketHilo svh = new ServerWSocketHilo(session, game);
        Thread t = new Thread(svh);
        t.start();
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        System.out.println("new message ==> " + message);
        game.addAction(message, session.getId());
    }

    @OnClose
    public void close(Session session) {
        System.out.println("Session closed ==>");
        sessions.remove(session);
    }

    @OnError
    public void onError(Throwable e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
    }
}
