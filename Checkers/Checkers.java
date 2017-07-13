import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;

/* CHECKERS GAME: a few notes
 * Field is red and black, red squares are legal, with white lines. Tokens are yellow, for first player playing, and green for
 * the other. Going on a legal square with the mouse (and one where you're actually allowed to do something)
 * makes that square get white, and it will keep being white in case you hit the mouse button. In that case player will
 * be asked to choose where to move, and computer will change the field according to the choice.
 * In the int 2D array representing the field, -1 means a black square, one on which you cannot do anything; 0 stands for
 * a free square, while 1 and 2 are for squares with a token from player #1 or #2. #3 and #4 stand for crowned tokens from
 * player 1 and 2. Everytime a player selects a spot, the int matrix gets filled with coordinates of spots where you can
 * move the token. If one of the coordinates is not valid (out of bounds), the coordinates are -1.
 *
 * Log:
 * 16/8-19.00: you can now move tokens, one player at a time.
 * 16/8-19.25: moves are now right, no more 'flying'
 * 17/8-10.00: killed tokens disappear from game field. White background still not working
 * 18/8-00.40: Game works. Crowned tokens get a blue circle in the middle, they can kill themselves and normal tokens, but cannot
 *             be killed by normal tokens. System actually doesn't know if someone wins or not.
 *
 */ 

public class Checkers extends JComponentWithEvents {
//-----------------DEFINE---VARIABLES-----------------  
  private final int N = 8;
  int[][] field = new int[N][N];
  private int height = getHeight();
  private int width = getWidth();
  private int spaceh;
  private int spacew;
  private int radiush;
  private int radiusw;
  private int miniradiush;
  private int miniradiusw;
  private int minispaceh;
  private int minispacew;
  private int mouseX = -1, mouseY = -1, dotX, dotY;
  private int mouseOverX, mouseOverY, mousePressedX, mousePressedY;
  private boolean player = true, wtd = true;
  private int[][] choice = new int[4][2];
  private int[] moving = new int[2];
  private Graphics2D page;
  Color lblue = new Color(54, 198, 244);
//----------------------------------------------------
  
  public static void main(String args[]) { launch(300, 300); }
  public void timerFired() {
    height = getHeight();
    width = getWidth();
    spaceh = height/80;
    spacew = width/80;
    radiush = height/20;
    radiusw = width/20;
    miniradiush = radiush/2;
    miniradiusw = radiusw/2;
    setTimerDelay(10); //0.01s delay
  }
  
  public void start() { init();}
  public void init() {
    boolean alpha = false;
    for(int i = 0; i < N; i++){ for(int j = 0; j < N; j++){ if(alpha) field[i][j] = 0; else field[i][j] = -1; alpha = !alpha;} alpha = !alpha; }
    for(int i = 0; i < 3; i++){ for(int j = 0; j < N; j++){ if(field[i][j]==0) field[i][j] = 1;} }
    for(int i = N-1; i >N-4; i--){ for(int j = 0; j < N; j++){ if(field[i][j]==0) field[i][j] = 2;} }
    player = true;
    wtd = true;
    for(int i = 0; i<4; i++){
            choice[i][0] = -1; choice[i][1] = -1;}

  }
  
  public void paint(Graphics2D page){
  printAll(page);
  }
/*
 * EVENT HANDLERS METHODS
 */
  public void mouseMoved(int x, int y) {
    mouseX = x;
    mouseY = y;
    for(int i=0; i<N; i++){
      if(mouseX > width/8 * i && mouseX < width/8 * (i+1)) mouseOverX = i;
      if(mouseY > height/8 * i && mouseY < height/8 * (i+1)) mouseOverY = i;
    }
  }
  
  public void mousePressed(int x, int y) {
    mouseX = x;
    mouseY = y;
    dotX = x;
    dotY = y;
    for(int i=0; i<N; i++){
      if(mouseX > width/8 * i && mouseX < width/8 * (i+1)) mousePressedX = i;
      if(mouseY > width/8 * i && mouseY < width/8 * (i+1)) mousePressedY = i;
    }
    go();
    printFromTab(page);
  }
  
  public void keyPressed(char key) {
    if (key == 'q'){beep(); exit();}
    if (key == 'r'){beep(); init();}
  }
  
/*
 * END OF EVENT HANDLERS METHODS
 */

/* 
 * PRINT METHODS
 */
  public void printField(Graphics2D page){
    boolean alpha = false;
    for(int i=0; i<N; i++){
      for(int j=0; j<N; j++){
        if(alpha){
          page.setColor(Color.red);
          page.fillRect(width/8 * i,height/8 * j, width/8, height/8);
        }
        else{
          page.setColor(Color.black);
          page.fillRect(width/8 * i,height/8 * j, width/8, height/8);
        } alpha = !alpha;
      } alpha = !alpha;
    }
  }
  
  public void printLines(Graphics2D page){
    for (int i=0; i<N+1; i++){
      setLineThickness(page, 3);
      page.setColor(Color.white);
      page.drawLine(width/8*i, 0, width/8*i, height);
      page.drawLine(0, height/8*i, width, height/8*i);
    }
  }
  
  public void printTokens(Graphics2D page){
    for (int i = 0; i < N; i++){
        for (int j = 0; j < N; j++){
          if (field[i][j] == 1 || field[i][j] == 3){
            page.setColor(Color.yellow);
            page.fillOval(spacew + width/8 * j, spaceh + height/8 * i, 2*radiusw, 2*radiush);
            setLineThickness(page, 2);
            page.setColor(Color.black);
            page.drawOval(spacew + width/8 * j, spaceh + height/8 * i, 2*radiusw, 2*radiush);
          }
          if (field[i][j] == 2 || field[i][j] == 4){
            page.setColor(Color.green);
            page.fillOval(spacew + width/8 * j, spaceh + height/8 * i, 2*radiusw, 2*radiush);
            setLineThickness(page, 2);
            page.setColor(Color.black);
            page.drawOval(spacew + width/8 * j, spaceh + height/8 * i, 2*radiusw, 2*radiush);           
          }
          if(field[i][j] == 3 || field[i][j] == 4){
            page.setColor(lblue);
            page.fillOval(spacew + miniradiusw + width/8 * j, spaceh + miniradiush + height/8 * i, 2*miniradiusw, 2*miniradiush);
            page.setColor(Color.white);
            page.drawOval(spacew + miniradiusw + width/8 * j, spaceh + miniradiush + height/8 * i, 2*miniradiusw, 2*miniradiush);
          }
        }
    }
  }
  public void printAll(Graphics2D page){
    int i = mouseOverY, j = mouseOverX;
    printField(page);
    Bravo: {  
      if(isValid(mousePressedY, mousePressedX) ){
        if(moving[0] == -1) break Bravo;
        else{
          page.setColor(Color.white);
          page.fillRect(width/8 * moving[1] ,height/8 * moving[0], width/8, height/8);
        }
        break Bravo;
      }
    }
      if(isValid(mouseOverY, mouseOverX)){
        if(player && (field[mouseOverY][mouseOverX] == 1 || field[mouseOverY][mouseOverX] == 3)){
        page.setColor(Color.white);
        page.fillRect(width/8 * mouseOverX ,height/8 * mouseOverY, width/8, height/8);
        }
        if(!player && (field[mouseOverY][mouseOverX] == 2 || field[mouseOverY][mouseOverX] == 4)){
        page.setColor(Color.white);
        page.fillRect(width/8 * mouseOverX ,height/8 * mouseOverY, width/8, height/8);
        }
      }
      printFromTab(page);
    
    printChance(page);
    printLines(page);
    printTokens(page);
  }
  public void printFromTab(Graphics2D page){
    for(int i = 0; i < 4; i++){
      if(mouseOverY == choice[i][0] && mouseOverX == choice[i][1]){
        page.setColor(Color.white);
        page.fillRect(width/8 * choice[i][1] ,height/8 * choice[i][0], width/8, height/8);
      }
    }
  }
  public void printChance(Graphics2D page){ //Prints white background on spots where player can move the chosen token
    int i = mousePressedY, j = mousePressedX;
    if(player && (field[i][j] == 1 || field[i][j] == 3)){
      if(j == 0 && i != N-1){
          if(mouseOverX == j+1 && mouseOverY == i+1 && field[i+1][j+1] == 0){
            page.setColor(Color.white);
            page.fillRect(width/8 * mouseOverX ,height/8 * mouseOverY, width/8, height/8);
          }
      }
      else if(j == N-1 && i != N-1){
        if(mouseOverX == j-1 && mouseOverY == i+1 && field[i+1][j-1] == 0){
          page.setColor(Color.white);
          page.fillRect(width/8 * mouseOverX ,height/8 * mouseOverY, width/8, height/8);
        }
      }
      else if(i != N-1){
        if(mouseOverX == j+1 && mouseOverY == i+1 && field[i+1][j+1] == 0){
          page.setColor(Color.white);
          page.fillRect(width/8 * mouseOverX ,height/8 * mouseOverY, width/8, height/8);
        }
        if(mouseOverX == j-1 && mouseOverY == i+1 && field[i+1][j-1] == 0){
          page.setColor(Color.white);
          page.fillRect(width/8 * mouseOverX ,height/8 * mouseOverY, width/8, height/8);
        }
      }
    }
    if(!player && (field[i][j] == 2 || field[i][j] == 4)){
      if(j == 0 && i != 0){
        if(mouseOverX == j+1 && mouseOverY == i-1 && field[i-1][j+1] == 0){
          page.setColor(Color.white);
          page.fillRect(width/8 * mouseOverX ,height/8 * mouseOverY, width/8, height/8);
        }
      }
      else if(j == N-1 && i != 0){
        if(mouseOverX == j-1 && mouseOverY == i-1 && field[i-1][j-1] == 0){
          page.setColor(Color.white);
          page.fillRect(width/8 * mouseOverX ,height/8 * mouseOverY, width/8, height/8);
        }
      }
      else if(i != 0){
        if(mouseOverX == j+1 && mouseOverY == i-1 && field[i-1][j+1] == 0){
          page.setColor(Color.white);
          page.fillRect(width/8 * mouseOverX ,height/8 * mouseOverY, width/8, height/8);
        }
        if(mouseOverX == j-1 && mouseOverY == i-1 && field[i-1][j-1] == 0){
          page.setColor(Color.white);
          page.fillRect(width/8 * mouseOverX ,height/8 * mouseOverY, width/8, height/8);
        }
      }
    }
    
    
  }
 /*
  * END OF PRINT METHODS
  */
  
  /*
   * DEBUG METHODS
   */
  
  public void printf(){
    for(int i=0; i<N; i++){
      for(int j=0; j<N; j++) System.out.print(field[i][j] + "\t");
      System.out.println();
    }
  }
  
  public void printTab(){
    for(int i=0; i<4; i++){
      for(int j=0; j<2; j++) System.out.print(choice[i][j] + "\t");
      System.out.println();
    }
  }
  /*
   * END OF DEBUG METHODS
   */
    public boolean isValid(int i, int j){  //Returns true if moving a selected token is legal, false elsewhere
    if(field[i][j] == 1){
      if(j == N-1 && i != N-1) if(field [i+1][j-1] == 0) return true;
      if(j == 0 && i != N-1) if(field [i+1][j+1] == 0) return true;
      if(i<N-1 && j<N-1) if(field[i+1][j+1] == 0) return true;
      if(i<N-1 && j>0) if(field[i+1][j-1] == 0) return true;
      if(i<N-2){ 
        if(j<N-2) if(field[i+1][j+1] == 2 && field[i+2][j+2] == 0) return true;
        if(j>1) if(field[i+1][j-1] == 2 && field[i+2][j-2] == 0) return true;
      }
    }
    if(field[i][j] == 2){
      if(j == N-1) if(field [i-1][j-1] == 0) return true;
      if(j == 0) if(field [i-1][j+1] == 0) return true;
      if(i>0 && j<N-1) if(field[i-1][j+1] == 0) return true;
      if(i>0 && j>0) if(field[i-1][j-1] == 0) return true;
      if(i>1){ 
        if(j<N-2) if(field[i-1][j+1] == 1 && field[i-2][j+2] == 0) return true;
        if(j>1) if(field[i-1][j-1] == 1 && field[i-2][j-2] == 0) return true;
      }
    }
    if(field[i][j] == 3 || field[i][j] == 4){
      if(j == N-1) if(field [i-1][j-1] == 0) return true;
      if(j == 0) if(field [i-1][j+1] == 0) return true;
      if(i>0 && j<N-1) if(field[i-1][j+1] == 0) return true;
      if(i>0 && j>0) if(field[i-1][j-1] == 0) return true;
      if(j == N-1 && i != N-1) if(field [i+1][j-1] == 0) return true;
      if(j == 0 && i != N-1) if(field [i+1][j+1] == 0) return true;
      if(i<N-1 && j<N-1) if(field[i+1][j+1] == 0) return true;
      if(i<N-1 && j>0) if(field[i+1][j-1] == 0) return true;
      if(i<N-2){ 
        if(j<N-2) if(field[i+1][j+1] == 2 && field[i+2][j+2] == 0) return true;
        if(j>1) if(field[i+1][j-1] == 2 && field[i+2][j-2] == 0) return true;
      }
      if(i>1){ 
        if(j<N-2) if(field[i-1][j+1] == 1 && field[i-2][j+2] == 0) return true;
        if(j>1) if(field[i-1][j-1] == 1 && field[i-2][j-2] == 0) return true;
      }
    }
    return false;
  }

  public void getChoice(int i, int j){
    if(i != 0 && j != 0)     {
      choice[0][0] = i-1; choice[0][1] = j-1;
      if(field[choice[0][0]][choice[0][1]] != 0){ choice[0][0] = -1; choice[0][1] = -1;}}
    if(i != 0 && j != N-1)   {choice[1][0] = i-1; choice[1][1] = j+1;
      if(field[choice[1][0]][choice[1][1]] != 0){ choice[1][0] = -1; choice[1][1] = -1;}}
    if(i != N-1 && j != N-1) {choice[2][0] = i+1; choice[2][1] = j+1;
    if(field[choice[2][0]][choice[2][1]] != 0){ choice[2][0] = -1; choice[2][1] = -1;}}
    if(i != N-1 && j!= 0)    {choice[3][0] = i+1; choice[3][1] = j-1;
    if(field[choice[3][0]][choice[3][1]] != 0){ choice[3][0] = -1; choice[3][1] = -1;}}
    
    if(i == 0 || field[i][j] == 1)  { choice[0][0] = -1; choice[0][1] = -1; choice[1][0] = -1; choice[1][1] = -1;}
    if(i == N-1 || field[i][j] == 2){ choice[2][0] = -1; choice[2][1] = -1; choice[3][0] = -1; choice[3][1] = -1;}
    
    if(j == 0)  { choice[0][0] = -1; choice[0][1] = -1; choice[3][0] = -1; choice[3][1] = -1;}
    if(j == N-1){ choice[1][0] = -1; choice[1][1] = -1; choice[2][0] = -1; choice[2][1] = -1;}
    
    if (player){
      if(i<N-2){ 
        if(j<N-2) if(field[i+1][j+1] == 2 && field[i+2][j+2] == 0)
          {choice[2][0] = i+2; choice[2][1] = j+2;}
        if(j>1) if(field[i+1][j-1] == 2 && field[i+2][j-2] == 0)
          {choice[3][0] = i+2; choice[3][1] = j-2;}
      }
      if(field[i][j] == 3){
        if(i>1){ 
        if(j<N-2) if((field[i-1][j+1] == 2 || field[i-1][j+1] == 4)&& field[i-2][j+2] == 0)
          {choice[1][0] = i-2; choice[1][1] = j+2;}
        if(j>1) if((field[i-1][j-1] == 2 || field[i-1][j-1] == 4) && field[i-2][j-2] == 0)
          {choice[0][0] = i-2; choice[0][1] = j-2;}
        }
      }
    }
    if(!player){
      if(i>1){ 
        if(j<N-2) if(field[i-1][j+1] == 1 && field[i-2][j+2] == 0)
          {choice[1][0] = i-2; choice[1][1] = j+2;}
        if(j>1) if(field[i-1][j-1] == 1 && field[i-2][j-2] == 0)
          {choice[0][0] = i-2; choice[0][1] = j-2;}
      }
      if(field[i][j] == 4){
        if(i<N-2){ 
        if(j<N-2) if((field[i+1][j+1] == 1 || field[i+1][j+1] == 3) && field[i+2][j+2] == 0)
          {choice[2][0] = i+2; choice[2][1] = j+2;}
        if(j>1) if((field[i+1][j-1] == 1 || field[i+1][j-1] == 3) && field[i+2][j-2] == 0)
          {choice[3][0] = i+2; choice[3][1] = j-2;}
        }
      }
    }
    printTab();
  }
  public boolean checkChoice(int x, int y){
    for(int i = 0; i<4; i++){
      if(choice[i][0] == x && choice[i][1] == y) return true; }
    return false;
  }
  public void go(){
    wp:{
      if(wtd){
        if(player){
          if(field[mousePressedY][mousePressedX] == 2 || field[mousePressedY][mousePressedX] == 4) break wp;
        }
        if(!player){
          if(field[mousePressedY][mousePressedX] == 1 || field[mousePressedY][mousePressedX] == 3) break wp;
        }
        
          if(isValid(mousePressedY, mousePressedX)){
          getChoice(mousePressedY, mousePressedX);
          wtd = false;
          moving[0] = mousePressedY;
          moving[1] = mousePressedX;
        }
      }
      else{
        if(checkChoice(mousePressedY, mousePressedX)){
          field[mousePressedY][mousePressedX] = field[moving[0]][moving[1]];
          if(player && mousePressedY == N-1) field[mousePressedY][mousePressedX] = 3;
          if(!player && mousePressedY == 0) field[mousePressedY][mousePressedX] = 4;
          field[moving[0]][moving[1]] = 0;
          wtd = true;
          player = !player;
          if(Math.abs(mousePressedY - moving[0]) == 2){
            if(mousePressedY == choice[0][0] && mousePressedX == choice[0][1])
              field[moving[0] - 1][moving[1] - 1] = 0;
            if(mousePressedY == choice[1][0] && mousePressedX == choice[1][1])
              field[moving[0] - 1][moving[1] + 1] = 0;
            if(mousePressedY == choice[2][0] && mousePressedX == choice[2][1])
              field[moving[0] + 1][moving[1] + 1] = 0;
            if(mousePressedY == choice[3][0] && mousePressedX == choice[3][1])
              field[moving[0] + 1][moving[1] - 1] = 0;
          }
          moving[0] = -1;
          moving[1] = -1;
          for(int i = 0; i<4; i++){
            choice[i][0] = -1; choice[i][1] = -1;
          }
        }
      }
    }
  }
}