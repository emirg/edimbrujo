package gamelogic;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Game implements Runnable {

    private LinkedList<State> states;
    private LinkedList<StaticState> staticStates;
    private HashMap<String, Action> actions;
    private ConcurrentHashMap<String, String> actionsSended; //sessionid -> accion
    private HashMap<String, Player> players;
    private ConcurrentHashMap<String, String> playersSended; //sessionid -> accion
    private String gameState;
    private String gameFullState;
    private String gameStaticState;
    private boolean canRead;
    private boolean endGame;

    private Lobby lobby;

    //constructor
    public Game(Lobby lobby) {
        states = new LinkedList<>();
        staticStates = new LinkedList<>();
        actions = new HashMap<>();
        actionsSended = new ConcurrentHashMap();
        players = new HashMap<>();
        playersSended = new ConcurrentHashMap<>();
        endGame = false;
        this.lobby = lobby;
    }

    @Override
    public void run() {
        init();
        createStaticState();
        LinkedList<State> nextStates;
        while (!endGame) {
            try {
                Thread.sleep(60); //time per frame
                readActions();
                readPlayers();
                nextStates = new LinkedList<>();
                for (State state : states) {
                    nextStates.add(state.next(states, actions));
                }
                for (int i = 0; i < states.size(); i++) {
                    states.get(i).createState(nextStates.get(i));
                }
                createState();
                lobby.stateReady();
                //System.out.println("STATIC: " + gameStaticState);
                //System.out.println("DYNAMIC: " + gameFullState);
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void init() {
        //TODO crear estados dinamicos y estaticos
        HashMap<Point, Integer> cells = new HashMap<>();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                cells.put(new Point(x, y), 0);
            }
        }
        staticStates.add(new gamelogic.Map(cells));
    }

    public void readActions() {
        actions.clear();
        for (Map.Entry<String, String> actionSend : actionsSended.entrySet()) {
            String sessionId = actionSend.getKey();
            String action = actionSend.getValue();
            actions.put(sessionId, new Action(sessionId, action));
            actionsSended.remove(sessionId);
        }
    }

    public void readPlayers() {
        for (Map.Entry<String, String> playerSended : playersSended.entrySet()) {
            String sessionId = playerSended.getKey();
            //String player = playerSended.getValue();
            if (players.get(sessionId) == null) {
                Random random = new Random();
                int x = random.nextInt(9);
                int y = random.nextInt(9);
                Player player = new Player(sessionId, false, x, y);
                states.add(player);
                players.put(sessionId, player);
            }
        }
        for (Map.Entry<String, Player> player : players.entrySet()) {
            String sessionId = player.getKey();
            Player playerState = player.getValue();
            if (playersSended.get(sessionId) == null) {
                playerState.setLeave(true);
            }
        }
    }

    private void createStaticState() {
        JSONObject jsonStaticStates = new JSONObject();
        int i = 0;
        for (StaticState staticState : staticStates) {
            jsonStaticStates.put(i + "", staticState.toJSON());
            i++;
        }
        gameStaticState = jsonStaticStates.toString();
    }

    private void createState() {
        JSONObject jsonFullStates = new JSONObject();
        JSONObject jsonStates = new JSONObject();
        int i = 0;
        int j = 0;
        for (State state : states) {
            jsonFullStates.put(i + "", state.toJSON());
            if (state.hasChanged()) {
                jsonStates.put(j + "", state.toJSON());
                j++;
            }
            i++;
        }
        gameFullState = jsonFullStates.toString();
        gameState = jsonStates.toString();
    }

    public boolean isEndGame() {
        return endGame;
    }

    public void endGame() {
        endGame = true;
    }

    public boolean canRead() {
        return canRead;
    }

    public ConcurrentHashMap<String, String> getActionsSended() {
        return actionsSended;
    }

    public ConcurrentHashMap<String, String> getPlayersSended() {
        return playersSended;
    }

    public String getGameState() {
        return gameState;
    }

    public String getGameFullState() {
        return gameFullState;
    }

    public String getGameStaticState() {
        return gameStaticState;
    }

}
