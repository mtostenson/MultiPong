import java.io.Serializable;

@SuppressWarnings("serial")
public class Values implements Serializable {

   // Values
   public int score1;
   public int score2;
   public int ball_x;
   public int ball_y;
   public int client1_y;
   public int client2_y;

   public Values(int pX, int pY, int p1, int p2, int s1, int s2) {
      ball_x = pX;
      ball_y = pY;
      client1_y = p1;
      client2_y = p2;
      score1 = s1;
      score2 = s2;
   }

}