package cz.tomet123.server;

public class Start {


    public static Server server;

    public static void main(String[] args) {
        Info.printVersionLines();

        server = new Server();

    }

}
