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

/**
 *
 * @author emiliano
 */
public class Asteroide extends Entity {

    protected double x; // La posicion deberia ser (double,double) o (int,int)?
    protected double y;
    protected double velocidadX;
    protected double velocidadY;

    public Asteroide(String id, double x, double y, String name, double velocidadX, double velocidadY) {
        super(x, y, name, false, id);
        this.velocidadX = velocidadX;
        this.velocidadY = velocidadY;
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates,
            HashMap<String, LinkedList<Action>> actions) {

        for (State state : states) {
            if (state.getName().equals("Player") && !((NavePlayer) state).dead) { // Player o Nave, dependiendo cual dejemos
                NavePlayer player = ((NavePlayer) state);
                if (x == player.x && y == player.y) { // Esto va a cambiar segun si terminamos usando una libreria
                                                      // fisica
                    state.addEvent("hit");
                    this.addEvent("collide");
                }
            }
        }

        return null;
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates,
            HashMap<String, LinkedList<Action>> actions) {
        hasChanged = true;
        double nuevoX = x + velocidadX;
        double nuevoY = y + velocidadY;
        // boolean destruido = destroy;

        // falta considerar que es un mundo de 360°
        /*
         * LinkedList<String> events = getEvents(); if (!events.isEmpty()) { hasChanged
         * = true; for (String event : events) { switch (event) { case "collide":
         * destruido = true; break; } } }
         */

        Asteroide newAsteroide = new Asteroide(id, nuevoX, nuevoY, name, velocidadX, velocidadY);
        return newAsteroide;
    }
}
