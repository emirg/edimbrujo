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

    public Asteroide(String name, boolean destroy,String id, double x, double y, double velocidadX, double velocidadY) {
        super(name, false, id, x, y, velocidadX, velocidadY, 0, 0);
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates,
            HashMap<String, LinkedList<Action>> actions) {

        for (State state : states) {
            if (state.getName().equals("Player") && !((NavePlayer) state).dead) {
                NavePlayer player = ((NavePlayer) state);
                double dist = Math.sqrt((player.x - this.x) * (player.x - this.x) + (player.y - this.y) * (player.y - this.y));
                if (dist <= (this.width / 2 + player.width / 2) || dist <= (this.height / 2 + player.height / 2)) { // Esto va a cambiar segun si terminamos usando una libreria fisica
                    state.addEvent("collide"); // Si la nave no muere entonces deberia ser un collide
                    this.addEvent("collide");
                }
            } else if (state.getName().equals("Projectile") && !((NavePlayer) state).dead) {
                Projectile projectile = ((Projectile) state);
                double dist = Math.sqrt((projectile.x - this.x) * (projectile.x - this.x) + (projectile.y - this.y) * (projectile.y - this.y));
                if (dist <= (this.width / 2 + projectile.width / 2) || dist <= (this.height / 2 + projectile.height / 2)) { // Esto va a cambiar segun si terminamos usando una libreria fisica
                    state.addEvent("hit"); // Si la nave no muere entonces deberia ser un collide
                    this.addEvent("collide");
                }
            }
        }

        return null;
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates,
            HashMap<String, LinkedList<Action>> actions) {
        hasChanged = true; // El asteroide siempre cambia porque siempre esta en movimiento
        double nuevoX = x + velocidad.x;
        double nuevoY = y + velocidad.y;
        // La velocidad es constante 
        if(nuevoX>5000){
            nuevoX=0;
        }
        Asteroide newAsteroide = new Asteroide(name, false, id, nuevoX, nuevoY, velocidad.x, velocidad.y);
        return newAsteroide;
    }
}
