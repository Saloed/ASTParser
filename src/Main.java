import py4j.GatewayServer;

public class Main {


    public static void main(String[] args) {
        GatewayServer gatewayServer = new GatewayServer(new ASTParser());
        gatewayServer.start();
        System.out.println("Gateway Server Started");
    }


}
