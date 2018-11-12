package gamelogic;

import java.awt.Point;
import java.util.HashMap;

public class Map extends StaticState {

    private HashMap<Point, Boolean> walls;

    public Map(HashMap<Point, Boolean> walls) {
        this.walls = walls;
    }

    public boolean canWalk(Point xy) {
        return walls.get(xy);
    }

    @Override
    public String toString() {
        String map = "";
        for (java.util.Map.Entry<Point, Boolean> entry : walls.entrySet()) {
            Point key = entry.getKey();
            Boolean value = entry.getValue();
            if (value) {
                map += "W";
            } else {
                map += "F";
            }
            /*if (key.getX() == 10) {
                map += "\n";
            }*/
        }
        return map;
    }

}
