/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamelogic;

import java.util.HashMap;
import java.util.LinkedList;

import engine.Action;
import engine.State;
import engine.StaticState;
import java.util.Random;
import org.json.simple.JSONObject;

/**
 *
 * @author emiliano
 */
public class Asteroide extends Entity {
    protected int worldWidth;
    protected int worldHeight;

    public Asteroide(String name, boolean destroy, String id, double x, double y, double velocidadX, double velocidadY, int worldWidth,int worldHeight) {
        super(name, destroy, id, x, y, velocidadX, velocidadY, 32*(32/((worldWidth)*0.1)), 32*(32/((worldWidth)*0.1)));
        this.worldWidth=worldWidth;
        this.worldHeight=worldHeight;
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
                    state.addEvent("hit"); // Si la nave no muere entonces deberia ser un collide
                    //this.addEvent("collide");
                }
            } else if (state.getName().equals("Proyectil")) {
                Proyectil proyectil = ((Proyectil) state);
                double dist = Math.sqrt((proyectil.x - this.x) * (proyectil.x - this.x) + (proyectil.y - this.y) * (proyectil.y - this.y));
                if (dist <= (this.width / 2 + proyectil.width / 2) || dist <= (this.height / 2 + proyectil.height / 2)) { // Esto va a cambiar segun si terminamos usando una libreria fisica
                    state.addEvent("hit");
                    //this.addEvent("collide"); // No se que tan importante es agregar el collide en el asteroide
                }
            }
        }

        return null;
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates,
            HashMap<String, LinkedList<Action>> actions) {
        hasChanged = true; // El asteroide siempre cambia porque siempre esta en movimiento
        boolean destruido = false;
        double nuevoX = x + velocidad.x;
        double nuevoY = y + velocidad.y;
        // La velocidad es constante
        if (nuevoX > worldWidth) {
            nuevoX = 0;
        }
        LinkedList<String> events = getEvents();
        if (!events.isEmpty()) {
            for (String event : events) {
                switch (event) {
                    case "despawn":
                        destruido = true;
                        break;
                }
            }
        }

        Asteroide newAsteroide = new Asteroide(name, destruido, id, nuevoX, nuevoY, velocidad.x, velocidad.y,worldWidth,worldHeight);
        return newAsteroide;
    }

        @Override
    public void setState(State newAsteroide) {
        super.setState(newAsteroide);
        worldWidth = ((Asteroide) newAsteroide).worldWidth;
        worldHeight = ((Asteroide) newAsteroide).worldHeight;

    }

    @Override
    public JSONObject toJSON() {
        JSONObject jAsteroide = new JSONObject();
        JSONObject atributo = new JSONObject();

        atributo.put("super", super.toJSON());
        jAsteroide.put("Asteroide", atributo);

        return jAsteroide;
    }
}
