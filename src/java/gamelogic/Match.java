package gamelogic;

import engine.Action;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import engine.State;
import engine.StaticState;

public class Match extends State 
{
    protected boolean endGame;
    protected boolean startGame;
    protected LinkedList<String> players;
    protected LinkedList<String> playingPlayers;
    protected LinkedList<String> ready;

    public Match(boolean endGame, boolean startGame, LinkedList<String> players, LinkedList<String> playingPlayers, LinkedList<String> ready,
            String name, boolean destroy) 
    {
        super(name, destroy);
        this.endGame = endGame;
        this.startGame = startGame;
        this.players = players;
        this.playingPlayers = playingPlayers;
        this.ready = ready;
    }

    private void reset(LinkedList<State> estados) {
        // despawnea a todas las torres, proyectiles y jugadores en el mapa
        for (State estado : estados) 
        {
            if (estado.getName().equals("Projectile")) 
            {
                Projectile projectile = (Projectile) estado;
                projectile.addEvent("collide");
            } 
            else 
                if (estado.getName().equals("Player")) 
                {
                    Player player = (Player) estado;
                    player.addEvent("despawn");
                }
        }
    }

    
    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, Action> actions) 
    {
        LinkedList<State> newStates = new LinkedList<>();
        if (!startGame) 
        {
            reset(states);
            addEvent("start");
        }
        
        if (playingPlayers.isEmpty() && startGame) {
            reset(states);
        }
        
        if (startGame) {
            Random random = new Random();
            
            for (State state : states) 
            {
                if (state.getName().equals("Player")) 
                {
                    Player player = (Player) state;
                    if (!player.leave) 
                    {
                        if (player.dead) {
                            if (random.nextInt(100) <= 1) {
                                player.addEvent("respawn");
                            }
                        }
                    }
                }
            }
            
        }
        for (java.util.Map.Entry<String, Action> actionEntry : actions.entrySet()) 
        {
            String id = actionEntry.getKey();
            Action action = actionEntry.getValue();
            hasChanged = true;
            switch (action.getName()) {
            case "restart":
                reset(states);
                addEvent("end");
            }
        }
        return newStates;
    }
    
 

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, Action> actions) 
    {
        hasChanged = false;
        boolean newStartGame = startGame;
        boolean newEndGame = endGame;
        LinkedList<String> newPlayers = (LinkedList<String>) players.clone();
        LinkedList<String> newPlayingPlayers = (LinkedList<String>) playingPlayers.clone();
        LinkedList<String> newReady = (LinkedList<String>) ready.clone();
        
        for (java.util.Map.Entry<String, Action> actionEntry : actions.entrySet()) 
        {
            String id = actionEntry.getKey();
            Action action = actionEntry.getValue();
            hasChanged = true;
            switch (action.getName()) 
            {
            case "enter":
                int x = (int)(Math.random()*10);
                int y = (int)(Math.random()*10);
                if(states != null)
                {
                    //verificar que en ese x e y no haya ninguna entidad
                    Player player = new Player(id,0,false,false,5,5,x,y,"player",false);
                }
                else
                    
                newPlayers.add(id);
                break;
            case "ready":
                if (!ready.contains(id) && !startGame) {
                    newReady.add(id);
                }
                break;
            case "leave":
                newPlayers.remove(id);
                newPlayingPlayers.remove(id);
                newReady.remove(id);
                break;
            case "restart":
                newPlayers.clear();
                newPlayingPlayers.clear();
                newReady.clear();
                break;
            }
        }
        LinkedList<String> events = getEvents();
        if (!events.isEmpty()) {
            hasChanged = true;
            for (String event : events) {
                switch (event) 
                {
                case "start":
                    newStartGame = true;
                    LinkedList<String> playersStates = new LinkedList<>();
                    for (State state : states) {
                        if (state.getName().equals("Player")) 
                        {
                            Player player = (Player) state;
                            playersStates.add(player.id);
                            newPlayingPlayers.add(player.id);
                        }
                    }                
                    break;
                case "end":
                    newStartGame = false;
                    newEndGame = true;
                    newReady = new LinkedList<>();
                    newPlayingPlayers = new LinkedList<>();
                    break;
                }
            }
        }
        if (startGame) 
        {
            hasChanged = true;      
        }
        if (playingPlayers.isEmpty() && startGame) 
        {
            hasChanged = true;
            newStartGame = false;
            newEndGame = true;
        }
        Match newMatch = new Match(newEndGame, newStartGame, newPlayers, newPlayingPlayers, newReady, name, destroy);
        return newMatch;
    }

    @Override
    public void setState(State newMatch) 
    {
        super.setState(newMatch);
        endGame = ((Match) newMatch).endGame;
        startGame = ((Match) newMatch).startGame;
        players = ((Match) newMatch).players;
        playingPlayers = ((Match) newMatch).playingPlayers;
        ready = ((Match) newMatch).ready;
    }

    @Override
    protected Object clone() {
        Match clon = new Match(endGame, startGame, players, playingPlayers, ready, name, destroy);
        return clon;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonMatch = new JSONObject();
        JSONObject jsonAttrs = new JSONObject();
        jsonAttrs.put("super", super.toJSON());
        jsonAttrs.put("endGame", endGame);
        jsonAttrs.put("startGame", startGame);

        JSONArray jsonPlayers = new JSONArray();
        for (String player : players) 
        {
            jsonPlayers.add(player);
        }
        jsonAttrs.put("players", jsonPlayers);

        JSONArray jsonPlayingPlayers = new JSONArray();
        for (String playingPlayer : playingPlayers) 
        {
            jsonPlayingPlayers.add(playingPlayer);
        }
        jsonAttrs.put("playingPlayers", jsonPlayingPlayers);

        JSONArray jsonReady = new JSONArray();
        for (String aReady : ready) 
        {
            jsonReady.add(aReady);
        }
        jsonAttrs.put("ready", jsonReady);
        
        jsonMatch.put("Match", jsonAttrs);
        
        return jsonMatch;
    }

}
