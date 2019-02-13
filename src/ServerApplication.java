import server.DataPersistence;
import server.Server;
import server.ServerInterface;
import server.ServerWindow;

public class ServerApplication {
    private static final DataPersistence dataPersistence = DataPersistence.getDataPersistenceINSTANCE();

    public static void main(String[] args) {
        launchGUI(initialise());
        dataPersistence.saveConfiguration();
    }

    private static ServerInterface initialise(){
        return Server.getServerINSTANCE();
    }

    private static void launchGUI(ServerInterface serverInterface){
        ServerWindow serverWindow = new ServerWindow(serverInterface);
    }
}
