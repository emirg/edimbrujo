/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserviceserver;

import javax.xml.ws.Endpoint;

/**
 *
 * @author joan
 */
public class Publisher {

    public static void main(String[] arg) {
        Endpoint.publish("http://webServerEdimbrujo/ws/ServerCentral", new ServerImp());
    }
}
