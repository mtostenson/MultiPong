import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Client extends JFrame implements KeyListener {

   // Nested class DrawPanel ---------------------------------------------------
   private class DrawPanel extends JPanel {

      public DrawPanel() {
         super();
      }

      @Override
      protected void paintComponent(Graphics g) {

         g.setColor(Color.white);

         // Show score
         g.drawString("[Player 1: " + current_values.score1 + "] [Player 2: "
            + current_values.score2 + "]", 300, 10);

         // Paddle 1
         g.fillRect(20, current_values.client1_y, 5, 50);

         // Ball
         g.fillOval(current_values.ball_x, current_values.ball_y, 10, 10);

         // Paddle 2
         g.fillRect(670, current_values.client2_y, 5, 50);
      }
   }

   // GUI Elements -------------------------------------------------------------
   private DrawPanel panel = null;

   // Connectivity
   private final String hostname = "10.157.101.15";
   private final int port = 7100;
   private Socket socket = null;
   private ObjectOutputStream output = null;
   private ObjectInputStream input = null;

   // Values -------------------------------------------------------------------
   private Values current_values;

   // Construction -------------------------------------------------------------
   public Client(String title) {

      super(title);

      addKeyListener(this);

      // Connect to server
      try {
         socket = new Socket(InetAddress.getByName(hostname), port);
         output = new ObjectOutputStream(socket.getOutputStream());
         input = new ObjectInputStream(socket.getInputStream());
         current_values = (Values) input.readObject();
      } catch (Exception e) {
         System.err.println("Client exception: " + e);
      }

      // Build GUI
      panel = new DrawPanel();
      panel.addKeyListener(this);
      add(panel, BorderLayout.CENTER);
      setSize(700, 450);
      setBackground(Color.black);
      setResizable(false);
      setVisible(true);
      startGame();

   }

   // Main loop ----------------------------------------------------------------
   public void startGame() {
      while (true) {
         try {
            current_values = (Values) input.readObject();
            repaint();
         } catch (Exception e) {
            System.err.println("Client error: " + e);
         }
      }
   }

   // Entry point --------------------------------------------------------------
   @SuppressWarnings("unused")
   public static void main(String[] args) {
      Client client = new Client("Client 1");
   }

   // Key event handlers -------------------------------------------------------
   @Override
   public void keyPressed(KeyEvent pE) {
      if (pE.getKeyCode() == KeyEvent.VK_UP) {
         try {
            output.writeBoolean(true);
            output.flush();
         } catch (IOException pException) {
            pException.printStackTrace();
         }
      }
      if (pE.getKeyCode() == KeyEvent.VK_DOWN) {
         try {
            output.writeBoolean(false);
            output.flush();
         } catch (IOException pException) {
            pException.printStackTrace();
         }
      }
   }

   @Override
   public void keyTyped(KeyEvent pE) {
   }

   @Override
   public void keyReleased(KeyEvent pE) {
   }
}
