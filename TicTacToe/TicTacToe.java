import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;

public class TicTacToe extends JComponentWithEvents {
//------------------------------------------------
  private final int N = 3;
  private int[][] field = new int[N][N];
  private int height = getHeight();
  private int width = getWidth();
  private Color grey = new Color(225,225,225);
  private int spaceh;
  private int spacew;
  private int radiush;
  private int radiusw;
  private int mouseX = -1, mouseY = -1, dotX, dotY;
  private int mouseOverX, mouseOverY, mousePressedX, mousePressedY;
  private boolean go = true;
  
//------------------------------------------------
  
  public static void main (String args[]) {launch(300, 300);}
  
  public void timerFired() {
    height = getHeight();
    width = getWidth();
    spaceh = height/30;
    spacew = width/30;
    radiush = 4*spaceh;
    radiusw = 4*spacew;
    mousePressedY = -1;
    mousePressedX = -1;
    setTimerDelay(10); //0.01 s delay
  }
  
  public void start() { init();}
  public void init() {
    for(int i = 0; i<3; i++) for(int j = 0; j<3; j++) field[i][j] = 0;
    go = true;
  }
  
  public void mouseMoved(int x, int y) {
    mouseX = x;
    mouseY = y;
    for(int i=0; i<N; i++){
      if(mouseX > width/3 * i && mouseX < width/3 * (i+1)) mouseOverX = i;
      if(mouseY > height/3 * i && mouseY < height/3 * (i+1)) mouseOverY = i;
    }
  }
  
  public void mousePressed(int x, int y) {
    mouseX = x;
    mouseY = y;
    dotX = x;
    dotY = y;
    for(int i=0; i<N; i++){
      if(mouseX > width/3 * i && mouseX < width/3 * (i+1)) mousePressedX = i;
      if(mouseY > height/3 * i && mouseY < height/3 * (i+1)) mousePressedY = i;
    }
    gameSys();
  }
  
  public void keyPressed(char key) {
    if (key == 'q'){beep(); exit();}
    if (key == 'r'){beep(); init();}
  }
  public void paint(Graphics2D page){
    
    paintBg(page);
    paintLines(page);
    paintTokens(page);
    paintWin(page);
  }
  
  public void paintBg(Graphics2D page){
    page.setColor(grey);
    page.fillRect(0,0,width,height);
  }
  
  public void paintLines(Graphics2D page){
    page.setColor(Color.black);
    setLineThickness(page, 3);
    for(int i = 1; i < N; i++){
      page.drawLine(width/3 * i, spaceh, width/3 * i, height-spaceh);
      page.drawLine(spacew, height/3 * i, width - spacew, height/3 * i);
    }
  }
  public void paintTokens(Graphics2D page){
    page.setColor(Color.black);
    for(int i = 0; i<3; i++){
      for(int j = 0; j<3; j++){
        if(field[i][j] == 1){
          setLineThickness(page, 3);
          page.drawLine(spacew + width/3 * j, spaceh + height/3 * i, width/3-spacew + width/3 * j, height/3-spaceh + height/3 * i);
          setLineThickness(page, 3);
          page.drawLine(width/3 * (j+1) - spacew, height/3 * i + spaceh, spacew + width/3 * j, height/3 * (i+1) - spaceh);
        }
        if(field[i][j] == 2)
          page.drawOval(spacew + width/3 * j, spaceh + height/3 * i, 2*radiusw, 2*radiush);
      }
    }
  }
  public void paintWin(Graphics2D page){
    if(winCheck(1)){
      page.setColor(Color.red);
      page.setFont(new Font("Serif", Font.BOLD, height/10));
      page.drawString("Vince il giocatore X",2*spacew, 2*height/3 - spaceh);
    }
    if(winCheck(2)){
      page.setColor(Color.red);
      page.setFont(new Font("Serif", Font.BOLD, height/10));
      page.drawString("Vince il giocatore O",2*spacew, 2*height/3 - spaceh);
    }    
  }
    
  public void gameSys(){
    if(field[mousePressedY][mousePressedX] == 0){
      if(go) field[mousePressedY][mousePressedX] = 1;
      else field[mousePressedY][mousePressedX] = 2;
      go = !go;
    }
    
  }
  
  public boolean winCheck(int who){
    
    for(int i = 0; i<N; i++)
      if(field[i][0] == field[i][1] && field[i][0] == field[i][2] && field[i][0] == who) return true;
    
    for(int j = 0; j<N; j++)
      if(field[0][j] == field[1][j] && field[0][j] == field[2][j] && field[0][j] == who) return true;
    
      if(field[0][0] == field[1][1] && field[1][1] == field[2][2] && field[0][0] == who) return true;
      if(field[0][2] == field[1][1] && field[1][1] == field[2][0] && field[2][0] == who) return true;
      return false;
  }

  public void printTab(){
        for(int i = 0; i<3; i++){
      for(int j = 0; j<3; j++){
        System.out.print(field[i][j] + "\t");
      }
      System.out.println();
    }
  }
  
}