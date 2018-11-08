package logicajuego;

import java.util.LinkedList;

public abstract class State {
    
    protected String nombreEstado;
    
    protected LinkedList<State> record;

    public State next(LinkedList<State> states, LinkedList<Action> actions) {
        return this;
    }

    public void createState(State newState) {
    }

    public State getState(int numState) {
        return record.get(numState);
    }
}
