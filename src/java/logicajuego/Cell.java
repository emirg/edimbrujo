package logicajuego;

import java.util.LinkedList;

public class Cell extends State {

    private int x;
    private int y;

    public Cell(int x, int y) {
        record = new LinkedList<>();
        this.x = x;
        this.y = y;
    }

    @Override
    public void createState(State cell) {
        record.add(new Cell(x, y));
        this.x = ((Cell) cell).getX();
        this.y = ((Cell) cell).getY();
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<Action> actions) {
        return new Cell(x, y);
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

    public String toString() {
        return "cell(" + x + "," + y + ")";
    }

}
