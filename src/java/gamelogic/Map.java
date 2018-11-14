package gamelogic;

import java.awt.Point;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Map extends StaticState {

    private HashMap<Point, Integer> cells;
    private int ancho;
    private int alto;

    public Map(HashMap<Point, Integer> cells, String name, int ancho, int alto) {
        super(name);
        this.cells = cells;
        this.ancho = ancho;
        this.alto = alto;
    }

    public boolean canWalk(Point xy) {
        boolean res;
        res = (cells.containsKey(xy) && cells.get(xy)==1);
        return res;
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

    public int getAncho() {
        return ancho;
    }

    public void setAncho(int ancho) {
        this.ancho = ancho;
    }

    public int getAlto() {
        return alto;
    }

    public void setAlto(int alto) {
        this.alto = alto;
    }

    
}
