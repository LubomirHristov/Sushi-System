import client.ClientInterface;
import client.ClientWindow;
import client.Client;

public class ClientApplication {
    private static ClientInterface initialise(){
        return new Client();
    }

    private static void launchGUI(ClientInterface clientInterface){
        ClientWindow clientWindow= new ClientWindow(clientInterface);
    }
    public static void main(String[] args) {
        launchGUI(initialise());
    }
}
