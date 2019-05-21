package gamelogic;

import engine.Action;
import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import org.json.simple.JSONObject;

public class Player extends Entity {

    protected String id;
    protected int countProjectile;
    protected boolean dead;
    protected boolean leave;
    protected int health;
    protected int healthMax;

    public Player(String id, int countProjectile, boolean dead, boolean leave, int health,
            int healthMax, int x, int y, String name, boolean destroy) 
    {
        super(x, y, name, destroy, id);
        this.id = id;
        this.leave = leave;
        this.countProjectile = countProjectile;
        this.dead = dead;
        this.health = health;
        this.healthMax = healthMax;
    }

    public LinkedList<Projectile> generate(LinkedList<Entity> estados, HashMap <String, LinkedList<Action>> acciones)
    {
        LinkedList<Action> listAccion = acciones.get(id);
        LinkedList<Projectile> listProyectil = new LinkedList <Projectile>();
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
                                int posX = Integer.parseInt(accion.getParameter("x"));
                                int posY = Integer.parseInt(accion.getParameter("y"));
                                
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
        Point futuraPos = futuraPosicion(acciones);
        for(Entity estado : estados)
        {
            //Solo se considera el choque con otro jugador
            if(estado != this && estado.getName().equalsIgnoreCase("player") && !((Player)estado).dead)
            {
                Point posContrincante = ((Player) estado).futuraPosicion(acciones);
                if(futuraPos.x == posContrincante.x && futuraPos.y == posContrincante.y)
                {
                    this.addEvent("collide");
                }
            }
        }
        return listProyectil;
    }
    
    private Point futuraPosicion(HashMap <String, LinkedList<Action>> acciones)
    {
        Point pos;
        LinkedList<Action> listAccion = acciones.get(id);
        int nuevoX = x;
        int nuevoY = y;
        
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
                            nuevoY = y - 1;
                        break;
                        case "down":
                            nuevoY = y + 1;
                        break;
                        case "left":
                            nuevoX = x - 1;
                        break;
                        case "right":
                            nuevoX = x + 1;
                        break;
                    }
                }
            }
        }
        pos = new Point(nuevoX, nuevoY);
        
        return pos;
    }
    
    public Player next(HashMap <String , LinkedList<Action>> acciones)
    {
        LinkedList<Action> listAccion = acciones.get(id);
        hasChanged = false;
        int nuevoX = x;
        int nuevoY = y;
        int nuevosProyectiles = countProjectile;
        boolean salir = leave;
        boolean muerto = dead;
        int nuevaVida = health;
        boolean destruido = destroy;
        
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
                                nuevoY = y - 1;
                            break;
                            case "down":
                                nuevoY = y + 1;
                            break;
                            case "left":
                                nuevoX = x - 1;
                            break;
                            case "rihgt":
                                nuevoX = x + 1;
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
                            muerto = true;
                        }
                    break;
                    case "collide":
                        if(!revivir)
                        {
                            nuevoX = x;
                            nuevoY = y;
                        }
                    break;
                    case "respawn":
                        revivir = true;
                        nuevoX = x;
                        nuevoY = y;
                        muerto = false;
                        nuevaVida = healthMax;
                    break;
                    case "despawn":
                        destruido = true;
                    break;
                }
            }
        }
        Player nuevoJugador = new Player(id, nuevosProyectiles, muerto, leave, nuevaVida, healthMax, nuevoX, nuevoY, name, destruido);
        return nuevoJugador;
    }
    
    public void setState(Player nuevoJ)
    {
        super.setState(nuevoJ);
        id = nuevoJ.id;
        countProjectile = nuevoJ.countProjectile;
        health = nuevoJ.health;
        healthMax = nuevoJ.healthMax;
        dead = nuevoJ.dead;
    }

    public JSONObject toJSON()
    {
        JSONObject jJugador = new JSONObject();
        JSONObject atributo = new JSONObject();
        
        atributo.put("super", super.toJSON());
        atributo.put("id", id);
        atributo.put("countProjectile", countProjectile);
        atributo.put("dead", dead);
        atributo.put("health", health);
        atributo.put("healthMax", healthMax);
        jJugador.put("Payer", atributo);
        
        return jJugador;
    }
}
