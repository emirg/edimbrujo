package gamelogic;

import java.util.HashMap;
import java.util.LinkedList;
import org.json.simple.JSONObject;

public class Entity extends State {

    protected int x;
    protected int y;

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public State next(LinkedList<State> states, HashMap<String, Action> actions) {
        hasChanged = false;
        Entity newEntity = new Entity(x, y);
        return newEntity;
    }

    @Override
    public void setState(State newEntity) {
        this.x = ((Entity) newEntity).getX();
        this.y = ((Entity) newEntity).getY();
    }

    @Override
    protected Object clone() {
        Entity clon = new Entity(x, y);
        return clon;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        JSONObject jsonAttrs = new JSONObject();
        jsonAttrs.put("x", x);
        jsonAttrs.put("y", y);
        jsonEntity.put("Entity", jsonAttrs);
        return jsonEntity;
    }

}
