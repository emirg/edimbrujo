package gamelogic;

import java.util.HashMap;
import java.util.LinkedList;

public abstract class StaticState extends State {

    @Override
    public State next(LinkedList<State> states, HashMap<String, Action> actions) {
        return this;
    }

    @Override
    public void createState(State newState) {
        //do nothing
    }

    @Override
    public State getState(int numState) {
        return this;
    }

    @Override
    public void setState(State newState) {
        //do nothing
    }

    @Override
    public String toString() {
        return "StaticState";
    }

}
