package gamelogic;

import engine.Action;
import engine.State;
import engine.StaticState;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import org.dyn4j.geometry.Vector2;
import org.json.simple.JSONObject;

public class NavePlayer extends Nave {

    private int DISTANCIA_ALIANZA;

    protected String nombreJugador;
    protected int health;
    protected int healthMax;
    protected boolean leave;
    protected boolean dead;
    protected int puntaje;
    protected int idBullets;
    private String pregunta;
    private String[] opciones;
    public boolean bloqueado;
    private int respuesta;
    private String idDesafio;
    private int tiempo;
    protected int worldWidth;
    protected int worldHeight;

    public NavePlayer(String name, String nombreJugador, boolean destroy, String id, double x, double y, double velocidadX, double velocidadY, double xDir, double yDir, int h, int hM, int cantProj, int puntaje, boolean leave, boolean dead, String preg, String[] op, boolean bq, int resp, int t, int worldWidth, int worldHeight) {
        super("NavePlayer", destroy, id, x, y, velocidadX, velocidadY, xDir, yDir, cantProj,
                64 * (64 / ((worldWidth) * 0.25)), 64 * (64 / ((worldWidth) * 0.25)));
        this.health = h;
        this.healthMax = hM;
        this.leave = leave;
        this.dead = dead;
        this.puntaje = puntaje;
        this.idBullets = 0;
        this.pregunta = preg;
        this.opciones = op;
        this.bloqueado = bq;
        this.respuesta = resp;
        this.tiempo = t;
        this.nombreJugador = nombreJugador;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.DISTANCIA_ALIANZA = (worldWidth - worldHeight) / 50;
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> estados, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> acciones) {
        LinkedList<Action> listAccion = acciones.get(id);
        LinkedList<State> listProyectil = new LinkedList();
        if (!bloqueado) {
            if (listAccion != null) {
                for (Action accion : listAccion) {
                    if (!dead) {
                        switch (accion.getName()) {
                            case "fire":
                                String idAux = id + "" + idBullets;
                                Proyectil proyectil = new Proyectil("Proyectil", false, idAux, id, x, y, velocidad.x, velocidad.y, direccion.x, direccion.y, angulo, 0, worldWidth, worldHeight);
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
                    double xFuturaProyectil = proj.x + proj.velocidad.x;
                    double yFuturaProyectil = proj.y + proj.velocidad.y;
                    if (futuraPos[0] == xFuturaProyectil && futuraPos[1] == yFuturaProyectil) {
                        this.addEvent("hit");
                    }
                } else if (estado != this && estado.getName().equalsIgnoreCase("moneda")) { // Choque contra moneda
                    Moneda mon = (Moneda) estado;
                    if (futuraPos[0] == mon.x && futuraPos[1] == mon.y) {
                        this.addEvent("collect");
                    }

                } else if (estado != this && estado.getName().equalsIgnoreCase("naveneutra")) {
                    NaveNeutra neutra = (NaveNeutra) estado;
                    double dist = Math.sqrt((futuraPos[0] - neutra.x) * (futuraPos[0] - neutra.x) + (futuraPos[1] - neutra.y) * (futuraPos[1] - neutra.y));
                    if (dist <= DISTANCIA_ALIANZA && neutra.idPropietario.equalsIgnoreCase("") && neutra.disponible && tiempo == 0) {
                        boolean creaDesafio = true;

                        //este for recorre los estados 
                        for (State estAux : estados) {
                            if (estAux != null && estAux != this && estAux.getName().equalsIgnoreCase("naveplayer")
                                    && !((NavePlayer) estAux).bloqueado) {
                                NavePlayer est = ((NavePlayer) estAux);
                                double[] futuraPosAux = est.futuraPosicion(acciones);
                                double distAux = Math.sqrt((futuraPosAux[0] - neutra.x) * (futuraPosAux[0] - neutra.x)
                                        + (futuraPosAux[1] - neutra.y) * (futuraPosAux[1] - neutra.y));
                                if (dist > distAux) {
                                    creaDesafio = false;
                                }
                            }
                        }
                        if (creaDesafio) {
                            Desafio desa = new Desafio("Desafio", false, "idDest", neutra.id, this.id, worldWidth, worldHeight);
                            this.addEvent("desafiar");
                            listProyectil.add(desa); // Por que agregar el desafio a la lista de proyectiles?
                        }
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
        boolean estaBloqueado = bloqueado;
        String[] op = this.opciones;
        int nuevaRespuesta = respuesta;
        String nuevaPregunta = pregunta;
        String nuevoDesafio = this.idDesafio;
        String nuevoNombreJugador = this.nombreJugador;
        int nuevoTiempo = tiempo - 1;

        if (!bloqueado) {
            if (listAccion != null) {
                for (Action accion : listAccion) {
                    if (accion != null) {
                        // System.out.println(accion.getName());
                        hasChanged = true;
                        if (!dead) {
                            switch (accion.getName()) {
                                case "move":
                                    if (accion.getParameter("x") != null && accion.getParameter("y") != null) {
                                        nuevaVelX = Double.parseDouble(accion.getParameter("x"));
                                        nuevaVelY = Double.parseDouble(accion.getParameter("y"));
                                        nuevaDirX = nuevaVelX;
                                        nuevaDirY = nuevaVelY;
                                    }
                                    break;
                                case "stop":
                                    nuevaVelX = 0;
                                    nuevaVelY = 0;
                                    break;
                                case "respuesta":
                                    op = null;
                                    nuevaRespuesta = -1;
                                    nuevaPregunta = "";
                                    break;
                                case "fire":
                                    nuevosProyectiles++;
                                    break;
                                case "enter":
                                    salir = false;
                                    break;
                                case "leave":
                                    destruido = true;
                                    break;
                                case "nombreJugador":
                                    nuevoNombreJugador = accion.getParameter("nombreElegido");
                            }

                        } else {
                            // System.out.println("respawn " + dead);
                            this.addEvent("respawn");
                        }
                    }
                }
            }
            nuevoX = nuevoX + nuevaVelX;
            /* correccion limitaciones mapa se va a poder hacer un modulo para que quede mas prolijo*/
            if (nuevoX < 0) {
                nuevoX = 0;
            }
            if (nuevoX > worldWidth) {
                nuevoX = worldWidth;
            }
            nuevoY = nuevoY + nuevaVelY;
            if (nuevoY < 0) {
                nuevoY = 0;
            }
            if (nuevoY > worldHeight) {
                nuevoY = worldHeight;
            }
        }

        LinkedList<String> eventos = getEvents();
        if (!eventos.isEmpty()) {
            hasChanged = true;
            boolean revivir = false;
            for (String evento : eventos) {
                switch (evento) {
                    case "hit":
                        if (!bloqueado) {
                            nuevaVida = nuevaVida - 10;
                            // System.out.println(nuevaVida);
                            if (nuevaVida <= 0) {
                                nuevaVelX = 0;
                                nuevaVelY = 0;
                                muerto = true;
                                nuevoPuntaje = nuevoPuntaje - 5;
                            }
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
                        nuevoPuntaje = nuevoPuntaje + 10;
                        break;
                    case "liberar":
                        estaBloqueado = false;
                        break;
                    case "incorrecta":
                        nuevoTiempo = 50;
                        break;
                    case "respawn":
                        revivir = true;
                        nuevoX = Math.random() * (worldWidth - 100) + 100;
                        nuevoY = Math.random() * (worldHeight - 100) + 100;
                        nuevaVelX = 0;
                        nuevaVelY = 0;
                        muerto = false;
                        nuevaVida = healthMax;
                        break;
                    case "despawn":
                        //Considera que el jugador sale del juego
                        destruido = true;
                        break;
                    case "desafiar":
                        estaBloqueado = true;
                        nuevaVelX = 0;
                        nuevaVelY = 0;
                        for (State estado : estados) {
                            if (estado != null && estado.getName().equalsIgnoreCase("desafio") && ((Desafio) estado).idNavePlayer.equalsIgnoreCase(id)) {
                                Desafio des = (Desafio) estado;
                                op = des.opciones;
                                nuevaRespuesta = des.correcta;
                                nuevaPregunta = des.pregunta;

                            }
                        }
                        break;
                }
            }
        }
        if (nuevoTiempo <= 0) {
            nuevoTiempo = 0;
        }
        NavePlayer nuevoJugador = new NavePlayer(name, nuevoNombreJugador, destruido, id, nuevoX, nuevoY, nuevaVelX, nuevaVelY,
                nuevaDirX, nuevaDirY, nuevaVida, healthMax, nuevosProyectiles, nuevoPuntaje, salir, muerto,
                nuevaPregunta, op, estaBloqueado, nuevaRespuesta, nuevoTiempo, worldWidth, worldHeight);

        return nuevoJugador;
    }

    @Override
    public void setState(State newPlayer) {
        super.setState(newPlayer);
        this.nombreJugador = ((NavePlayer) newPlayer).nombreJugador;
        this.id = ((NavePlayer) newPlayer).id;
        this.leave = ((NavePlayer) newPlayer).leave;
        this.health = ((NavePlayer) newPlayer).health;
        this.healthMax = ((NavePlayer) newPlayer).healthMax;
        this.dead = ((NavePlayer) newPlayer).dead;
        this.bloqueado = ((NavePlayer) newPlayer).bloqueado;
        this.puntaje = ((NavePlayer) newPlayer).puntaje;
        this.pregunta = ((NavePlayer) newPlayer).pregunta;
        this.opciones = ((NavePlayer) newPlayer).opciones;
        this.respuesta = ((NavePlayer) newPlayer).respuesta;
        this.tiempo = ((NavePlayer) newPlayer).tiempo;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jJugador = new JSONObject();
        JSONObject atributo = new JSONObject();
        JSONObject opciones = new JSONObject();

        if (this.opciones != null) {
            for (int i = 0; i < this.opciones.length; i++) {
                opciones.put("opcion" + i, this.opciones[i]);
            }
        }

        atributo.put("super", super.toJSON());
        atributo.put("nombreJugador", nombreJugador);
        atributo.put("health", health);
        atributo.put("healthMax", healthMax);
        atributo.put("leave", leave);
        atributo.put("dead", dead);
        atributo.put("puntaje", puntaje);
        atributo.put("bloqueado", bloqueado);
        atributo.put("idBullets", idBullets);
        atributo.put("pregunta", pregunta);
        atributo.put("opciones", opciones);
        atributo.put("respuesta", respuesta);
        atributo.put("idDesafio", idDesafio);

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
