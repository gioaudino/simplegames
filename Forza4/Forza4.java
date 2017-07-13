import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;


public class Forza4 extends JComponentWithEvents {

//---------------DEFINE---VARIABLES---------------
//------------------------------------------------
  static final int N = 6;
  static final int M = 7;
  static final char UNO = 'R';
  static final char DUE = 'Y';
  static final int MARGINE = 3;
  private int radiush;
  private int radiusw;
  private int width = getWidth();
  private int realheight = getHeight();
  private int height = 6/7*realheight;
  private int spacew;
  private int spaceh;
  private static char tab[][] = new char[N][M];
  private int mouseOver;
  private int mousePressed = -1;
  private boolean go = false;
  private int play = 0, r = -1;
  private Color grey = new Color(198,198,199);
//---------EVENT---HANDLERS---VARIABLES-----------
  private int mouseX = -1, mouseY = -1, dotX, dotY;
//--------DEFAULT---STRING---MESSAGES-------------
  String plUNO = "Tocca al giocatore UNO!";
  String plDUE = "Tocca al giocatore DUE!";
  String winUNO = "Complimenti!! Vince il giocatore UNO!";
  String winDUE = "Complimenti!! Vince il giocatore DUE!";
  String nowin = "Partita patta!";
  String def = "Premere con il tasto sinistro del mouse";
  String defb = "la colonna in cui inserire il gettone;";
  String defc = "Vince il giocatore che per primo";
  String defd = "inserisce quattro gettoni in fila";
  String end = "Premi q per chiudere il programma, r per ricominciare";
//------------------------------------------------  
  public static void main (String args[]) {launch(700, 700);}
  
  public void timerFired() {
    height = getHeight() - getHeight()/7;
    width = getWidth();
    radiush = height/15;
    radiusw = 2* width/35;
    spaceh = height/60;
    spacew = width/70;
    setTimerDelay(10); //0.01 s delay
  }
  
  public void start() { init();}
  public void init() {
    
    for (int i = 0; i < N; i++) for (int j = 0; j < M; j++) tab[i][j] = '.' ; //field init
    go = false;
    mousePressed = -1;
    
  }
  
  public void paint(Graphics2D page){

    printBg(page);
    game();
   
  }
  public void game(){
        if(r!=-1){
          if(go) tab[r][mousePressed] = UNO;
          else tab[r][mousePressed] = DUE;
        }
    }
  public void keyPressed(char key) {
   
  if (key == 'q'){beep(); exit();}
  if (key == 'r'){beep(); init();}
  }

  public void mouseMoved(int x, int y) {
    mouseX = x;
    mouseY = y;
    for(int i=0; i<M; i++)
      if(mouseX > width/7 * i && mouseX < width/7 * (i+1)) mouseOver = i;
  }
  
  public void mousePressed(int x, int y) {
    mouseX = x;
    mouseY = y;
    dotX = x;
    dotY = y;
    for(int i=0; i<M; i++)
      if(mouseX > width/7 * i && mouseX < width/7 * (i+1)) mousePressed = i;
    r = free(mousePressed, N-1);
 if (r!=-1){ go = !go; play++;}
  }

/* 
 * PRINT METHODS
 */
  public void printField(Graphics2D page){ //PRINTS A GREY BACKGROUND
    page.setColor(grey);
    page.fillRect(0,0,width,height);
  }
  
  public void printLines(Graphics2D page){ //PRINTS A BLACK GRID
    for (int i=0; i<M+1; i++){
      setLineThickness(page, 4);
      page.setColor(Color.black);
      page.drawLine(width/7*i, 0, width/7*i, height); 
      if(i==M) continue;
      page.drawLine(0, height/6*i, width, height/6*i);
    }
  }
    
  public void printTokens(Graphics2D page){ //PRINTS TOKENS (RED-BLUE) ACCORDING TO THE tab REFERENCE
    for(int i = 0; i < N; i++){
      for(int j = 0; j < M; j++){
        if(tab[i][j] == UNO){
          page.setColor(Color.red);
          page.fillOval(spacew + width/7*j, spaceh + height/6*i, 2* radiusw, 2*radiush);
          page.setColor(Color.black);
          setLineThickness(page, 2);
          page.drawOval(spacew + width/7*j, spaceh + height/6*i, 2*radiusw, 2*radiush);}
        
        if(tab[i][j] == DUE){
          page.setColor(Color.blue);
          page.fillOval(spacew + width/7*j, spaceh + height/6*i, 2*radiusw, 2*radiush);
          page.setColor(Color.black);
          setLineThickness(page, 2);
          page.drawOval(spacew + width/7*j, spaceh + height/6*i, 2*radiusw, 2*radiush);}
      }
    }
  }
  public void printBg(Graphics2D page){ //CALLS ALL THE PRINT METHODS AND PRINTS A YELLOW COLUMN ACCORDING TO mouseOver
    
    printField(page);
    Alpha: {
      if(mouseX==-1 || mouseX < 0 || mouseX > width) break Alpha;
      page.setColor(Color.yellow);
      page.fillRect(width/7*mouseOver, 0, width/7, height);
    }
      printLines(page);
      printTokens(page);
      msgBoard(page);
   }
  public void msgBoard(Graphics2D page){ //HANDLES THE MESSAGEBOARD
   page.setColor(Color.green);
   page.fillRect(0, height, width, 100);
   if(play == 0){ page.setFont(new Font("Serif", Font.BOLD, 16));
                  page.setColor(Color.black);
                  page.drawString(def, width/2, height+height/30);
                  page.drawString(defb, width/2, height+height/15);
                  page.drawString(defc, width/2, height+height/10);
                  page.drawString(defd, width/2, height+height*2/15);}
    endOfGame: {
      if(win(UNO)) {   page.setFont(new Font("Serif", Font.BOLD, 32)); page.setColor(Color.red);
                       page.drawString(winUNO, width/14, height + height/12);
                       page.setColor(Color.black); page.setFont(new Font("Serif", Font.BOLD, 16));
                       page.drawString(end, width/4, realheight-20);  break endOfGame;}
      if(win(DUE)) {   page.setFont(new Font("Serif", Font.BOLD, 32)); page.setColor(Color.blue);
                       page.drawString(winDUE, width/14, height + height/12);
                       page.setColor(Color.black); page.setFont(new Font("Serif", Font.BOLD, 16));
                       page.drawString(end, width/4, realheight-20); break endOfGame;}
      if(play == M*N){ page.setFont(new Font("Serif", Font.BOLD, 24)); page.setColor(Color.black);
                       page.drawString(nowin, width/14, height + height/12);
                       page.setColor(Color.black); page.setFont(new Font("Serif", Font.BOLD, 16));
                       page.drawString(end, width/4, realheight-20); break endOfGame;}
      if(!go) { page.setFont(new Font("Serif", Font.BOLD, 24)); page.setColor(Color.red); page.drawString(plUNO, width/14, height+height/12); break endOfGame;}
      if(go) { page.setFont(new Font("Serif", Font.BOLD, 24)); page.setColor(Color.blue); page.drawString(plDUE, width/14, height+height/12); break endOfGame;}
    }
   }

/*
 *  END OF PRINT METHODS 
 */
  static int whose (int i, int j){ //RETURNS NUMBER OF THE PLAYER OWNER OF THE TOKEN IN A SPECIFIC POSITION
  if(tab[i][j] == UNO) return 1;
  if(tab[i][j] == DUE) return 2;
  return -1;
  }
  
  static int free (int col, int r){ //Returns the number of the first free row, -1 if the column is full
    if (col == -1) return -1;
    if (r == 0 && tab[r][col] != '.') return -1;
    if (tab[r][col] == '.') return r;
    else return free(col, r-1);
  }
  
  static boolean win(char P){ //Returns TRUE if the player received as argument has won, FALSE elsewhere
    int i, j;
    //HORIZONTAL WIN CHECK
    for (i = N-1; i >= 0; i--){
      for (j = 0; j < M-MARGINE; j++){
        if (tab[i][j] != P) continue;  //If place is other player's or empty, ignores the rest of the instruction of the loop
        if (tab[i][j] == tab[i][j+1] && tab[i][j+1] == tab[i][j+2] && tab[i][j+2] == tab[i][j+3]) return true;
      }
    }
    //VERTICAL WIN CHECK
    for (i = N-1; i >= N-MARGINE; i--){
      for (j = 0; j < M; j++){
        if (tab[i][j] != P) continue;  //If place is other player's or empty, ignores the rest of the instruction of the loop
        if (tab[i][j] == tab[i-1][j] && tab[i-1][j] == tab[i-2][j] && tab[i-2][j] == tab[i-3][j]) return true;
      }
    }
    
    //DIAGONAL WIN CHECK (FROM BOTTOM LEFT)
    for (i = N-1; i >= N-MARGINE; i--){
      for (j = 0; j < M-MARGINE; j++){
        if (tab[i][j] != P) continue;  //If place is other player's or empty, ignores the rest of the instruction of the loop
        if (tab[i][j] == tab[i-1][j+1] && tab[i-1][j+1] == tab[i-2][j+2] && tab[i-2][j+2] == tab[i-3][j+3]) return true;
      }
    }
    //DIAGONAL WIN CHECK (FROM BOTTOM RIGHT)
    for (i = N-1; i >= N-MARGINE; i--){
      for (j = M-1; j > M-MARGINE; j--){
        if (tab[i][j] != P) continue;  //If place is other player's or empty, ignores the rest of the instruction of the loop
        if (tab[i][j] == tab[i-1][j-1] && tab[i-1][j-1] == tab[i-2][j-2] && tab[i-2][j-2] == tab[i-3][j-3]) return true;
      }
    }    
        
  return false;
  }
}