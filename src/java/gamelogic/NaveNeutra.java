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
import org.dyn4j.geometry.Vector2;

public class NaveNeutra extends Nave {
    //si propietario es nulo es por que no fue reclutado

    //private String propietario; // Podria ser una NavePlayer tambien
    private NavePlayer propietario;
    private static final int DISTANCIA_DE_ALIANZA = 150;
    private String idProp; // Por que 'idProp' y a la vez 'propietario'? Quedemosno con solo uno para evitar inconsistencias y facilitar el codigo
    private boolean disponible;
    private String pregunta;
    private int respuesta;
    private static final int[] opciones = {9, 7, 6, 4};
    private String idPosP; // Que es?

    public NaveNeutra(String name, boolean destroy, String id, double x, double y, double velocidadX, double velocidadY, double xDir, double yDir, int cantProj, NavePlayer prop,
            String posible, boolean d, String p) {
        super("NaveNeutra", destroy, id, x, y, velocidadX, velocidadY, xDir, yDir, cantProj);
        this.propietario = prop;
        this.idPosP = posible;
        this.disponible = d;
        this.idProp = p; 
        this.pregunta = "2 + 5";
        this.respuesta = 2;
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> estados, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> acciones) {
        LinkedList<State> listProyectil = new LinkedList();

        if (!this.idProp.equalsIgnoreCase("")) {
            //   System.out.println("TIENE PROPIETARIO  "+(idProp));
            LinkedList<Action> listAccion = acciones.get(this.idProp);

            if (listAccion != null) {

                for (Action accion : listAccion) {
                    switch (accion.getName()) {
                        case "fire":
                            Proyectil proyectil = new Proyectil("Proyectil", false, id, propietario.id, x, y, velocidad.x, velocidad.y, direccion.x, direccion.y, angulo, 0);
                            listProyectil.add(proyectil);

                    }
                }
            }

        }

        return listProyectil;
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
        double nuevaDirX = direccion.x;
        double nuevaDirY = direccion.y;
        NavePlayer nuevoPropietario = propietario;
        String nuevoIdP = this.idProp;
        String nuevoPos = this.idPosP;
        boolean nuevaDis = disponible;
        int resp;

        if (this.idProp.equalsIgnoreCase("")) {

            for (State estado : estados) {
                if (estado != this && estado.getName().equalsIgnoreCase("naveplayer") && !((NavePlayer) estado).dead && propietario == null) {
                    NavePlayer nave = (NavePlayer) estado;
                    double dist = Math.sqrt((nave.x - this.x) * (nave.x - this.x) + (nave.y - this.y) * (nave.y - this.y));

                    if (dist <= DISTANCIA_DE_ALIANZA && disponible) {
                        nuevoPos = nave.id; //Jugador que debe responder la pregunta
                        //Enviar la pregunta a nuevoPos
                        // genera la pregunta pero no tiene propietario hasta que responda
                        nave.setPregunta(pregunta, opciones, respuesta);

                        /*  nuevoPropietario = (NavePlayer) estado;
                        nuevoPropietario.navesAliadas.add(this);
                        nuevoIdP = nuevoPropietario.id;
                        nuevaDis = false;*/
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
                                Vector2 velAux = flock(nuevoPropietario);
                                nuevaVelX = velAux.x;
                                nuevaVelY = velAux.y;
                                //       System.out.println("nuevaX "+nuevaVelX+" nuevaY "+nuevaVelY);

                                // nuevoY = x+ nueva
                                break;
                            case "stop":
                                //System.out.println("Llegue al stop");
                                nuevaVelX = 0;
                                nuevaVelY = 0;
                                break;
                            case "fire":
                                nuevosProyectiles++;
                                break;
                            /* case "respuesta":
                                //controlar respuesta
                                resp = Integer.parseInt(accion.getParameter("1"));
                                if(respuesta == resp)
                                {
                                    for(State estado : estados)
                                    {
                                        if(estado != null && estado.getName().equalsIgnoreCase("naveplayer") && estado.getId().equalsIgnoreCase(nuevoPos))
                                        {
                                            nuevoPropietario = (NavePlayer) estado;
                                            nuevoPropietario.navesAliadas.add(this);
                                            nuevoIdP = nuevoPropietario.id;
                                        }
                                    }
                                }
                                else
                                {
                                    nuevaDis = true;
                                    nuevoPos = "";
                                } 
                               /*     nuevoPropietario = (NavePlayer) estado;
                                    nuevoPropietario.navesAliadas.add(this);
                                    nuevoIdP = nuevoPropietario.id;
                                
                                break; */

                        }
                    }
                }
            }
        } else if (nuevoPropietario != null && nuevoPropietario.dead) {

            nuevoPropietario = null;
            nuevoIdP = "";
            //nuevaDis = true;
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
        NaveNeutra nuevaNeutra = new NaveNeutra(name, destruido, id, nuevoX, nuevoY, nuevaVelX, nuevaVelY, nuevaDirX, nuevaDirY, nuevosProyectiles, nuevoPropietario, nuevoPos, nuevaDis, nuevoIdP);
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

    public Vector2 flock(NavePlayer nuevoPropietario) {

        nuevoPropietario.navesAliadas.add(nuevoPropietario);
        Vector2 alignment = computeAlignment(this, nuevoPropietario.navesAliadas); // De donde saco los vecinos?
        Vector2 cohesion = computeCohesion(this, nuevoPropietario.navesAliadas).multiply(2);
        Vector2 separation = computeSeparation(this, nuevoPropietario.navesAliadas);
        Vector2 aux = alignment.sum(cohesion.sum(separation));
        aux.setMagnitude(100);
        /*  this.velocidad.x = aux.x;
        this.velocidad.y = aux.y;*/
        return aux;
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
                    vectorAlignment.sum(agente.velocidad);
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
    public void setState(State neutra) {
        super.setState(neutra);
        this.id = ((NaveNeutra) neutra).id;
        this.disponible = ((NaveNeutra) neutra).disponible;
        this.propietario = ((NaveNeutra) neutra).propietario;
        this.idProp = ((NaveNeutra) neutra).idProp;
        this.pregunta = ((NaveNeutra) neutra).pregunta;
        this.respuesta = ((NaveNeutra) neutra).respuesta;

        // this.countProyectil= ((NaveNeutra)neutra).countProyectil;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jNeutra = new JSONObject();
        JSONObject opciones = new JSONObject();
        JSONObject atributo = new JSONObject();

        for (int i = 0; i < this.opciones.length; i++) {
            opciones.put("opcion"+i, this.opciones[i]);
        }

        atributo.put("super", super.toJSON());
        atributo.put("propietario", propietario.id); // No podemos poner un objeto en el JSON
        atributo.put("DISTANCIA_DE_ALIANZA", DISTANCIA_DE_ALIANZA);
        atributo.put("idProp", idProp);
        atributo.put("disponible", disponible);
        atributo.put("pregunta", pregunta);
        atributo.put("respuesta", respuesta);
        atributo.put("idPosP", idPosP);
        atributo.put("opciones", opciones);
        
        jNeutra.put("NaveNeutra", atributo);

        return jNeutra;
    }
}
