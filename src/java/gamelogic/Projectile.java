package gamelogic;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import org.json.simple.JSONObject;

public class Projectile extends Entity {

    protected int xVelocity;
    protected int yVelocity;
    protected String id;
    protected int nroArrow;

    public Projectile(int xVelocity, int yVelocity, String id, int nroArrow, int x, int y, String name, boolean destroy) {
        super(x, y, name, destroy);
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
        this.id = id;
        this.nroArrow = nroArrow;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getxVelocity() {
        return xVelocity;
    }

    public void setxVelocity(int xVelocity) {
        this.xVelocity = xVelocity;
    }

    public int getyVelocity() {
        return yVelocity;
    }

    public void setyVelocity(int yVelocity) {
        this.yVelocity = yVelocity;
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, Action> actions) {
        //si golpea a un jugador le quita vida
        //int newX = x + xVelocity;
        //int newY = y + yVelocity;
        for (State state : states) {
            if (state.getName().equals("Player") && !((Player) state).isDead() && !((Player) state).isLeave()) {
                Player player = ((Player) state);
                //Point futurePosition = player.futurePosition(actions);
                //int playerxVelocity = futurePosition.x - player.x;
                //int playeryVelocity = futurePosition.y - player.y;
                if (x == player.x && y == player.y && id != player.id) {
                    //        || (futurePosition.x == newX && futurePosition.y == newY)
                    //        || ()) {
                    state.addEvent("hit");
                    this.addEvent("collide");
                }
            }
        }
        return null;
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, Action> actions) {
        hasChanged = true;
        int newX = x + xVelocity;
        int newY = y + yVelocity;
        boolean newDestroy = destroy;
        if (!((Map) staticStates.get(0)).canWalk(new Point(newX, newY))) {
            //si llega a una pared
            newX = x;
            newY = y;
            newDestroy = true;
        }
        LinkedList<String> events = getEvents();
        if (!events.isEmpty()) {
            hasChanged = true;
            for (String event : events) {
                switch (event) {
                    case "collide":
                        newDestroy = true;
                        break;
                }
            }
        }
        Projectile newArrow = new Projectile(xVelocity, yVelocity, id, nroArrow, newX, newY, name, newDestroy);
        return newArrow;
    }

    @Override
    public void setState(State newProjectile) {
        super.setState(newProjectile);
        xVelocity = ((Projectile) newProjectile).xVelocity;
        yVelocity = ((Projectile) newProjectile).yVelocity;
        id = ((Projectile) newProjectile).id;
        nroArrow = ((Projectile) newProjectile).nroArrow;
    }

    @Override
    protected Object clone() {
        Projectile clon = new Projectile(xVelocity, yVelocity, id, nroArrow, x, y, name, destroy);
        return clon;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonArrow = new JSONObject();
        JSONObject jsonAttrs = new JSONObject();
        jsonAttrs.put("super", super.toJSON());
        jsonAttrs.put("id", id + "_" + nroArrow);
        jsonAttrs.put("xVelocity", x);
        jsonAttrs.put("yVelocity", y);
        jsonArrow.put("Projectile", jsonAttrs);
        return jsonArrow;
    }

}
