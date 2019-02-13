package common;

import server.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerComms {
    private ServerSocket serverSocket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String localhost = "127.0.0.1";
    private Integer port = 50123;

    public ServerComms() {

        try {
            serverSocket = new ServerSocket(port); // Set the server

            // Create a new thread that is going to listen for clients that want to connect.
            new Thread(() -> {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept(); //Listen and connect the client.
                        objectInputStream = new ObjectInputStream(clientSocket.getInputStream());

                        // A new thread that is going to listen for the messages that the clients send
                        new Thread(() -> {
                            while(true){
                                receiveMessageFromClient(clientSocket);
                            }
                        }).start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends an object message.
     * @param message object to be sent
     * @param clientSocket the socket that should receive the message
     */
    public void sendMessageToClient(Object message, Socket clientSocket) {

        try {
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the received message.
     * @param clientSocket the socket that sent the message
     */
    public void receiveMessageFromClient(Socket clientSocket) {
        String message = "";

        try {
            message = (String) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // e.printStackTrace();

        }

        if (message.equals("GET POSTCODES")) {
            this.sendMessageToClient(Server.getServerINSTANCE().getPostcodes(), clientSocket);
        }

        if (message.equals("GET USERS")) {
            this.sendMessageToClient(Server.getServerINSTANCE().getUsers(), clientSocket);
        }

        if (message.equals("GET ORDERS")) {
            this.sendMessageToClient(Server.getServerINSTANCE().getOrders(), clientSocket);
        }

        if (message.equals("GET DISHES")) {
            this.sendMessageToClient(Server.getServerINSTANCE().getDishes(), clientSocket);
        }

        if (message.startsWith("REGISTER USER")) {
            String[] userDetails = message.split(":");
            Server.getServerINSTANCE().addPostcode(userDetails[4], Integer.parseInt(userDetails[5]));
            Server.getServerINSTANCE().addUser(userDetails[1], userDetails[2], userDetails[3], Server.getServerINSTANCE().getPostcodeByCode(userDetails[4]));
            this.sendMessageToClient(Server.getServerINSTANCE().getUserByName(userDetails[1]), clientSocket);
        }

        if(message.startsWith("GET ORDERS:")){
            String[] userOrder=message.split(":");
            User user = Server.getServerINSTANCE().getUserByName(userOrder[1]);
            this.sendMessageToClient(Server.getServerINSTANCE().getUserOrder(user),clientSocket);
        }

        if(message.startsWith("ADD ORDER")){
            message=message.replaceAll("\\{","");
            message=message.replaceAll("}","");
            String[] orderDetails = message.split(":");
            User user = Server.getServerINSTANCE().getUserByName(orderDetails[1]);
            String[] basketDetails=orderDetails[2].split(", ");
            Map<Dish, Number> basket = new ConcurrentHashMap<>();
            for(String element: basketDetails){
                String[] dishAndNum=element.split("=");
                basket.put(StockManagement.getINSTANCE().getDishByName(dishAndNum[0]),Integer.parseInt(dishAndNum[1]));
            }
            Server.getServerINSTANCE().addOrder(user.getName(),basket);
            Server.getServerINSTANCE().getOrders().get(Server.getServerINSTANCE().getOrders().size()-1).setCost(Integer.parseInt(orderDetails[3]));
            this.sendMessageToClient(Server.getServerINSTANCE().getOrders().get(Server.getServerINSTANCE().getOrders().size()-1),clientSocket);
        }

        if(message.startsWith("UPDATE ORDER STATUS:")){
            String[] orderDetails=message.split(":");
            Order order=Server.getServerINSTANCE().getOrderByName(orderDetails[1],orderDetails[2]);
            order.setStatus(orderDetails[3]);
        }

    }
}
