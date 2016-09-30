import py4j.GatewayServer;

import java.io.IOException;

public class Main {


//    public static void main(String[] args) {
//        GatewayServer gatewayServer = new GatewayServer(new ASTParser());
//        gatewayServer.start();
//        System.out.println("Gateway Server Started");
//    }

    public static void main(String[] args) {
        ASTParser parser = new ASTParser();
        try {
            ASTParser.ASTNode ast = parser.parseFile("java_files/ActionId.java");
            System.out.println(ast);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
