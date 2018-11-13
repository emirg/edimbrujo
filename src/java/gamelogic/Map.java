package gamelogic;

import java.awt.Point;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Map extends StaticState {

    private HashMap<Point, Integer> cells;

    public Map(HashMap<Point, Integer> cells) {
        this.cells = cells;
    }

    public boolean canWalk(Point xy) {
        return cells.containsKey(xy);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonMap = new JSONObject();
        JSONArray jsonCells = new JSONArray();
        for (java.util.Map.Entry<Point, Integer> cell : cells.entrySet()) {
            Point key = cell.getKey();
            Integer value = cell.getValue();
            JSONObject jsonCell = new JSONObject();
            jsonCell.put("val", value);
            jsonCell.put("x", key.x);
            jsonCell.put("y", key.y);
            jsonCells.add(jsonCell);
        }
        jsonMap.put("Map", jsonCells);
        return jsonMap;
    }

}
