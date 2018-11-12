package gamelogic;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                Thread.sleep(1000); //time per frame
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
        HashMap<Point, Boolean> walls = new HashMap<>();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                walls.put(new Point(x, y), false);
            }
        }
        staticStates.add(new gamelogic.Map(walls));
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
        gameStaticState = "";
        for (StaticState staticState : staticStates) {
            gameStaticState += staticState.toString();
        }
    }

    private void createState() {
        gameState = "";
        gameFullState = "";
        for (State state : states) {
            gameFullState += state.toString();
            if (state.hasChanged()) {
                gameState += state.toString();
            }
        }
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
