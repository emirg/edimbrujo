/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamelogic;

import engine.Action;
import engine.State;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author karen
 */
public abstract class Nave extends Entity
{
    protected int health;
    protected int healtMax;
    protected boolean dead;
    protected int countProjectile;
    //depende de como tratemos la orientacion puede no ser un int
    protected int orientacion;
    
    public Nave(String id, int x, int y, String name, int h, int hM, int orientacion)
    {
        super(x,y,name,false,id);
        this.health = h;
        this.healtMax = hM;
        this.dead = false;
        this.countProjectile = 0;
        this.orientacion = orientacion;
        
        
    }
    public LinkedList<State> generate(LinkedList<State> estados, HashMap <String, LinkedList<Action>> acciones)
    {
        return null;
    }
    public Nave next(HashMap <String , LinkedList<Action>> acciones)
    {
        hasChanged = false;
        return this;
    }

    public int getHealth() 
    {
        return health;
    }

    public void setHealth(int health) 
    {
        this.health = health;
    }

    public int getHealtMax() 
    {
        return healtMax;
    }

    public void setHealtMax(int healtMax) 
    {
        this.healtMax = healtMax;
    }

    public boolean isDead() 
    {
        return dead;
    }

    public void setDead(boolean dead) 
    {
        this.dead = dead;
    }

    public int getCountProjectile() 
    {
        return countProjectile;
    }

    public void setCountProjectile(int countProjectile) 
    {
        this.countProjectile = countProjectile;
    }
    
}
