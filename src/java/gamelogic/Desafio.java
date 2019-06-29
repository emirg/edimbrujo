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

/**
 *
 * @author PC
 */
public class Desafio extends State
{
    public String idNaveNeutra;
    public String idNavePlayer;
    public String pregunta;
    public String [] opciones;
    public int correcta;
    public Desafio(String name,boolean destroy,String id,String idNeutra,String idPlayer)
    {
        super(name,destroy,id);
        this.idNaveNeutra=idNeutra;
        this.idNavePlayer=idPlayer;
        this.opciones = new String[3];
        this.pregunta = "2 + 3";
        this.opciones[0]="5";
        this.opciones[1]="6";
        this.opciones[2]="7";
        this.correcta = 1;
    }
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String,
            LinkedList<Action>> actions) 
    {
        LinkedList<State> neutras = new LinkedList();
        LinkedList<Action> listAccion = actions.get(this.idNavePlayer);
        NavePlayer jugador=null;
        NaveNeutra neutra=null;
        if (listAccion != null) 
            {
                for (Action accion : listAccion) 
                {

                    if (accion != null) 
                    {
                        switch(accion.getName())
                        {
                            case "respuesta":
                                
                                int res = Integer.parseInt(accion.getParameter("x"));
                                for(State estado:states)
                                {
                                    if(estado!=null && estado.id.equalsIgnoreCase(this.idNavePlayer))
                                    {
                                        jugador = (NavePlayer)estado;
                                    }
                                    else
                                    {
                                        if(estado!=null && estado.id.equalsIgnoreCase(this.idNaveNeutra))
                                        {
                                            neutra = (NaveNeutra)estado;
                                        }
                                    }
                                    
                                }
                                
                                if(res == correcta)
                                {
                                    
                                    neutra.addEvent("destruir");
                                    
                                    neutras.add(new NaveNeutra(neutra.name,neutra.destroy,this.idNaveNeutra,neutra.x,neutra.y,neutra.velocidad.x,neutra.velocidad.y,
                                    neutra.direccion.x,neutra.direccion.y,neutra.countProyectil,true,this.idNavePlayer,neutra.idBullets));
                                }
                                else
                                {   
                                    
                                   neutra.addEvent("destruir");
                                    
                                    neutras.add(new NaveNeutra(neutra.name,neutra.destroy,this.idNaveNeutra,neutra.x,neutra.y,neutra.velocidad.x,neutra.velocidad.y,
                                    neutra.direccion.x,neutra.direccion.y,neutra.countProyectil,true,"",neutra.idBullets));
                                }
                                jugador.addEvent("liberar");
                                this.addEvent("destruir");
                                break;
                        }
                           
                    }
                }
            }
        return neutras;
    }

    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        //TODO in concrete class
        hasChanged = false;
        boolean nuevoDes = this.destroy;
        LinkedList<String> eventos = getEvents();

        if (!eventos.isEmpty()) 
        {
            hasChanged = true;
            
            for (String evento : eventos) 
            {
                switch (evento) 
                {
                    case "destruir":
                        nuevoDes = true;
                        
                        break;
                }
            }
        }    
        Desafio nuevoDesafio =new Desafio(name,nuevoDes,id,this.idNaveNeutra,this.idNavePlayer);
        
        return nuevoDesafio;
    }
    public void setState(State desafio)
    {
        super.setState(desafio);
        this.idNaveNeutra=((Desafio)desafio).idNaveNeutra;
        this.idNavePlayer=((Desafio)desafio).idNavePlayer;
        
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }

}
