package logicajuego;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game implements Runnable {

    private LinkedList<State> states;
    private LinkedList<Action> actions;
    private LinkedList<Player> players;

    public void Game() {
    }

    @Override
    public void run() {
        players = new LinkedList<>();
        addPlayers();
        states = new LinkedList<>();
        init();
        LinkedList<State> nextStates;
        while (true) {
            try {
                readActions();
                Thread.sleep(1000);
                nextStates = new LinkedList<>();
                for (State state : states) {
                    nextStates.add(state.next(states, actions));
                }
                for (int i = 0; i < states.size(); i++) {
                    states.get(i).createState(nextStates.get(i));
                    System.out.println(states.get(i).toString());
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void init() {
        states.add(new Cell(0, 0));
        states.add(new Cell(0, 1));
        states.add(new Cell(1, 0));
        states.add(new Cell(1, 1));
    }

    public void readActions() {
        actions = new LinkedList<>();
        for (Player player : players) {
            actions.add(new Action("noop"));
        }
    }

    public void addPlayers() {
        players.add(new Player(1));
        players.add(new Player(2));
    }
}
