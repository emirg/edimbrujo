package gamelogic;

import java.util.HashMap;
import java.util.LinkedList;

public class Player extends Entity {

    private String id;
    private boolean leave;

    public Player(String id, boolean leave, int x, int y) {
        super(x, y);
        this.id = id;
        this.leave = leave;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isLeave() {
        return leave;
    }

    public void setLeave(boolean leave) {
        this.leave = leave;
    }

    @Override
    public State next(LinkedList<State> states, HashMap<String, Action> actions) {
        hasChanged = false;
        Action action = actions.get(id);
        System.out.println("ACTIONS: " + actions);
        int newX = x;
        int newY = y;
        if (action != null) {
            System.out.println("ACTION: " + action.getName());
            switch (action.getName()) {
                case "up":
                    newY = y + 1;
                    hasChanged = true;
                    break;
                case "down":
                    newY = y - 1;
                    hasChanged = true;
                    break;
                case "left":
                    newX = x - 1;
                    hasChanged = true;
                    break;
                case "right":
                    newX = x + 1;
                    hasChanged = true;
                    break;
            }
        }
        Player newPlayer = new Player(id, leave, newX, newY);
        return newPlayer;
    }

    @Override
    public void setState(State newPlayer) {
        id = ((Player) newPlayer).getId();
        leave = ((Player) newPlayer).isLeave();
        x = ((Player) newPlayer).getX();
        y = ((Player) newPlayer).getY();
    }

    @Override
    protected Object clone() {
        Player clon = new Player(id, leave, x, y);
        return clon;
    }

    @Override
    public String toString() {
        return ("P(" + id + "," + x + "," + y + "," + leave + ")");
    }

}
