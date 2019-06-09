/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamelogic;

import engine.Action;
import engine.State;
import engine.StaticState;
import java.util.HashMap;
import java.util.LinkedList;
import org.json.simple.JSONObject;

/**
 *
 * @author emiliano
 */
public class Moneda extends Entity {

    public Moneda(String name, boolean destroy, String id, double x, double y, double velocidadX, double velocidadY, double width, double height) {
        super("Moneda", destroy, id, x, y, 0, 0, width, height);
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates,
            HashMap<String, LinkedList<Action>> actions) {

        for (State state : states) {
            if (state.getName().equals("NavePlayer") && !((NavePlayer) state).dead) {
                NavePlayer player = ((NavePlayer) state);
                double dist = Math.sqrt((player.x - this.x) * (player.x - this.x) + (player.y - this.y) * (player.y - this.y));
                if (dist <= (this.width / 2 + player.width / 2) || dist <= (this.height / 2 + player.height / 2)) { // Esto va a cambiar segun si terminamos usando una libreria fisica
                    //System.out.println("COLISION"); // Hasta aca llega bien
                    state.addEvent("collect");
                    this.addEvent("collide");
                }
            }
        }

        return null;
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates,
            HashMap<String, LinkedList<Action>> actions) {
        boolean destruido = false;
        LinkedList<String> events = getEvents();
        if (!events.isEmpty()) {
            hasChanged = true;
            for (String event : events) {
                switch (event) {
                    case "collide":
                        destruido = true;
                        break;
                }
            }
        }
        Moneda newMoneda = new Moneda(name, destruido, id, x, y, velocidad.x, velocidad.y, width, height);
        return newMoneda;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jMoneda = new JSONObject();
        JSONObject atributo = new JSONObject();

        atributo.put("super", super.toJSON());
        jMoneda.put("Moneda", atributo);

        return jMoneda;
    }

}
