package gamelogic;

import engine.Action;
import engine.State;
import engine.StaticState;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;
import org.dyn4j.collision.Fixture; // Clase que representa una figura colisionable
import org.dyn4j.dynamics.Body; // Clase que representa un cuerpo
import org.dyn4j.dynamics.BodyFixture; // Clase que representa la figura de un cuerpo
import org.dyn4j.geometry.Vector2;
import org.json.simple.JSONObject;

public class Entity extends State {

    protected double x; // Centro de la entidad
    protected double y; // Centro de la entidad
    protected double width; // Ancho (Necesario para la detección de colisiones - Deberia ser del mismo tamaño que el sprite del cliente visual)
    protected double height; // Alto
    protected Vector2 velocidad; // Todavia no hago uso de esto, capaz sirve para steering behaviour

    public Entity(String name, boolean destroy, String id, double x, double y, double velocidadX, double velocidadY, double width, double height) {
        super(name, destroy, id == null ? UUID.randomUUID().toString() : id);
        this.x = x;
        this.y = y;
        velocidad = new Vector2(velocidadX, velocidadY);
    }

    public double getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        return null;
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, LinkedList<Action>> actions) {
        hasChanged = false;
        Entity newEntity = new Entity(name, destroy, id, x, y, velocidad.x, velocidad.y, 0, 0);
        return newEntity;
    }

    @Override
    public void setState(State newEntity) {
        super.setState(newEntity);
        x = ((Entity) newEntity).x;
        y = ((Entity) newEntity).y;
    }

    @Override
    protected Object clone() {
        Entity clon = new Entity(name, destroy, id, x, y, velocidad.x, velocidad.y, 0, 0);
        return clon;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonEntity = new JSONObject();
        JSONObject jsonAttrs = new JSONObject();
        jsonAttrs.put("super", super.toJSON());
        jsonAttrs.put("x", x);
        jsonAttrs.put("y", y);
        jsonEntity.put("Entity", jsonAttrs);
        return jsonEntity;
    }

}
