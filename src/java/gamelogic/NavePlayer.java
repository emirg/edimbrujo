package gamelogic;

import engine.Action;
import engine.State;
import engine.StaticState;
import java.util.HashMap;
import java.util.LinkedList;
import org.json.simple.JSONObject;

public class NavePlayer extends Nave {

    protected int health;
    protected int healthMax;
    protected boolean leave;
    protected boolean dead;
    protected int puntaje;
    protected LinkedList<Nave> navesAliadas;
    protected int idBullets;
    private String pregunta;
    private int[] opciones;
    private boolean bloqueado;
    private int respuesta;

    public NavePlayer(String name, boolean destroy, String id, double x, double y, double velocidadX, double velocidadY, double xDir,
            double yDir, int h, int hM, int cantProj, int puntaje, boolean leave, boolean dead, String preg, int[] op,
            boolean bq, int resp) {
        super("NavePlayer", destroy, id, x, y, velocidadX, velocidadY, xDir, yDir, cantProj
        );

        this.health = h;
        this.healthMax = hM;
        this.leave = leave;
        this.dead = dead;
        this.puntaje = puntaje;
        this.navesAliadas = new LinkedList<Nave>();
        this.idBullets = 0;
        this.pregunta = preg;
        this.opciones = op;
        this.bloqueado = bq;
        this.respuesta = resp;
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
                            String idAux = id + "" + idBullets;
                            Proyectil proyectil = new Proyectil("Proyectil", false, idAux, id, x, y, velocidad.x, velocidad.y, direccion.x, direccion.y, angulo, 0);
                            listProyectil.add(proyectil);
                            idBullets++;
                    }
                }
            }
        }

        double[] futuraPos = futuraPosicion(acciones);
        for (State estado : estados) {
            //Choque contra otra nave
            if (estado != this && estado.getName().equalsIgnoreCase("asteroide")) { // Choque contra asteroide
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
        double nuevaDirX = direccion.x;
        double nuevaDirY = direccion.y;

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
        int nuevosProyectiles = countProyectil;
        boolean salir = leave;
        boolean muerto = dead;
        int nuevaVida = health;
        boolean destruido = destroy;
        double nuevaVelX = velocidad.x;
        double nuevaVelY = velocidad.y;
        double nuevaDirX = direccion.x;
        double nuevaDirY = direccion.y;
        int nuevoPuntaje = puntaje;
        boolean estaBq = bloqueado;

        if (listAccion != null) {
            for (Action accion : listAccion) {

                if (accion != null) {
                    System.out.println(accion.getName());
                    hasChanged = true;
                    //System.out.println("has change");
                    if (!dead) {
                        switch (accion.getName()) {
                            case "move":
                                //System.out.println("Llegue al move");
                                if (accion.getParameter("x") != null && accion.getParameter("y") != null) {
                                    nuevaVelX = Double.parseDouble(accion.getParameter("x"));
                                    nuevaVelY = Double.parseDouble(accion.getParameter("y"));
                                    nuevaDirX = nuevaVelX;
                                    nuevaDirY = nuevaVelY;
                                    System.out.println(nuevaDirX);
                                    System.out.println(nuevaDirY);
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
                                destruido = true;
                                //System.out.println("salir "+salir);
                                break;

                        }

                    } else {
                        System.out.println("respawn " + dead);
                        this.addEvent("respawn");
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
                    case "hit":
                        nuevaVida = nuevaVida - 10;
                        System.out.println(nuevaVida);
                        if (nuevaVida <= 0) {
                            nuevaVelX = 0;
                            nuevaVelY = 0;
                            muerto = true;
                            //this.addEvent("respawn"); ser rompe cuando trata de tratarlo 
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
                    case "collect":
                        nuevoPuntaje = nuevoPuntaje + 10; // Si esto se hace dos veces cuando recolecta moneda entonces hay que sacar el state.addEvent de Moneda
                        break;
                    case "aliar":
                        estaBq = true;
                        break;
                    case "respawn":
                        revivir = true;
                        nuevoX = 200;
                        nuevoY = 200;
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
        NavePlayer nuevoJugador = new NavePlayer(name, destruido, id, nuevoX, nuevoY, nuevaVelX, nuevaVelY, nuevaDirX, nuevaDirY, nuevaVida, healthMax, nuevosProyectiles, nuevoPuntaje, salir, muerto, pregunta, opciones, estaBq, respuesta);
        return nuevoJugador;
    }

    public void setPregunta(String preg, int[] op, int resp) {
        this.pregunta = preg;
        this.opciones = op;
        this.respuesta = resp;
    }

    @Override
    public void setState(State newPlayer) {
        super.setState(newPlayer);
        this.id = ((NavePlayer) newPlayer).id;
        this.leave = ((NavePlayer) newPlayer).leave;
        this.health = ((NavePlayer) newPlayer).health;
        this.healthMax = ((NavePlayer) newPlayer).healthMax;
        this.dead = ((NavePlayer) newPlayer).dead;
        this.puntaje = ((NavePlayer) newPlayer).puntaje;
        this.pregunta = ((NavePlayer) newPlayer).pregunta;
        this.opciones = ((NavePlayer) newPlayer).opciones;
        this.respuesta = ((NavePlayer) newPlayer).respuesta;
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
        atributo.put("puntaje", puntaje);
        atributo.put("bloqueado", bloqueado);
        atributo.put("pregunta", pregunta);
        atributo.put("opcion1", opciones[0]);
        atributo.put("opcion2", opciones[1]);
        atributo.put("opcion3", opciones[2]);

        jJugador.put("NavePlayer", atributo);

        return jJugador;
    }

    @Override
    public JSONObject toJSON(String sessionId, LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions, JSONObject lastState) {
        NavePlayer thePlayer = getPlayer(sessionId, states);
        if (thePlayer == null || this.id.equalsIgnoreCase(sessionId)) {
            return lastState == null || hasChanged ? toJSON() : null;
        } else {
            return null;
        }
    }

}
