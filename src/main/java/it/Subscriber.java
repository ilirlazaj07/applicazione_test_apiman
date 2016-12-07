/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it;

import java.io.IOException;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class Subscriber implements MqttCallback {

    private String brokerUrl;
    private int qos;

    private MqttClient client;
    private MqttConnectOptions conOpt;

    public Subscriber(String server, int port, String clientId, int qos) {
        this.brokerUrl = "tcp://" + server + ":" + port;
        this.qos = qos;
        String user = "sandbox";
        String password = "sandbox$1";

        try {
            // Construct the object that contains connection parameters
            // such as cleansession and LWAT
            conOpt = new MqttConnectOptions();
            conOpt.setCleanSession(false);
            conOpt.setUserName(user);
            conOpt.setPassword(password.toCharArray());
            // Construct the MqttClient instance
            client = new MqttClient(this.brokerUrl, clientId);

            // Set this wrapper as the callback handler
            client.setCallback(this);
        } catch (MqttException e) {
            e.printStackTrace();
            System.err.println("Unable to set up client: " + e.toString());
            System.exit(1);
        }
    }

    public void subscribe(String topicName) throws MqttException {

        client.connect(conOpt);

       client.subscribe(topicName);
       
        System.out.println("Connected to " + brokerUrl + " with client ID " + client.getClientId());

        // Subscribe to the topic
        System.out.println("Subscribing to topic \"" + topicName + "\" qos " + qos);

        // Block until Enter is pressed
        System.out.println("Press <Enter> to exit");
        try {
            System.in.read();
        } catch (IOException e) {
            //If we can't read we'll just exit
        }
        client.disconnect();
        System.out.println("Disconnected");
    }

    public static void main(String[] args) {
        String server = "localhost";
        int port = 1883;
        String clientId = "JavaSample123";
        String topic = "input/sandbox";
        int qos = 2;
       

        Subscriber sub = new Subscriber(server, port, clientId, 2);
        try {
            sub.subscribe(topic);
        } catch (MqttException e) {
            System.err.println("Exception when subscribing0");
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void messageArrived(String string, MqttMessage mm) throws Exception {

        System.out.println("Arrivato: " + mm.getPayload());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
        System.out.println("Arrivato");
    }

}
