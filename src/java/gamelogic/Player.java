package gamelogic;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Player extends Entity {

    protected String id;
    protected int cantArrow;
    protected boolean dead;
    protected boolean leave;

    public Player(String id, int cantArrow, boolean dead, boolean leave, int x, int y, String name, boolean destroy) {
        super(x, y, name, destroy);
        this.id = id;
        this.cantArrow = cantArrow;
        this.dead = dead;
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

    public int getCantArrow() {
        return cantArrow;
    }

    public void setCantArrow(int cantArrow) {
        this.cantArrow = cantArrow;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, Action> actions) {
        Action action = actions.get(id);
        LinkedList<State> newStates = new LinkedList<>();
        if (action != null) {
            if (!dead) {
                switch (action.getName()) {
                    case "fire":
                        int posX = Integer.parseInt(action.getParameter("x"));
                        int posY = Integer.parseInt(action.getParameter("y"));
                        if (posX != x || posY != y) {
                            int xVelocity;
                            int yVelocity;
                            if (posX < x) {
                                xVelocity = -1;
                            } else if (posX > x) {
                                xVelocity = 1;
                            } else {
                                xVelocity = 0;
                            }
                            if (posY < y) {
                                yVelocity = -1;
                            } else if (posY > y) {
                                yVelocity = 1;
                            } else {
                                yVelocity = 0;
                            }
                            Projectile projectile = new Projectile(xVelocity, yVelocity, id, cantArrow, x, y, "Projectile", false);
                            newStates.add(projectile);
                        }
                        break;
                }
            }
        }
        for (State state : states) {
            if (state != this && state.getName().equals("Player") && !((Player) state).isDead() && !((Player) state).isLeave()) {
                Point myFuturePosition = futurePosition(actions);
                Point otherFuturePosition = ((Player) state).futurePosition(actions);
                if (myFuturePosition.x == otherFuturePosition.x && myFuturePosition.y == otherFuturePosition.y) {
                    this.addEvent("collide");
                }
            }
        }
        return newStates;
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, Action> actions) {
        hasChanged = false;
        Action action = actions.get(id);
        int newX = x;
        int newY = y;
        int newCantArrow = cantArrow;
        boolean newLeave = leave;
        boolean newDead = dead;
        if (action != null) {
            hasChanged = true;
            //System.out.println("ACTION: " + action.getName());
            if (!dead) {
                switch (action.getName()) {
                    case "up":
                        newY = y - 1;
                        break;
                    case "down":
                        newY = y + 1;
                        break;
                    case "left":
                        newX = x - 1;
                        break;
                    case "right":
                        newX = x + 1;
                        break;
                    case "upleft":
                        newY = y - 1;
                        newX = x - 1;
                        break;
                    case "upright":
                        newY = y - 1;
                        newX = x + 1;
                        break;
                    case "downleft":
                        newY = y + 1;
                        newX = x - 1;
                        break;
                    case "downright":
                        newY = y + 1;
                        newX = x + 1;
                        break;
                    case "fire":
                        newCantArrow = cantArrow + 1;
                        break;
                }
            }
            switch (action.getName()) {
                case "enter":
                    newLeave = false;
                    break;
                case "leave":
                    newLeave = true;
                    break;
            }
            if (!((Map) staticStates.get(0)).canWalk(new Point(newX, newY))) {
                newX = x;
                newY = y;
            }
        }
        LinkedList<String> events = getEvents();
        if (!events.isEmpty()) {
            hasChanged = true;
            for (String event : events) {
                switch (event) {
                    case "hit":
                        newDead = true;
                        System.out.println("Player " + id + " has been killed.");
                        break;
                    case "collide":
                        newX = x;
                        newY = y;
                        break;
                }
            }
        }
        Player newPlayer = new Player(id, newCantArrow, newDead, newLeave, newX, newY, name, destroy);
        return newPlayer;
    }

    public Point futurePosition(HashMap<String, Action> actions) {
        Point position;
        Action action = actions.get(id);
        int newY = y;
        int newX = x;
        if (action != null) {
            switch (action.getName()) {
                case "up":
                    newY = y - 1;
                    break;
                case "down":
                    newY = y + 1;
                    break;
                case "left":
                    newX = x - 1;
                    break;
                case "right":
                    newX = x + 1;
                    break;
                case "upleft":
                    newY = y - 1;
                    newX = x - 1;
                    break;
                case "upright":
                    newY = y - 1;
                    newX = x + 1;
                    break;
                case "downleft":
                    newY = y + 1;
                    newX = x - 1;
                    break;
                case "downright":
                    newY = y + 1;
                    newX = x + 1;
                    break;
            }
        }
        position = new Point(newX, newY);
        return position;
    }

    @Override
    public void setState(State newPlayer) {
        super.setState(newPlayer);
        id = ((Player) newPlayer).id;
        cantArrow = ((Player) newPlayer).cantArrow;
        dead = ((Player) newPlayer).dead;
        leave = ((Player) newPlayer).leave;
    }

    @Override
    protected Object clone() {
        Player clon = new Player(id, cantArrow, dead, leave, x, y, name, destroy);
        return clon;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonPlayer = new JSONObject();
        JSONObject jsonAttrs = new JSONObject();
        jsonAttrs.put("super", super.toJSON());
        jsonAttrs.put("id", id);
        jsonAttrs.put("dead", dead);
        jsonAttrs.put("leave", leave);
        jsonPlayer.put("Player", jsonAttrs);
        return jsonPlayer;
    }

}
