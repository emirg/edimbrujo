package logicajuego;

import java.util.LinkedList;

public abstract class State {

    protected String nombreEstado;
    protected int x;
    protected int y;

    public State(int x, int y) {
        this.x = x;
        this.y = y;
    }

    protected LinkedList<State> record;

    public State next(LinkedList<State> states, LinkedList<Action> actions) {
        return this;
    }

    public void createState(State newState) {
    }

    public State getState(int numState) {
        return record.get(numState);
    }

    public abstract String toJSON();

    protected int getX() {
        return x;
    }

    protected int getY() {
        return y;
    }
}
