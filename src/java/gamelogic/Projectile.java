package gamelogic;

import engine.Action;
import engine.State;
import engine.StaticState;
import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import org.json.simple.JSONObject;

public class Projectile extends Entity {

    protected String id;
    protected int number;
    protected double xVelocity;
    protected double yVelocity;

    public Projectile(String id, int number, double xVelocity, double yVelocity, double x, double y, String name, boolean destroy) {
        super(x, y, "Projectile", destroy, id);
        this.id = id;
        this.number = number;
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) 
    {
        
        for (State state : states) {
            if (state.getName().equals("Player") && !((NavePlayer) state).dead)
            {
                NavePlayer player = ((NavePlayer) state);
                
                if (x == player.x && y == player.y) 
                {
                    state.addEvent("hit");
                    this.addEvent("collide");
                }
            }                 }
            
        return null;
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) 
    {
        hasChanged = true;
        double nuevoX = x + xVelocity;
        double nuevoY = y + yVelocity;
        boolean destruido = destroy;
        
        //falta considerar que es un mundo de 360°
        LinkedList<String> events = getEvents();
        if (!events.isEmpty()) {
            hasChanged = true;
            for (String event : events) {
                switch (event) {
                    case "collide":
                        destruido = true;
                        break;
                }
            }
        }
        Projectile newArrow = new Projectile(id, number, xVelocity, yVelocity, nuevoX, nuevoY, name, destruido);
        return newArrow;
    }

    @Override
    public void setState(State newProjectile) {
        super.setState(newProjectile);
        id = ((Projectile) newProjectile).id;
        number = ((Projectile) newProjectile).number;
        xVelocity = ((Projectile) newProjectile).xVelocity;
        yVelocity = ((Projectile) newProjectile).yVelocity;
    }

    @Override
    protected Object clone() {
        Projectile clon = new Projectile(id, number, xVelocity, yVelocity, x, y, name, destroy);
        return clon;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonArrow = new JSONObject();
        JSONObject jsonAttrs = new JSONObject();
        jsonAttrs.put("super", super.toJSON());
        jsonAttrs.put("number", number);
        jsonAttrs.put("xVelocity", x);
        jsonAttrs.put("yVelocity", y);
        jsonArrow.put("Projectile", jsonAttrs);
        return jsonArrow;
    }

}
