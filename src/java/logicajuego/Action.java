package logicajuego;

public class Action {

    private String name;

    private String playerID;

    public Action(String name, String playerID) {
        this.name = name;
        this.playerID = playerID;
    }

    public Action(String name) {
        this.name = name;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
