package gamelogic;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
        LinkedList<State> newStates;
        while (!endGame) {
            try {
                Thread.sleep(100); //time per frame (10 fps)
                readActions();
                readPlayers();
                newStates = new LinkedList<>();
                for (State state : states) {
                    LinkedList<State> s = state.generate(states, staticStates, actions);
                    if (s != null) {
                        newStates.addAll(s);
                    }
                }
                states.addAll(newStates);
                nextStates = new LinkedList<>();
                for (State state : states) {
                    nextStates.add(state.next(states, staticStates, actions));
                }
                for (int i = 0; i < states.size(); i++) {
                    states.get(i).createState(nextStates.get(i));
                    states.get(i).clearEvents();
                }
                createState();
                lobby.stateReady();
                int i = 0;
                while (i < states.size()) {
                    if (states.get(i).isDestroy()) {
                        System.out.println("se remueve " + states.get(i).getName());
                        states.remove(i);
                        nextStates.remove(i);
                    } else {
                        i++;
                    }
                }
                //System.out.println("STATIC: " + gameStaticState);
                //System.out.println("DYNAMIC: " + gameFullState);
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void init() {
        //TODO crear estados dinamicos y estaticos
        File map = new File("C:\\Users\\Martin\\Desktop\\Edimbrujo\\src\\java\\files\\map.csv");
        loadMap(map);
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
                gamelogic.Map map = (gamelogic.Map) staticStates.get(0);
                int x;
                int y;
                do {
                    Random random = new Random();
                    x = random.nextInt(map.getAncho() + 1);
                    y = random.nextInt(map.getAlto() + 1);
                } while (!map.canWalk(new Point(x, y)));
                Player player = new Player(sessionId, false, x, y, "Player");
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

    private void loadMap(File fileMap) {
        try {
            String linea;
            HashMap<Point, Integer> cells = new HashMap<>();
            BufferedReader buffer = new BufferedReader(new FileReader(fileMap));
            int y = 0;
            int x = 0;
            while ((linea = buffer.readLine()) != null) {
                String[] cols = linea.split(",");
                for (x = 0; x < cols.length; x++) {
                    cells.put(new Point(x, y), Integer.parseInt(cols[x]));
                }
                y++;
            }
            staticStates.add(new gamelogic.Map(cells, "Map", x, y));

        } catch (IOException ex) {
            Logger.getLogger(Game.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addAction(String sid, String action) {
        Player p = players.get(sid);
        if (p != null) {
            if (!p.isLeave()) {
                //si existe el player y no salio ni murio, entonces puede hacer accion
                actionsSended.put(sid, action);
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
