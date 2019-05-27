package gamelogic;

import engine.Action;
import engine.State;
import engine.StaticState;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;
import org.json.simple.JSONObject;

public class Entity extends State {

    protected double x;
    protected double y;

    public Entity(double x, double y, String name, boolean destroy, String id) {
        super(name, destroy, id == null ? UUID.randomUUID().toString() : id);
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

  
    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        return null;
    }


    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        hasChanged = false;
        Entity newEntity = new Entity(x, y, name, destroy, id);
        return newEntity;
    }

    @Override
    public void setState(State newEntity) {
        super.setState(newEntity);
        x = ((Entity) newEntity).x;
        y = ((Entity) newEntity).y;
    }

    @Override
    protected Object clone() {
        Entity clon = new Entity(x, y, name, destroy, id);
        return clon;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        JSONObject jsonAttrs = new JSONObject();
        jsonAttrs.put("super", super.toJSON());
        jsonAttrs.put("x", x);
        jsonAttrs.put("y", y);
        jsonEntity.put("Entity", jsonAttrs);
        return jsonEntity;
    }

}
