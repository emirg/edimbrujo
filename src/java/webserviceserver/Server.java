package webserviceserver;

import gamelogic.State;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

//Service Endpoint Interface
@WebService
@SOAPBinding(style = Style.DOCUMENT)
public interface Server {

    //Este metodo devuelve un hash que va a representar a su jugador.
    @WebMethod
    String init(String roll, String nombre);

    //Este ataque es para los personajes de rango melee.
    @WebMethod
    State atack(int direccion, String hash);

    //Este ataque es para los personajes de rango no melee.
    @WebMethod
    State rangeAtack(int direccion, String hash);

    //Movimiento.
    @WebMethod
    State move(int direccion, String hash);

    //Esta accion es solo para el curandero/clerigo.
    @WebMethod
    State heal(String hashJugadorObjectivo, String hash);

}
