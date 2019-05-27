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

/**
 *
 * @author karen
 */
public class NaveNeutra extends Nave
{
    //si propietario es nulo es por que no fue reclutado
   
    private String propietario;
    
    public NaveNeutra(String id, double x, double y, int h, int hM, double oriX, double oriY, String prop, int countProjectile, boolean leave, boolean dead)
    {
        super(id,x,y,"Neutra",h,hM,oriX,oriY, countProjectile, leave, dead);
        
        this.propietario = prop;
        
    }
    @Override
    public LinkedList<State> generate(LinkedList<State> estados, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> acciones)
    {
        LinkedList<State> nuevosEst = null;
        LinkedList<Action> listAccion = acciones.get(propietario);
        if(this.propietario != null)
        {
            for(Action accion: listAccion)
            {
                if(accion != null)
                {
                    if(accion.getName().equalsIgnoreCase("fire"))
                            if(accion.getParameter("x") != null && accion.getParameter("y") != null)
                            {
                                double posX = Double.parseDouble(accion.getParameter("x"));
                                double posY = Double.parseDouble(accion.getParameter("y"));
                                
                                if(posX != x || posY != y)
                                {
                                    double velocidadX, velocidadY;
                                    if(posX < x)
                                    {
                                        velocidadX = -1;
                                    }
                                    else
                                    {
                                        if(posX > x)
                                        {
                                            velocidadX = 1;
                                        }
                                        else
                                        {
                                            velocidadX = 0;
                                        }
                                    }
                                    if(posY < y)
                                    {
                                        velocidadY = -1;
                                    }
                                    else
                                    {
                                        if(posY > y)
                                        {
                                            velocidadY = 1;
                                        }
                                        else
                                        {
                                            velocidadY = 0;
                                        }
                                    }
                                    Projectile proyectil = new Projectile(id,countProjectile,velocidadX,velocidadY,x,y,"proyectil",false);
                                    nuevosEst.add(proyectil);
                                }
                            
                        
                    }
                }
            }
            double [] futuraPos = futuraPosicion(acciones);
            for(State estado : estados)
            {
            //Solo se considera el choque con otro jugador
                if(estado != this && estado.getName().equalsIgnoreCase("player") && !((NavePlayer)estado).dead)
                {
                    double [] posContrincante = ((NavePlayer) estado).futuraPosicion(acciones);
                    if(futuraPos[0] == posContrincante[0] && futuraPos[1] == posContrincante[1])
                    {
                        this.addEvent("collide");
                    }
                }
            }
        
        }
        return nuevosEst;
    }
    
    public double [] futuraPosicion(HashMap <String, LinkedList<Action>> acciones)
    {
        double [] pos = new double[2];
        LinkedList<Action> listAccion = acciones.get(id);
        double nuevoX = x;
        double nuevoY = y;
        double nuevaVelX = velocidadX;
        double nuevaVelY = velocidadY;
        
        if(listAccion != null)
        {
            //Revisar
            for(Action accion : listAccion)
            {
                if(accion != null)
                {
                    switch(accion.getName())
                    {
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
    
    public Nave next(HashMap <String , LinkedList<Action>> acciones)
    {
        LinkedList<Action> listAccion = acciones.get(propietario);
        hasChanged = false;
        double nuevoX = x;
        double nuevoY = y;
        int nuevosProyectiles = countProjectile;
        boolean salir = leave;
        boolean muerto = dead;
        int nuevaVida = health;
        boolean destruido = destroy;
        double nuevaVelX = velocidadX;
        double nuevaVelY = velocidadY;
        
        if(!listAccion.isEmpty())
        {
            for(Action accion : listAccion)
            {
                if(!dead)
                {
                    hasChanged = true;
                    switch(accion.getName())
                    {
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
                        case "fire":
                            nuevosProyectiles++;
                            break;
                        case "enter":
                            salir = false;
                            break;
                        case "leave":
                            salir = true;
                    }
                }
            }
        }
        LinkedList<String> eventos = getEvents();
        if(!eventos.isEmpty())
        {
            //Ver en que momento tiene un propietario
            hasChanged = true;
            boolean revivir = false;
            for(String evento: eventos)
            {
               
            }
            
        }
        return null;
       
    }
     @Override
    public JSONObject toJSON()
    {
        JSONObject jNeutra = new JSONObject();
        JSONObject atributo = new JSONObject();
        
        atributo.put("super", super.toJSON());
        atributo.put("propietario", propietario);
        jNeutra.put("NaveNeutra", atributo);
        
        return jNeutra;
    }
}
