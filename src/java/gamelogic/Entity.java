package gamelogic;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;

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
    public String toString() {
        return ("E(" + x + "," + y + ")");
    }

}
