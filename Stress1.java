import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Stress1 {

    public static void main(String[] args) throws IOException {
        Socket s;
        String ip;
        int port;
        int n;
        PrintWriter out;

        /* Traitement des arguments */
        if (args.length != 3) {
            /* erreur de syntaxe */
            System.out.println("Usage: java Stress1 @server @port @n");
            System.exit(1);
        }
        ip = args[0];
        port = Integer.parseInt(args[1]);
        n = Integer.parseInt(args[2]);

        if (port > 65535) {
            System.err.println("Port hors limite");
            System.exit(3);
        }

        for(int i = 0; i < n ; i++){
            try{
               s =  new Socket(ip, port);
               out = new PrintWriter(s.getOutputStream(), true);
               out.println("client stress1 n = " + i);
               s.shutdownOutput();
               s.close();
            }catch (Exception e){}
        }
    }

}
