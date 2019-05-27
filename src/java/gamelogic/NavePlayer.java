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

    

    public NavePlayer(String id, int countProjectile, boolean dead, boolean leave, int health,
            int healthMax, double x, double y, String name, boolean destroy, double oriX, double oriY) 
    {
        super(id,x,y,"Player",health,healthMax,oriX, oriY, countProjectile, leave, dead);
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> estados, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> acciones)
    {
        LinkedList<Action> listAccion = acciones.get(id);
        LinkedList<State> listProyectil = new LinkedList ();
        if(listAccion != null)
        {
            for(Action accion : listAccion)
            {
                if(!dead)
                {
                    switch(accion.getName())
                    {
                        case "fire":
                            if(accion.getParameter("x") != null && accion.getParameter("y") != null)
                            {
                                double posX = Double.parseDouble(accion.getParameter("x"));
                                double posY = Double.parseDouble(accion.getParameter("y"));
                                
                                if(posX != x || posY != y)
                                {
                                    int velocidadX, velocidadY;
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
                                    listProyectil.add(proyectil);
                                }
                                
                                
                            }
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
        return listProyectil;
    }
    
    public double [] futuraPosicion(HashMap <String, LinkedList<Action>> acciones)
    {
        double [] pos = new double [2];
        LinkedList<Action> listAccion = acciones.get(id);
        double nuevoX = x;
        double nuevoY = y;
        double nuevaVelX = velocidadX;
        double nuevaVelY = velocidadY;
        
        if(listAccion != null)
        {
            
            for(Action accion : listAccion)
            {
                if(accion != null)
                {
                    switch(accion.getName())
                    {
                        case "up":
                            nuevaVelX++;
                            nuevoX = x - nuevaVelX;
                        break;
                        case "down":
                            nuevaVelX++;
                            nuevoX = x + nuevaVelX;
                        break;
                        case "left":
                            nuevaVelY++;
                            nuevoY = y - nuevaVelY;
                        break;
                        case "right":
                            nuevaVelY++;
                            nuevoX = y + nuevaVelY;
                        break;
                    }
                }
            }
        }
        pos[0] = nuevoX;
        pos[1] = nuevoY;
        
        return pos;
    }
    
    @Override
    public NavePlayer next(LinkedList<State> estados, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> acciones)
    {
        LinkedList<Action> listAccion = acciones.get(id);
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
        
        if(listAccion != null)
        {
            for(Action accion : listAccion)
            {
                if(accion != null)
                {
                    hasChanged = true;
                    if(!dead)
                    {
                        switch(accion.getName())
                        {
                            case "up":
                                nuevaVelX++;
                                nuevoX = x - nuevaVelX;
                            break;
                            case "down":
                                nuevaVelX++;
                                nuevoX = x + nuevaVelX;
                            break;
                            case "left":
                                nuevaVelY++;
                                nuevoY = y - velocidadY;
                            break;
                            case "rihgt":
                                nuevaVelY++;
                                nuevoY = y + velocidadY;
                            break;
                            case "fire":
                                countProjectile++;
                            break;
                            case "enter":
                                salir = false;
                            break;
                            case "leave":
                                salir = true;
                            break;
                        }
                      
                    }
                }
            }
        }
        LinkedList<String> eventos = getEvents();
        if(!eventos.isEmpty())
        {
            hasChanged = true;
            boolean revivir = false;
            for(String evento : eventos)
            {
                switch(evento)
                {
                    case "hit":
                        nuevaVida = health - 10;
                        if(nuevaVida <= 0)
                        {
                            nuevaVelX = 0;
                            nuevaVelY = 0;
                            muerto = true;
                        }
                    break;
                    //ver colisiones
                    case "collide":
                        if(!revivir)
                        {
                            nuevaVelX = velocidadX;
                            nuevaVelY = velocidadY;
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
                        nuevaVida = healtMax;
                    break;
                    case "despawn":
                        //Considera que el jugador sale del juego
                        destruido = true;
                    break;
                }
            }
        }
        NavePlayer nuevoJugador = new NavePlayer(id, nuevosProyectiles, muerto, leave, nuevaVida, healtMax, nuevoX, nuevoY, name, destruido, velocidadX, velocidadY);
        return nuevoJugador;
    }
    
    public void setState(NavePlayer nuevoJ)
    {
        super.setState(nuevoJ);
        id = nuevoJ.id;
        countProjectile = nuevoJ.countProjectile;
        health = nuevoJ.health;
        healtMax = nuevoJ.healtMax;
        dead = nuevoJ.dead;
    }

    @Override
    public JSONObject toJSON()
    {
        JSONObject jJugador = new JSONObject();
        JSONObject atributo = new JSONObject();
        
        atributo.put("super", super.toJSON());
        jJugador.put("NavePlayer", atributo);
        
        return jJugador;
    }
}
