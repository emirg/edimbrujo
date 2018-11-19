package gamelogic;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Player extends Entity {

    private String id;
    private int cantArrow;

    public Player(String id, boolean leave, int x, int y, String name) {
        super(x, y, name);
        this.id = id;
        this.leave = leave;
        cantArrow = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isLeave() {
        return leave;
    }

    public void setLeave(boolean leave) {
        this.leave = leave;
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, Action> actions) {
        Action action = actions.get(id);
        LinkedList<State> nstates = new LinkedList<>();
        if (action != null) {
            hasChanged = true;
            switch (action.getName()) {
                case "fire":
                    //int posX = Integer.parseInt(action.getParameter("x"));
                    //int posY = Integer.parseInt(action.getParameter("y"));
                    Arrow a = new Arrow(id, cantArrow, x, y, 1, 1, "Arrow");
                    cantArrow++;
                    nstates.add(a);
                    break;
            }
        }
        return nstates;
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, Action> actions) {
        hasChanged = false;
        Action action = actions.get(id);
        int newX = x;
        int newY = y;
        boolean newLeave = leave;
        if (action != null) {
            hasChanged = true;
            switch (action.getName()) {
                case "up":
                    newY = y - 1;
                    break;
                case "down":
                    newY = y + 1;
                    break;
                case "left":
                    newX = x - 1;
                    break;
                case "right":
                    newX = x + 1;
                    break;
                case "upleft":
                    newY = y - 1;
                    newX = x - 1;
                    break;
                case "upright":
                    newY = y - 1;
                    newX = x + 1;
                    break;
                case "downleft":
                    newY = y + 1;
                    newX = x - 1;
                    break;
                case "downright":
                    newY = y + 1;
                    newX = x + 1;
                    break;
                case "enter":
                    newLeave = false;
                    break;
                case "leave":
                    newLeave = true;
                    break;
            }
            if (!((Map) staticStates.get(0)).canWalk(new Point(newX, newY))) {
                newX = x;
                newY = y;
            }
        }
        LinkedList<String> events = getEvents();
        if (!events.isEmpty()) {
            hasChanged = true;
            for (String event : events) {
                switch (event) {
                    case "hit":
                        newLeave = true;
                        break;
                }
            }
        }
        Player newPlayer = new Player(id, newLeave, newX, newY, name);
        return newPlayer;
    }

    public Point posFutura(HashMap<String, Action> actions) {
        Point p;
        Action acc = actions.get(id);
        int newY = y;
        int newX = x;
        if (acc != null) {
            switch (acc.getName()) {
                case "up":
                    newY = y - 1;
                    break;
                case "down":
                    newY = y + 1;
                    break;
                case "left":
                    newX = x - 1;
                    break;
                case "right":
                    newX = x + 1;
                    break;
                case "upleft":
                    newY = y - 1;
                    newX = x - 1;
                    break;
                case "upright":
                    newY = y - 1;
                    newX = x + 1;
                    break;
                case "downleft":
                    newY = y + 1;
                    newX = x - 1;
                    break;
                case "downright":
                    newY = y + 1;
                    newX = x + 1;
                    break;
            }
        }
        p = new Point(newX, newY);
        return p;
    }

    @Override
    public void setState(State newPlayer) {
        id = ((Player) newPlayer).getId();
        leave = ((Player) newPlayer).isLeave();
        x = ((Player) newPlayer).getX();
        y = ((Player) newPlayer).getY();
    }

    @Override
    protected Object clone() {
        Player clon = new Player(id, leave, x, y, name);
        return clon;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonPlayer = new JSONObject();
        JSONObject jsonAttrs = new JSONObject();
        jsonAttrs.put("id", id);
        jsonAttrs.put("leave", leave);
        jsonAttrs.put("x", x);
        jsonAttrs.put("y", y);
        jsonPlayer.put("Player", jsonAttrs);
        return jsonPlayer;
    }

}
