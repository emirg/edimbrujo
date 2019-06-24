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
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        //TODO in concrete class
        return null;
    }

    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        //TODO in concrete class
        hasChanged = false;
        boolean nuevoDes = this.destroy;
        LinkedList<Action> listAccion = actions.get(this.idNavePlayer);
         LinkedList<Action> listAccion2 = actions.get(this.idNaveNeutra);
       /* if (!actions.isEmpty()) 
        {
            hasChanged = true;
            if (listAccion != null) 
            {
                for (Action accion : listAccion) 
                {

                    if (accion != null) 
                    {
                        switch(accion.getName())
                        {
                            /*case "respuesta":
                                if( Integer.parseInt(accion.getParameter(id)) == this.correcta)
                                {
                                    Action ac1 =new Action(this.idNavePlayer,"aliar");
                                    ac1.putParameter("idNeutra",this.idNaveNeutra );
                                    
                                    Action ac2 = new Action(this.idNaveNeutra,"seguir");
                                    ac2.putParameter("idPlayer", this.idNavePlayer);
                                    listAccion.add(ac1);
                                    listAccion2.add(ac2);
                                    
                                    
                                }
                                else
                                {
                                    Action ac = new Action(this.idNavePlayer,"liberar");
                                    ac.putParameter("idPlayer", this.idNavePlayer);
                                    Action ac3 = new Action(this.idNaveNeutra,"liberar");
                                    ac3.putParameter(this.idNaveNeutra, "liberar");
                                    listAccion.add(ac);
                                    listAccion2.add(ac3);
                                    
                                    
                                }
                                      
                                nuevoDes = true;      
                            break;
                    }
                }
            }    
        }*/
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
