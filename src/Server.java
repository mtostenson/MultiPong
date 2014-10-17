import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

   // Server Fields ------------------------------------------------------------
   private ServerSocket server_socket = null;
   private ObjectOutputStream output1 = null;
   private ObjectOutputStream output2 = null;
   private ObjectInputStream input1 = null;
   private ObjectInputStream input2 = null;
   private Socket client1 = null;
   private Socket client2 = null;
   private final int port = 7100;

   // All values ---------------------------------------------------------------
   private final int STEP = 2;
   private boolean up1 = false;
   private boolean up2 = false;
   private int ball_x = 100;
   private int ball_y = 100;
   private int ball_step_x = 2;
   private int ball_step_y = 0;
   private int client1_y = 50;
   private int client2_y = 350;
   private int score1;
   private int score2;
   private Values current_values = null;

   // Construction -------------------------------------------------------------
   public Server() {

      try {
         System.out.println("Waiting for clients...");

         // Connect to client 1
         server_socket = new ServerSocket(port);
         client1 = server_socket.accept();
         output1 = new ObjectOutputStream(client1.getOutputStream());
         input1 = new ObjectInputStream(client1.getInputStream());
         System.out.println("Connected to client 1");

         // Connect to client 2
         client2 = server_socket.accept();
         output2 = new ObjectOutputStream(client2.getOutputStream());
         input2 = new ObjectInputStream(client2.getInputStream());
         System.out.println("Connected to client 2");

         startGame();
      } catch (IOException ioe) {
         System.err.println("Server exception: " + ioe);
      }
   }

   // Main loop ----------------------------------------------------------------
   private void startGame() {

      listen();
      while (true) {
         sendValues();
         updateValues();
         try {
            Thread.sleep(10);
         } catch (Exception e) {
            System.err.println("Sleep exception: " + e);
         }
      }

   }

   // Send current data to each client -----------------------------------------
   public void sendValues() {

      current_values = new Values(ball_x, ball_y, client1_y, client2_y, score1,
         score2);
      try {
         output1.writeObject(current_values);
         output1.flush();
         output2.writeObject(current_values);
         output2.flush();
      } catch (IOException ioe) {
         System.err.println("Server exceptoin: " + ioe);
      }

   }

   public void updateValues() {

      if (up1)
         client1_y -= STEP;
      else
         client1_y += STEP;
      if (up2)
         client2_y -= STEP;
      else
         client2_y += STEP;

      if (ball_x < 0) {
         score2++;
         ball_x = 650;
      }
      if (ball_x > 690) {
         score1++;
         ball_x = 50;
      }

      if (ball_y < 0 || ball_y > 410)
         ball_step_y = -ball_step_y;

      if (client1_y < 0)
         up1 = false;

      if (client2_y < 0)
         up2 = false;

      if (client1_y > 375)
         up1 = true;

      if (client2_y > 375)
         up2 = true;

      if (ball_x <= 25 && ball_x > 20 && ball_y > client1_y - 5
         && ball_y < client1_y + 45) {
         ball_step_x = -ball_step_x;
         if (ball_y <= client1_y + 20 && ball_step_y > -1)
            ball_step_y--;
         if (ball_y > client1_y + 20 && ball_step_y < 1)
            ball_step_y++;
      }

      if (ball_x >= 660 && ball_x < 665 && ball_y > client2_y - 5
         && ball_y < client2_y + 45) {
         ball_step_x = -ball_step_x;
         if (ball_y <= client1_y + 20 && ball_step_y > -1)
            ball_step_y--;
         if (ball_y > client1_y + 20 && ball_step_y < 1)
            ball_step_y++;
      }

      ball_x += ball_step_x;
      ball_y += ball_step_y;

   }

   public void listen() {

      Thread listener1 = new Thread() {
         public void run() {
            while (true) {
               try {
                  up1 = input1.readBoolean();
               } catch (IOException pException) {
                  pException.printStackTrace();
               }
            }
         }
      };
      listener1.start();

      Thread listener2 = new Thread() {
         public void run() {
            while (true) {
               try {
                  up2 = input2.readBoolean();
               } catch (IOException pException) {
                  pException.printStackTrace();
               }
            }
         }
      };
      listener2.start();

   }

   // Entry point --------------------------------------------------------------
   public static void main(String[] args) {

      @SuppressWarnings("unused")
      Server server = new Server();

   }
}
