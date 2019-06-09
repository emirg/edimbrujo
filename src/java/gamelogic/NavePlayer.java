package gamelogic;

import engine.Action;
import engine.State;
import engine.StaticState;
import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import org.json.simple.JSONObject;
import org.dyn4j.collision.Collisions;

public class NavePlayer extends Nave {

    protected int health;
    protected int healthMax;
    protected boolean leave;
    protected boolean dead;

    public NavePlayer(String name, String id, double x, double y, double velocidadX, double velocidadY, int h, int hM, int cantProj, boolean leave, boolean dead) {
        super("NavePlayer", id, x, y, velocidadX, velocidadY, cantProj);
        this.health = h;
        this.healthMax = hM;
        this.leave = leave;
        this.dead = dead;
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> estados, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> acciones) {
        LinkedList<Action> listAccion = acciones.get(id);
        LinkedList<State> listProyectil = new LinkedList();
        if (listAccion != null) {
            for (Action accion : listAccion) {
                if (!dead) {
                    switch (accion.getName()) {
                        case "fire":
                            Projectile proyectil = new Projectile("Proyectil", false, id, x, y, velocidad.x, velocidad.y, 0);
                            listProyectil.add(proyectil);

                    }
                }
            }
        }

        double[] futuraPos = futuraPosicion(acciones);
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
            } else if (estado != this && estado.getName().equalsIgnoreCase("projectile")) { // Choque contra projectile
                Projectile proj = (Projectile) estado;
                double xFuturaAsteroide = proj.x + proj.velocidad.x;
                double yFuturaAsteroide = proj.y + proj.velocidad.y;
                if (futuraPos[0] == xFuturaAsteroide && futuraPos[1] == yFuturaAsteroide) {
                    this.addEvent("hit");
                }
            } else if (estado != this && estado.getName().equalsIgnoreCase("projectile")) { // Choque contra moneda
                Moneda mon = (Moneda) estado;
                if (futuraPos[0] == mon.x && futuraPos[1] == mon.y) {
                    this.addEvent("hit");
                }
            }
            /*else if(){ // Choca contra moneda?
                
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
            for (Action accion : listAccion) {
                if (accion != null) {
                    switch (accion.getName()) {
                        case "move":
                            if (accion.getParameter("x") != null && accion.getParameter("y") != null) {
                                nuevaVelX = Double.parseDouble(accion.getParameter("x"));
                                nuevaVelY = Double.parseDouble(accion.getParameter("y"));
                            }

                            break;
                        case "stop":
                            nuevaVelX = 0;
                            nuevaVelY = 0;
                            break;
                    }
                }
            }
        }
        pos[0] = nuevoX + nuevaVelX;
        pos[1] = nuevoY + nuevaVelY;

        return pos;
    }

    @Override
    public NavePlayer next(LinkedList<State> estados, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> acciones) {
        LinkedList<Action> listAccion = acciones.get(id);
        hasChanged = true;
        double nuevoX = x;
        double nuevoY = y;
        int nuevosProyectiles = countProjectile;
        boolean salir = leave;
        boolean muerto = dead;
        int nuevaVida = health;
        boolean destruido = destroy;
        double nuevaVelX = velocidad.x;
        double nuevaVelY = velocidad.y;

        if (listAccion != null) {
            for (Action accion : listAccion) {
                if (accion != null) {
                    hasChanged = true;
                    //System.out.println("has change");
                    if (!dead) {
                        switch (accion.getName()) {
                            case "move":
                                //System.out.println("Llegue al move");
                                if (accion.getParameter("x") != null && accion.getParameter("y") != null) {
                                    nuevaVelX = Double.parseDouble(accion.getParameter("x"));
                                    nuevaVelY = Double.parseDouble(accion.getParameter("y"));
                                }
                                break;
                            case "stop":
                                //System.out.println("Llegue al stop");
                                nuevaVelX = 0;
                                nuevaVelY = 0;
                                break;
                            case "fire":
                                nuevosProyectiles++;
                                break;
                            case "enter":
                                salir = false;
                                break;
                            case "leave":
                                salir = true;
                                //System.out.println("salir "+salir);
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
            for (String evento : eventos) {
                switch (evento) {
                    case "hit":
                        nuevaVida = nuevaVida - 10;
                        System.out.println(nuevaVida);
                        if (nuevaVida <= 0) {
                            nuevaVelX = 0;
                            nuevaVelY = 0;
                            muerto = true;
                        }
                        break;
                    //ver colisiones
                    case "collide":
                        if (!revivir) {
                            nuevaVelX = velocidad.x;
                            nuevaVelY = velocidad.y;
                            nuevoX = x;
                            nuevoY = y;
                        }
                        break;
                    case "respawn":
                        revivir = true;
                        nuevoX = x;
                        nuevoY = y;
                        nuevaVelX = 0;
                        nuevaVelY = 0;
                        muerto = false;
                        nuevaVida = healthMax;
                        break;
                    case "despawn":
                        //Considera que el jugador sale del juego
                        destruido = true;
                        break;
                }
            }
        }
        NavePlayer nuevoJugador = new NavePlayer(name, id, nuevoX, nuevoY, nuevaVelX, nuevaVelY, nuevaVida, healthMax, nuevosProyectiles, salir, muerto);
        return nuevoJugador;
    }

    @Override
    public void setState(State newPlayer) {
        super.setState(newPlayer);
        this.id = ((NavePlayer) newPlayer).id;
        this.leave = ((NavePlayer) newPlayer).leave;
        this.health = ((NavePlayer) newPlayer).health;
        this.healthMax = ((NavePlayer) newPlayer).healthMax;
        this.dead = ((NavePlayer) newPlayer).dead;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jJugador = new JSONObject();
        JSONObject atributo = new JSONObject();

        atributo.put("super", super.toJSON());
        atributo.put("health", health);
        atributo.put("healthMax", healthMax);
        atributo.put("leave", leave);
        atributo.put("dead", dead);
        jJugador.put("NavePlayer", atributo);

        return jJugador;
    }

    @Override
    public JSONObject toJSON(String sessionId, LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions, JSONObject lastState) {
        NavePlayer thePlayer = getPlayer(sessionId, states);
        return (thePlayer != null && Math.abs(thePlayer.getX() - x) < 10 && Math.abs(thePlayer.getY() - y) < 10)
                ? (lastState == null || hasChanged || isJSONRemover(lastState) ? toJSON() : null)
                : (lastState != null && !isJSONRemover(lastState) ? toJSONRemover() : null);
    }

    protected NavePlayer getPlayer(String sessionId, LinkedList<State> states) {
        NavePlayer thePlayer = null;
        int i = 0;
        while (thePlayer == null && i < states.size()) {
            State state = states.get(i);
            if (state.getName().equals("NavePlayer") && ((NavePlayer) state).id.equals(sessionId)) {
                thePlayer = (NavePlayer) state;
            }
            i++;
        }
        return thePlayer;
    }
}
