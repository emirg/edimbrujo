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
public class Neutra extends Nave
{
    //si propietario es nulo es por que no fue reclutado
   
    private String propietario;
    
    public Neutra(String id, int x, int y, int h, int hM, int orientacion, String prop)
    {
        super(id,x,y,"Neutra",h,hM,orientacion);
        
        this.propietario = prop;
        
    }
    public LinkedList<State> generate(LinkedList<State> estados, HashMap <String, LinkedList<Action>> acciones)
    {
        
    }
    public Nave next(HashMap <String , LinkedList<Action>> acciones)
    {
       
    }
}
