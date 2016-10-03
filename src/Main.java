import py4j.GatewayServer;

public class Main {


    public static void main(String[] args) {
        GatewayServer gatewayServer = new GatewayServer(new ASTParser());
        gatewayServer.start();
        System.out.println("Gateway Server Started");
    }

//    public static void main(String[] args) {
//        ASTParser parser = new ASTParser();
//
//        ASTNode ast = parser.parseFile("java_files/Arguments.java");
//        System.out.println(ast);
//        List<String> l = parser.getAllAvailableTokens();
//        l.forEach(System.out::println);
//
//    }
}
