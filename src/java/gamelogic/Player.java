package gamelogic;

import java.util.HashMap;
import java.util.LinkedList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Player extends Entity {

    private String id;
    private boolean leave;

    public Player(String id, boolean leave, int x, int y) {
        super(x, y);
        this.id = id;
        this.leave = leave;
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
    public State next(LinkedList<State> states, HashMap<String, Action> actions) {
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
                case "enter":
                    newLeave = false;
                    break;
                case "leave":
                    newLeave = true;
                    break;
            }
        }
        Player newPlayer = new Player(id, newLeave, newX, newY);
        return newPlayer;
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
        Player clon = new Player(id, leave, x, y);
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
