package common;

import java.io.*;
import java.net.Socket;
import java.util.List;

@SuppressWarnings("unchecked")
public class ClientComms {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String localhost = "127.0.0.1";
    private Integer port = 50123;

    public ClientComms() {

        try {
            socket = new Socket(localhost, port); // Creating the socket that connects to the server
            objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a message to the server.
     * @param message String message
     */
    public void sendMessageToServer(Object message) {

        try {
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Recieve a message from the server after the send request.
     * @return the received message object.
     */
    public Object receiveMessage(){
        Object o = new Object();
        try {
            objectInputStream=new ObjectInputStream(socket.getInputStream());
            o = objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return o;
    }

    // Getters that send string messages to the server and return the relevant objects.

    public List<Postcode> getObjectPostcodes(){
        this.sendMessageToServer("GET POSTCODES");
        return (List<Postcode>) this.receiveMessage();
    }

    public List<User> getUserObjects(){
        this.sendMessageToServer("GET USERS");
        return (List<User>) this.receiveMessage();
    }

    public List<Order> getOrderObjects(){
        this.sendMessageToServer("GET ORDERS");
        return (List<Order>) this.receiveMessage();
    }

    public List<Dish> getDishObjects(){
        this.sendMessageToServer("GET DISHES");
        return (List<Dish>) this.receiveMessage();
    }

    public User registerUser(String userDetails){
        this.sendMessageToServer(userDetails);
        return (User) this.receiveMessage();
    }

    public List<Order> getUserOrder(User user){
        this.sendMessageToServer("GET ORDERS:"+user.getName());
        return (List<Order>) this.receiveMessage();
    }

    public Order addOrder(String userOrder){
        this.sendMessageToServer(userOrder);
        return (Order) this.receiveMessage();
    }

    public void updateOrderStatus(String orderName, String orderDetails){
        this.sendMessageToServer( String.format("UPDATE ORDER STATUS:%s:%s:Cancelled",orderName,orderDetails));
    }
}
