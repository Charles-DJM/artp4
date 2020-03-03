/* echo / serveur basique
   Master Informatique 2012 -- Université Aix-Marseille
   Bilel Derbel, Emmanuel Godard
*/

import java.net.*;
import java.util.concurrent.*;
import java.io.*;

class EchoServerDPool {

  /* Démarrage et délégation des connexions entrantes */
   void demarrer(int port, Executor executor) {
    ServerSocket ssocket; // socket d'écoute utilisée par le serveur

    System.out.println("Lancement du serveur sur le port " + port);
    try {
      ssocket = new ServerSocket(port);
      ssocket.setReuseAddress(true); /* rend le port réutilisable rapidement */

      FileExport fileExport = new FileExport();

      while (true) {
        executor.execute(new Handler(ssocket.accept(), fileExport));
      }
    } catch (IOException ex) {
      ex.printStackTrace();
      System.out.println("Arrêt anormal du serveur.");
      return;
    }
  }

  public static void main(String[] args) {
    int argc = args.length;
    EchoServerDPool serveur;
    Executor executor;

    /* Traitement des arguments */
    if (argc == 1) {
     // executor = Executors.newWorkStealingPool();
      executor = Executors.newCachedThreadPool();
      try {
        serveur = new EchoServerDPool();
        serveur.demarrer(Integer.parseInt(args[0]), executor);
      } catch (Exception e) {
        e.printStackTrace();
      }

    }else if(argc == 2){
      executor = Executors.newFixedThreadPool(Integer.parseInt(args[0]));
      try {
        serveur = new EchoServerDPool();
        serveur.demarrer(Integer.parseInt(args[1]), executor);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("Usage: java EchoServer [-t nb] port");
    }
    return;
  }

  /*
     echo des messages reçus (le tout via la socket).
     NB classe Runnable : le code exécuté est défini dans la
     méthode run().
  */
  class Handler implements Runnable {

    Socket socket;
    PrintWriter out;
    BufferedReader in;
    InetAddress hote;
    int port;
    long responseTime;
    FileExport fileExport;

    Handler(Socket socket, FileExport fileExport) throws IOException {
      this.socket = socket;
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      hote = socket.getInetAddress();
      port = socket.getPort();
      this.fileExport = fileExport;
    }

    public void run() {
      String tampon;
      long compteur = 0;

      try {
        /* envoi du message d'accueil */
        out.println("Bonjour " + hote + "! (vous utilisez le port " + port + ")");

        do {
          /* Faire echo et logguer */
          tampon = in.readLine();
          responseTime =  System.nanoTime();
          if (tampon != null) {
            compteur++;
            /* log */
            System.err.println("[" + hote + ":" + port + "]: " + compteur + ":" + tampon);
            /* echo vers le client */
            out.println("> " + tampon);

            responseTime = System.nanoTime() - responseTime;
            fileExport.write(responseTime);
          } else {
            break;
          }
        } while (true);

        /* le correspondant a quitté */
        in.close();
        out.println("Au revoir...");
        out.close();
        socket.close();

        System.err.println("[" + hote + ":" + port + "]: Terminé...");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  class FileExport {

    File file;

    FileExport() {
      try {
        file = new File("serverResponseTime.csv");
        file.delete();
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    void write(long time) {
      try {
        FileWriter writer = new FileWriter(file, true);

        String string;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(time);
        stringBuilder.append('\n');
        string = stringBuilder.toString();
        writer.write(string);
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }


    }

  }
}
