/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamelogic;

import engine.Action;
import engine.State;
import engine.StaticState;
import java.awt.Point;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedList;
import org.json.simple.JSONObject;
import org.dyn4j.geometry.Vector2;

public class NaveNeutra extends Nave {
    //si propietario es nulo es por que no fue reclutado

    //private String propietario; // Podria ser una NavePlayer tambien
    private NavePlayer propietario;

    public NaveNeutra(String name, String id, double x, double y, double velocidadX, double velocidadY, int cantProj, NavePlayer prop) {
        super("NaveNeutra", id, x, y, velocidadX, velocidadY, cantProj);
        this.propietario = prop;
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> estados, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> acciones) {
        LinkedList<State> listProyectil = new LinkedList();
        if (propietario != null) {
            LinkedList<Action> listAccion = acciones.get(propietario.id);
            if (listAccion != null) {
                for (Action accion : listAccion) {
                    switch (accion.getName()) {
                        case "fire":
                            Proyectil proyectil = new Proyectil("Proyectil", false, id, x, y, 50, 0, 0);
                            listProyectil.add(proyectil);

                    }

                }
            }

            /*double[] futuraPos = futuraPosicion(acciones);
            for (State estado : estados) {
                //Choque contra otra nave
                if (estado != this && estado.getName().equalsIgnoreCase("naveplayer") && !((NavePlayer) estado).dead) {
                    double[] posContrincante = ((NavePlayer) estado).futuraPosicion(acciones);
                    if (futuraPos[0] == posContrincante[0] && futuraPos[1] == posContrincante[1]) {
                        this.addEvent("collide");
                    }
                } else if (estado != this && estado.getName().equalsIgnoreCase("asteroide")) { // Choque contra asteroide
                    Asteroide ast = (Asteroide) estado;
                    double xFuturaAsteroide = ast.x + ast.velocidad.x;
                    double yFuturaAsteroide = ast.y + ast.velocidad.y;
                    if (futuraPos[0] == xFuturaAsteroide && futuraPos[1] == yFuturaAsteroide) {
                        this.addEvent("hit");
                    }
                } else if (estado != this && estado.getName().equalsIgnoreCase("proyectil")) { // Choque contra proyectil
                    Proyectil proj = (Proyectil) estado;
                    double xFuturaAsteroide = proj.x + proj.velocidad.x;
                    double yFuturaAsteroide = proj.y + proj.velocidad.y;
                    if (futuraPos[0] == xFuturaAsteroide && futuraPos[1] == yFuturaAsteroide) {
                        this.addEvent("hit");
                    }
                } else if (estado != this && estado.getName().equalsIgnoreCase("moneda")) { // Choque contra moneda
                    Moneda mon = (Moneda) estado;
                    if (futuraPos[0] == mon.x && futuraPos[1] == mon.y) {
                        this.addEvent("collect");
                    }
                }
                
            }*/
        }
        return listProyectil;
    }

    public double[] futuraPosicion(HashMap<String, LinkedList<Action>> acciones) {
        double[] pos = new double[2];
        LinkedList<Action> listAccion = acciones.get(id);
        double nuevoX = x;
        double nuevoY = y;
        double nuevaVelX = velocidad.x;
        double nuevaVelY = velocidad.y;

        if (listAccion != null) {
            //Revisar
            for (Action accion : listAccion) {
                if (accion != null) {
                    switch (accion.getName()) {
                        case "up":
                            nuevoX = x - nuevaVelX;
                            break;
                        case "down":
                            nuevoX = x + nuevaVelX;
                            break;
                        case "left":
                            nuevoY = y - nuevaVelY;
                            break;
                        case "right":
                            nuevoY = y + nuevaVelY;
                            break;
                    }
                }
            }
        }
        pos[0] = nuevoX;
        pos[1] = nuevoY;
        return pos;
    }

    @Override
    public NaveNeutra next(LinkedList<State> estados, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> acciones) {

        hasChanged = true;
        double nuevoX = x;
        double nuevoY = y;
        int nuevosProyectiles = countProyectil;
        boolean destruido = destroy;
        double nuevaVelX = velocidad.x;
        double nuevaVelY = velocidad.y;
        NavePlayer nuevoPropietario = propietario;
        if (propietario != null) {
            LinkedList<Action> listAccion = acciones.get(propietario.id);
            if (listAccion != null) {
                for (Action accion : listAccion) {
                    if (accion != null) {
                        System.out.println(accion.getName());
                        hasChanged = true;
                        //System.out.println("has change");

                        switch (accion.getName()) {
                            case "move":
                                flock();
                                break;
                            case "stop":
                                //System.out.println("Llegue al stop");
                                nuevaVelX = 0;
                                nuevaVelY = 0;
                                break;
                            case "fire":
                                nuevosProyectiles++;
                                break;

                        }
                    }
                }
            }
        }
        nuevoX = nuevoX + nuevaVelX;
        nuevoY = nuevoY + nuevaVelY;
        //System.out.println("(velX,velY): " + nuevaVelX + "," + nuevaVelY);

        LinkedList<String> eventos = getEvents();

        if (!eventos.isEmpty()) {
            hasChanged = true;
            boolean revivir = false;
            //System.out.println(eventos);
            for (String evento : eventos) {
                switch (evento) {

                }
            }
        }
        NaveNeutra nuevaNeutra = new NaveNeutra(name, id, nuevoX, nuevoY, nuevaVelX, nuevaVelY, nuevosProyectiles, nuevoPropietario);
        return nuevaNeutra;
    }

    /**
     * @param propietario the propietario to set
     */
    public void setPropietario(NavePlayer propietario) {
        this.propietario = propietario;
    }

    public void flock() {

    }

    public void computeAlignment() {

    }

    public void computeCohesion() {

    }

    public void computeSeparation() {

    }

    @Override
    public JSONObject toJSON() {
        JSONObject jNeutra = new JSONObject();
        JSONObject atributo = new JSONObject();

        atributo.put("super", super.toJSON());
        atributo.put("propietario", propietario);
        jNeutra.put("NaveNeutra", atributo);

        return jNeutra;
    }
}
