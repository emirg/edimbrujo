package logicajuego;

public class Action {

    private String name;

    private int playerID;

    public Action(String name, int playerID) {
        this.name = name;
        this.playerID = playerID;
    }

    public Action(String name) {
        this.name = name;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
