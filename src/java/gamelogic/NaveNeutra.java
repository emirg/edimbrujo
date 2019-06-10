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
    private static final int DISTANCIA_DE_ALIANZA = 150;

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
        if (nuevoPropietario == null) {
            for (State estado : estados) {
                if (estado != this && estado.getName().equalsIgnoreCase("naveplayer") && !((NavePlayer) estado).dead && propietario == null) {
                    NavePlayer nave = (NavePlayer) estado;
                    double dist = Math.sqrt((nave.x - this.x) * (nave.x - this.x) + (nave.y - this.y) * (nave.y - this.y));
                    if (DISTANCIA_DE_ALIANZA <= 150) {
                        nuevoPropietario = (NavePlayer) estado;
                        propietario.navesAliadas.add(this); // Capaz que "nuevoPropietario" funciona, pero por las dudas...
                    }
                }
            }
        }

        if (nuevoPropietario != null && !nuevoPropietario.dead) {
            LinkedList<Action> listAccion = acciones.get(nuevoPropietario.id); // Obtengo las acciones del propietario
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
        } else if (nuevoPropietario != null && nuevoPropietario.dead) {
            nuevoPropietario = null;
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
     * A
     *
     * @param propietario the propietario to set
     */
    public void setPropietario(NavePlayer propietario) {
        this.propietario = propietario;
    }

    public void flock() {
        Vector2 alignment = computeAlignment(this, propietario.navesAliadas); // De donde saco los vecinos?
        Vector2 cohesion = computeCohesion(this, propietario.navesAliadas);
        Vector2 separation = computeSeparation(this, propietario.navesAliadas);
        Vector2 aux = alignment.add(cohesion.add(separation));
        aux.setMagnitude(100);
        this.velocidad.x = aux.x;
        this.velocidad.y = aux.y;
    }

    public Vector2 computeAlignment(NaveNeutra miAgente, LinkedList<Nave> agentes) {
        //console.log(lider);
        //console.log(agentes);
        Vector2 vectorAlignment = new Vector2(0, 0);
        int vecinos = 0;
        double xAgente = miAgente.x;
        double yAgente = miAgente.y;
        for (int i = 0; i < agentes.size(); i++) {
            Nave agente = agentes.get(i);
            if (agentes.get(i) != miAgente) {
                double xAgenteVecino = agente.x;
                double yAgenteVecino = agente.y;

                Vector2 vectorDistancia = new Vector2(xAgente, yAgente).subtract(new Vector2(xAgenteVecino, yAgenteVecino));
                double distanceToTarget = vectorDistancia.getMagnitude();
                //console.log(distanceToTarget);
                if (distanceToTarget < 300) {
                    vectorAlignment.add(agente.velocidad);
                    vecinos++;
                }
            }
        }
        if (vecinos == 0) {
            return vectorAlignment;
        }
        //console.log(vectorAlignment);
        vectorAlignment.x /= vecinos;
        vectorAlignment.y /= vecinos;
        vectorAlignment.normalize();
        return vectorAlignment;
    }

    public Vector2 computeCohesion(NaveNeutra miAgente, LinkedList<Nave> agentes) {
        Vector2 vectorAlignment = new Vector2(0, 0);
        int vecinos = 0;
        double xAgente = miAgente.x;
        double yAgente = miAgente.y;
        for (int i = 0; i < agentes.size(); i++) {
            Nave agente = agentes.get(i);
            if (agente != miAgente) {
                double xAgenteVecino = agente.x;
                double yAgenteVecino = agente.y;
                Vector2 vectorDistancia = new Vector2(xAgente, yAgente).subtract(new Vector2(xAgenteVecino, yAgenteVecino));
                double distanceToTarget = vectorDistancia.getMagnitude();
                if (distanceToTarget < 300) {
                    vectorAlignment.x += xAgenteVecino;
                    vectorAlignment.y += yAgenteVecino;
                    vecinos++;
                }
            }
        }
        if (vecinos == 0) {
            return vectorAlignment;
        }
        vectorAlignment.x /= vecinos;
        vectorAlignment.y /= vecinos;
        vectorAlignment = new Vector2(
                vectorAlignment.x - xAgente,
                vectorAlignment.y - yAgente
        );
        vectorAlignment.normalize();
        return vectorAlignment;
    }

    public Vector2 computeSeparation(NaveNeutra miAgente, LinkedList<Nave> agentes) {
        Vector2 vectorAlignment = new Vector2(0, 0);
        int vecinos = 0;
        double xAgente = miAgente.x;
        double yAgente = miAgente.y;
        for (int i = 0; i < agentes.size(); i++) {
            Nave agente = agentes.get(i);
            if (agente != miAgente) {
                double xAgenteVecino = agente.x;
                double yAgenteVecino = agente.y;
                Vector2 vectorDistancia = new Vector2(xAgente, yAgente).subtract(new Vector2(xAgenteVecino, yAgenteVecino));
                double distanceToTarget = vectorDistancia.getMagnitude();
                if (distanceToTarget < 300) {
                    vectorAlignment.x += xAgenteVecino - xAgente;
                    vectorAlignment.y += yAgenteVecino - yAgente;
                    vecinos++;
                }
            }
        }
        if (vecinos == 0) {
            return vectorAlignment;
        }
        vectorAlignment.x /= vecinos;
        vectorAlignment.y /= vecinos;
        vectorAlignment.negate();
        vectorAlignment.normalize();
        return vectorAlignment;
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
