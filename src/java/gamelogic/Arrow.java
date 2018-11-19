package gamelogic;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import org.json.simple.JSONObject;

public class Arrow extends Entity {

    private int xVelocity;
    private int yVelocity;
    private String id;
    private int nroArrow;

    public Arrow(String id, int nroA, int x, int y, int xVelocity, int yVelocity, String name) {
        super(x, y, name);
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
        this.id = id;
        nroArrow = nroA;
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
        int newX = x + xVelocity;
        int newY = y + yVelocity;
        for (State state : states) {
            if (state.getName().equals("Player") && !((Player) state).isLeave()) {
                Point posF = ((Player) state).posFutura(actions);
                if (posF.x == newX && posF.y == newY) {
                    state.addEvent("hit");
                    destroy = true;
                    leave = true;
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
        if (!((Map) staticStates.get(0)).canWalk(new Point(newX, newY))) {
            //si llega a una pared
            newX = x;
            newY = y;
            destroy = true;
            leave = true;
        }
        Arrow newArrow = new Arrow(id, nroArrow, newX, newY, xVelocity, yVelocity, name);
        return newArrow;
    }

    @Override
    public void setState(State newArrow) {
        id = ((Arrow) newArrow).getId();
        x = ((Arrow) newArrow).getX();
        y = ((Arrow) newArrow).getY();
        xVelocity = ((Arrow) newArrow).getxVelocity();
        yVelocity = ((Arrow) newArrow).getyVelocity();
    }

    @Override
    protected Object clone() {
        Arrow clon = new Arrow(id, nroArrow, x, y, xVelocity, yVelocity, name);
        return clon;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonArrow = new JSONObject();
        JSONObject jsonAttrs = new JSONObject();
        jsonAttrs.put("id", id+"_"+nroArrow);
        jsonAttrs.put("x", x);
        jsonAttrs.put("y", y);
        jsonAttrs.put("xVelocity", x);
        jsonAttrs.put("yVelocity", y);
        jsonAttrs.put("leave", leave);
        jsonArrow.put("Arrow", jsonAttrs);
        return jsonArrow;
    }

}
