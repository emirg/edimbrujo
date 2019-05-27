package gamelogic;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Match extends State {

    protected LinkedList<String> players;
    protected LinkedList<String> playingPlayers;

    public Match(LinkedList<String> players, LinkedList<String> playingPlayers, boolean destroy) {
        super(name, destroy);
        this.players = players;
        this.playingPlayers = playingPlayers;
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, Action> actions) {
        hasChanged = false;
        LinkedList<String> newPlayers = (LinkedList<String>) players.clone();
        LinkedList<String> newPlayingPlayers = (LinkedList<String>) playingPlayers.clone();
        for (java.util.Map.Entry<String, Action> actionEntry : actions.entrySet()) {
            String id = actionEntry.getKey();
            Action action = actionEntry.getValue();
            hasChanged = true;
            switch (action.getName()) {
                case "enter":
                    newPlayers.add(id);
                    break;
                case "leave":
                    newPlayers.remove(id);
                    newPlayingPlayers.remove(id);
                    newReady.remove(id);
                    break;
            }
        }
    }

    @Override
    public void setState(State newMatch) {
        super.setState(newMatch);
        round = ((Match) newMatch).round;
        countRounds = ((Match) newMatch).countRounds;
        endGame = ((Match) newMatch).endGame;
        endRound = ((Match) newMatch).endRound;
        startGame = ((Match) newMatch).startGame;
        players = ((Match) newMatch).players;
        playingPlayers = ((Match) newMatch).playingPlayers;
        ready = ((Match) newMatch).ready;
    }

    @Override
    protected Object clone() {
        Match clon = new Match(round, countRounds, endGame, endRound, startGame, teamAttacker, sizeTeam, players, playingPlayers, ready, teamPoints, name, destroy);
        return clon;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonMatch = new JSONObject();
        JSONObject jsonAttrs = new JSONObject();
        jsonAttrs.put("super", super.toJSON());
        jsonAttrs.put("round", round);
        jsonAttrs.put("countRounds", countRounds);
        jsonAttrs.put("endGame", endGame);
        jsonAttrs.put("endRound", endRound);
        jsonAttrs.put("startGame", startGame);
        //jsonAttrs.put("teamAttacker", teamAttacker);
        jsonAttrs.put("sizeTeam", sizeTeam);

        JSONArray jsonPlayers = new JSONArray();
        for (String player : players) {
            jsonPlayers.add(player);
        }
        jsonAttrs.put("players", jsonPlayers);

        JSONArray jsonPlayingPlayers = new JSONArray();
        for (String playingPlayer : playingPlayers) {
            jsonPlayingPlayers.add(playingPlayer);
        }
        jsonAttrs.put("playingPlayers", jsonPlayingPlayers);

        JSONArray jsonReady = new JSONArray();
        for (String aReady : ready) {
            jsonReady.add(aReady);
        }
        jsonAttrs.put("ready", jsonReady);

        JSONArray jsonTeamPoints = new JSONArray();
        for (Integer teamPoint : teamPoints) {
            jsonTeamPoints.add(teamPoint);
        }
        //jsonAttrs.put("teamPoints", jsonTeamPoints);

        jsonMatch.put("Match", jsonAttrs);
        return jsonMatch;
    }

}
