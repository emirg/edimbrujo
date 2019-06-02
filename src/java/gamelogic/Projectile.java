package gamelogic;

import engine.Action;
import engine.State;
import engine.StaticState;
import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import org.json.simple.JSONObject;

public class Projectile extends Entity {

    protected int number;

    public Projectile(String name, boolean destroy, String id, double x, double y, double velocidadX, double velocidadY, int number) {
        super("Projectile", destroy, id, x, y, velocidadX, velocidadY, 0, 0);
        this.number = number;
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {

        for (State state : states) {
            if (state.getName().equals("Player") && !((NavePlayer) state).dead) {
                NavePlayer player = ((NavePlayer) state);

                if (x == player.x && y == player.y) {
                    state.addEvent("hit");
                    this.addEvent("collide");
                }
            }
        }

        return null;
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
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
