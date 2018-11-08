package server;

import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint("/serverWSocket")
public class ServerWSocket {

    private Set<Session> sessions = new HashSet<>();

    @OnOpen
    public void open(Session session) {
        System.out.println("Session opened ==>");
        sessions.add(session);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        System.out.println("new message ==> " + message);
        try {
            String estado = "{\"cell\":{\"x\":1,\"y\":1,\"jugador\":\"player1\"}}";//juego.getEstado();
            Thread.sleep(5000);
            for (Session s : sessions) {
                s.getAsyncRemote().sendText(estado);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            sessions.remove(sessions);
        }
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
