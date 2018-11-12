/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamelogic;

import java.util.LinkedList;

/**
 *
 * @author Martin
 */
public class PlayerState extends State {

    private String equipo;
    private String nombre;
    private int vida;
    private int daño;
    private int velocidad;
    private String id;

    /*public PlayerState(String equipo, String nombre, int vida, int daño,
            int velocidad, int posX, int posY, String id) {
        super(posX, posY);
        this.equipo = equipo;
        this.nombre = nombre;
        this.vida = vida;
        this.daño = daño;
        this.velocidad = velocidad;
        this.id = id;
        nombreEstado = "player";
    }

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public int getDaño() {
        return daño;
    }

    public void setDaño(int daño) {
        this.daño = daño;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    @Override
    public void createState(State player) {
        PlayerState old = new PlayerState(equipo, nombre, vida, daño, velocidad, x, y, id);
        record.add(old);
        this.equipo = ((PlayerState) player).getEquipo();
        this.nombre = ((PlayerState) player).getNombre();
        this.vida = ((PlayerState) player).getVida();
        this.daño = ((PlayerState) player).getDaño();
        this.velocidad = ((PlayerState) player).getVelocidad();
        x = ((PlayerState) player).getX();
        y = ((PlayerState) player).getY();
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<Action> actions) {
        State newState = new PlayerState(equipo, nombre, vida, daño, velocidad, x, y, id);
        for (Action action : actions) {
            //si el pj ejecuta accion moverse
            if (action.getId().equals(id)) {
                if (action.getName().equals("mover")) {
                    //si se mueve, entonces hay que ver que no caiga ningun personaje o ataque
                    for (State state : states) {
                        if (state.equals("proyectil") || state.equals("conjuro")) {
                            //verificar si el ataque cae acá
                        } else if (state.equals("personaje")) {
                            //verificar si el personaje se mueve aca
                        }
                    }
                }
            }
        }
        return newState;
    }

    @Override
    public String toJSON() {
        String res = "{\"posx\":\"" + x + "\", \"posy\":\"" + y + "\", \"id\":\"" + id + "\"}";
        return res;
    }

    public State ejecutarAccion(State st) {
        State res = null;
        switch (st.nombreEstado) {
            //verificar el comportamiento de este state con cada posible state
            case "casillaVacia":
                break;
            case "personaje":
                break;
            case "pared":
                break;
            case "ataque":

                break;
        }
        return res;
    }*/
}
