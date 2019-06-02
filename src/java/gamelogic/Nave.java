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

/**
 *
 * @author karen
 */
public abstract class Nave extends Entity {

    protected int health;
    protected int healtMax;
    protected int countProjectile;
    protected boolean leave;
    protected boolean dead;
    //depende de como tratemos la orientacion puede no ser un int

    public Nave(String name, String id, double x, double y, double velocidadX, double velocidadY, int h, int hM, int cantProj, boolean leave, boolean dead) {
        super(name, false, id, x, y, velocidadX, velocidadY, 0, 0);
        this.health = h;
        this.healtMax = hM;
        this.leave = leave;
        this.dead = dead;
        this.countProjectile = cantProj;
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        return null;
    }

    public Nave next(LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        hasChanged = false;
        return this;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealtMax() {
        return healtMax;
    }

    public void setHealtMax(int healtMax) {
        this.healtMax = healtMax;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public int getCountProjectile() {
        return countProjectile;
    }

    public void setCountProjectile(int countProjectile) {
        this.countProjectile = countProjectile;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jNave = new JSONObject();
        JSONObject atributo = new JSONObject();

        atributo.put("super", super.toJSON());
        atributo.put("countProjectile", countProjectile);
        atributo.put("dead", dead);
        atributo.put("health", health);
        atributo.put("healthMax", healtMax);
        atributo.put("leave", leave);
        jNave.put("Nave", atributo);

        return jNave;
    }
}
