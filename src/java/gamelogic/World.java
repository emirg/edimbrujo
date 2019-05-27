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
 * @author karen
 */
public class World extends State
{
    private LinkedList<String> players;
    
    public World(LinkedList<String> players, String name, boolean destroy, String id)
    {
        super(name,destroy,id);
        this.players = players;
    }
    
    private void actualizarJugador(LinkedList<State> estados, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> acciones)
    {
        
    }
    
    @Override
    public LinkedList<State> generate(LinkedList<State> estados, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> acciones)
    {
        LinkedList<State> nuevosEstados = new LinkedList();
        //actualizarJugador(estados,nuevosEstados,acciones);
        return nuevosEstados;
    }
    
    @Override
    public State next(LinkedList<State> estados, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> acciones)
    {
        hasChanged = false;
        LinkedList<String> nuevosJugadores = (LinkedList<String>) players.clone();
        return null;
    }
}
