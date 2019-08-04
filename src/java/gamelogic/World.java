package gamelogic;

import engine.Action;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.Map;
import engine.State;
import engine.StaticState;
import java.util.Random;

public class World extends State {

    protected LinkedList<String> players;
    protected int width;
    protected int height;
    protected boolean spawn;

    public World(LinkedList<String> players, String name, boolean destroy, String id, int worldWidth, int worldHeight, boolean spawn) {
        super(name, destroy, id == null ? UUID.randomUUID().toString() : id);
        this.players = players;
        this.width = worldWidth;
        this.height = worldHeight;
        this.spawn = spawn;
        //System.out.println(spawn);

    }

    public LinkedList<State> createSpawns(LinkedList<State> states) {
        Random r = new Random();
        LinkedList<State> newStates = new LinkedList<>();
        int x, y, v;
        int cantAsteroides = height / 50;

        for (int i = 0; i < cantAsteroides; i++) {
            x = r.nextInt(width);
            y = r.nextInt(height);
            v = r.nextInt(5) + 10;
            newStates.add(new Asteroide("Asteroide", false, "" + i, x, y, v, 0, width, height));
        }

        //states.add(new Asteroide("Asteroide", false, "0", 0, 100, 10, 0,width,height)); // Capaz convenga que el id sea 
        //states.add(new Asteroide("Asteroide", false, "1", 100, 250, 12, 0,width,height)); // algo mas significativo como
        //states.add(new Asteroide("Asteroide", false, "2", 50, 350, 15, 0,width,height)); // "asteroideX" con X el numero
        //states.add(new Asteroide("Asteroide", false, "3", 200, 550, 18, 0,width,height));
        for (int i = 0; i < 10; i++) {
            x = r.nextInt(width);
            y = r.nextInt(height);
            newStates.add(new Moneda("Moneda", false, "" + i, x, y, 0, 0, width, height));
        }
        /**
         * (String name, boolean destroy, String id, double x, double y, double
         * velocidadX, double velocidadY, double xDir, double yDir, int
         * cantProj, NavePlayer prop, String posible, boolean d, String p)
         */
        for (int i = 0; i < 5; i++) {
            //System.out.println("cargando naves neutras");
            x = r.nextInt(width);
            y = r.nextInt(height - 50);
            newStates.add(new NaveNeutra("NaveNeutra", false, "neutra" + i, x, y, 0, 0, 1, 0, 0, true, "", 0, width, height));
        }

        return newStates;

    }

    private void updatePlayers(LinkedList<State> states, LinkedList<StaticState> staticStates, LinkedList<State> newStates, HashMap<String, LinkedList<Action>> actions) {
        //obtiene los puntos de spawn para personajes que atacan y defienden, y para las torres
        for (Map.Entry<String, LinkedList<Action>> actionEntry : actions.entrySet()) {
            String id = actionEntry.getKey();
            LinkedList<Action> actionsList = actionEntry.getValue();
            for (Action action : actionsList) {
                switch (action.getName()) {
                    case "start":
                        NavePlayer newPlayer = new NavePlayer("NavePlayer", null, false, id, 200, 200, 0, 0, 1, 0, 100, 100,
                                0, 0, false, false, "", null, false, -1, 0, width, height);

                        newStates.add(newPlayer);
                        //newPlayer.addEvent("spawn");
                        break;
                    /*case "leave": // TODO: Falta comprobar que este leave sea del World 
                        for (State state : states) {
                            if (state.getName().equals("NavePlayer") && ((NavePlayer) state).id.equals(id)) {
                                state.addEvent("despawn");
                                break;
                            }
                        }
                        break;
                    */
                }
            }
        }
        //return newStates;
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        LinkedList<State> newStates = new LinkedList<>();
        //System.out.println(spawn);
        // System.out.println("Cant states: "+  states.size());
        if (states.size() == 1 && spawn) {
            System.out.println("createspawn");
            //System.out.println("width"+width);
            //System.out.println("height"+height);
            newStates.addAll(createSpawns(states));
            this.spawn = false;
        }
        updatePlayers(states, staticStates, newStates, actions);
        return newStates;
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        hasChanged = false;
        int nuevoWidth = this.width;
        int nuevoHeight = this.height;
        boolean newSpawn = this.spawn;
        boolean newDestroy = this.destroy;
        String newId = this.id;
        LinkedList<String> newPlayers = (LinkedList<String>) players.clone();
        for (Map.Entry<String, LinkedList<Action>> actionEntry : actions.entrySet()) {
            String id = actionEntry.getKey();
            LinkedList<Action> actionsList = actionEntry.getValue();
            for (Action action : actionsList) {
                switch (action.getName()) {
                    case "enter":
                        hasChanged = true;
                        System.out.println("World> Player " + id + " entered the game.");
                        newPlayers.add(id);
                        break;
                    case "leave":
                        newPlayers.remove(id);
                        if (this.id.equalsIgnoreCase(action.getId())) {
                            hasChanged = true;
                            for (int i = 0; i < states.size(); i++) {
                                if (states.get(i) != this) {
                                    states.get(i).addEvent("despawn");
                                }
                            }
                        }

                        /* for (int j = 0; j < staticStates.size(); j++) {
                            staticStates.get(j).setDestroy(true);
                        } */
                        break;
                    case "tamañoCanvas":
                        hasChanged = true;
                        newId = action.getId();
                        nuevoWidth = Integer.parseInt(action.getParameter("width"));
                        //this.width = Integer.parseInt(action.getParameter("width"));
                        nuevoHeight = Integer.parseInt(action.getParameter("height"));
                        //this.height = Integer.parseInt(action.getParameter("height"));
                        //System.out.println("width "+nuevoWidth);
                        //System.out.println("height "+nuevoHeight);
                        //newPlayers = new LinkedList<String>();
                        newSpawn = true;
                        //this.spawn = true;
                        break;

                }
            }
        }
        World newWorld = new World(newPlayers, name, newDestroy, newId, nuevoWidth, nuevoHeight, newSpawn);
        return newWorld;
    }

    @Override
    public void setState(State newWorld) {
        super.setState(newWorld);
        players = ((World) newWorld).players;
        width = ((World) newWorld).width;
        height = ((World) newWorld).height;
        spawn = ((World) newWorld).spawn;
    }

    @Override
    protected Object clone() {
        World clon = new World(players, name, destroy, id, width, height, spawn);
        return clon;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonWorld = new JSONObject();
        JSONObject jsonAttrs = new JSONObject();
        jsonAttrs.put("super", super.toJSON());

        JSONArray jsonPlayers = new JSONArray();
        for (String player : players) {
            jsonPlayers.add(player);
        }
        jsonAttrs.put("players", jsonPlayers);

        jsonWorld.put("World", jsonAttrs);
        return jsonWorld;
    }

}
