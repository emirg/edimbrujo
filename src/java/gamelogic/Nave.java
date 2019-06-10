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
import java.util.Vector;
import org.dyn4j.geometry.Vector2;

import org.json.simple.JSONObject;

/**
 *
 * @author karen
 */
public abstract class Nave extends Entity {

    protected int countProyectil;
    protected Vector2 direccion;

    //depende de como tratemos la orientacion puede no ser un int
    public Nave(String name, String id, double x, double y, double velocidadX, double velocidadY,double xDir, double yDir, int cantProj) {
        super(name, false, id, x, y, velocidadX, velocidadY, 64, 64);
        this.countProyectil = cantProj;
        this.direccion= new Vector2(xDir,yDir);
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        return null;
    }

    public Nave next(LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        hasChanged = false;
        return this;
    }

    public int getCountProyectil() {
        return countProyectil;
    }

    public void setCountProyectil(int countProyectil) {
        this.countProyectil = countProyectil;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jNave = new JSONObject();
        JSONObject atributo = new JSONObject();

        atributo.put("super", super.toJSON());
        atributo.put("xDir",direccion.x);
        atributo.put("yDir",direccion.y);
        atributo.put("countProyectil", countProyectil);
        jNave.put("Nave", atributo);

        return jNave;
    }
}
