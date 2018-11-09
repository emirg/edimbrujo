package logicajuego;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game implements Runnable {

    private static LinkedList<State> states;
    private static LinkedList<Action> actions;
    private static Map<String, String> actionsSync; //sessionid->accion
    private static LinkedList<Player> players;
    private static String jsonStates;
    private static boolean endGame;
    private static boolean canRead;
    //primer int para fila, segundo int para columna.
    private static HashMap<Integer, HashMap<Integer, State>> mapa;

    private static Game game;

    private void Game() {
        //queda priva3 para que no puedan instanciarlo
    }

    //constructor singleton
    public static synchronized Game iniciarJuego() {
        if (game == null) {
            //System.out.println("ejecuta");
            game = new Game();
            game.init();
            //System.out.println("entraca");
            Thread t = new Thread(game);
            //t.start();
        }
        return game;
    }

    @Override
    public void run() {
        LinkedList<State> nextStates;
        while (!endGame) {
            try {
                Thread.sleep(1000);
                canRead = false;
                readActions();
                nextStates = new LinkedList<>();
                for (State state : states) {
                    nextStates.add(state.next(states, actions));
                }
                for (int i = 0; i < states.size(); i++) {
                    states.get(i).createState(nextStates.get(i));
                    System.out.println(states.get(i).toString());
                }
                endGame = terminaJuego();
                crearJSON();
                canRead = true;
                notifyAll();
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void init() {
        actionsSync = new ConcurrentHashMap();
        players = new LinkedList<>();
        states = new LinkedList<>();
        endGame = false;
        canRead = false;
        //cargar mapa
        /*states.add(new Cell(0, 0));
        states.add(new Cenew Cell(0, 0));ll(0, 1));
        states.add(new Cell(1, 0));
        states.add(new Cell(1, 1));*/
    }

    public void readActions() {
        actions = new LinkedList<>();
        for (Player player : players) {
            actions.add(new Action(actionsSync.get(player.getId()), player.getId()));
        }
    }

    public void addAction(String acc, String id) {
        actionsSync.put(id, acc);
    }

    public void addPlayers(String id) {
        players.add(new Player(id));
    }

    public boolean terminaJuego() {
        return false;
    }

    private void crearJSON() {
        for (State state : states) {
            jsonStates += state.toJSON();
        }
    }

    public synchronized String getState() throws InterruptedException {
        while (!canRead) {
            wait();
        }
        //notifyAll();
        return jsonStates;
    }

}
