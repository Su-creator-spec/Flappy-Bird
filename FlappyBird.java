import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener,KeyListener{
    int boardwidth=360;
    int boardheight=640;
    // Images 
    Image backgroundImage;
    Image birdImage;
    Image topPipeImage;
    Image bottomPipeImage;
    
    // Bird 
    int birdX = boardwidth/8;
    int birdY = boardheight/2;
    int birdwidth=34;
    int birdheigth =24;
    class Bird{
        int x=birdX;
        int y= birdY;
        int width=birdwidth;
        int heigth=birdheigth;
        Image img ;

        Bird(Image img){
            this.img=img;

        }
    }


    // pipes 
    int pipeX=boardwidth;
    int pipeY=0;
    int pipewidth=64;
    int pipeheigth=512;

    class pipe{
        int x =pipeX;
        int y= pipeY;
        int width=pipewidth;
        int heigth =pipeheigth;
        Image img;
        boolean passed =false;

        pipe(Image img){
            this.img=img;
        }

    }

    // game logic 
    Bird bird;
    int velocityX=-4;//moves the pipes to the left (simultes bird moving to right)
    int velocityY=0;// moves the bird up and down 
    int gravity =1;

    ArrayList<pipe> pipes;
    Random random =new Random();
    
    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver=false;
    double score=0;



    FlappyBird(){
        setPreferredSize(new Dimension(boardwidth,boardheight));
        //setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this); // using the key board related functions are checked



        // loading the images 
        backgroundImage= new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImage=new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        
        // bird 
        bird =new Bird(birdImage);
        pipes =new ArrayList<pipe>();
        // palce piprs timer 
        placePipesTimer = new Timer(1500,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                placepipes();
            }
        });
        placePipesTimer.start();


        // game timer
        gameLoop = new Timer(1000/60,this); // 60 frames per second and 1000 milliseconds = 1 second
        gameLoop.start();   
    }
    public void placepipes(){
        int randomPipeY=(int)(pipeY-pipeheigth/4-Math.random()*(pipeheigth/2));
        int openspace=boardheight/4;
        pipe toppipe =new pipe(topPipeImage);
        toppipe.y=randomPipeY;
        pipes.add(toppipe);
        pipe bottompipe =new pipe(bottomPipeImage);
        bottompipe.y=toppipe.y+pipeheigth+openspace;
        pipes.add(bottompipe);


    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);

    }
    public void draw(Graphics g){
        // background
        g.drawImage(backgroundImage,0,0,boardwidth,boardheight,null);
        
        // bird 
        g.drawImage(birdImage,bird.x,bird.y,birdwidth,birdheigth,null);
        // pipes 
        for(int i=0;i<pipes.size();i++){
            pipe Pipe =pipes.get(i);
            g.drawImage(Pipe.img,Pipe.x,Pipe.y,pipewidth,pipeheigth,null);
            
        }
        //score 
        g.setColor(Color.white);
        g.setFont(new Font("Arial",Font.PLAIN,32));
        if(gameOver){
            g.drawString("Game Over"+String.valueOf((int)score),10,35);
        }else{
            g.drawString(String.valueOf((int)score),10,35);

        }
    }
    //velocity is -6 and gravity is +2
    public void move(){
        // bird 
        velocityY+=gravity;
        bird.y+=velocityY;
        bird.y=Math.max(bird.y,0);

        //pipes
        for (int i=0;i<pipes.size();i++){
            pipe Pipe =pipes.get(i);
            Pipe.x+= velocityX;
            if(!Pipe.passed && bird.x>Pipe.x+Pipe.width){
                Pipe.passed=true;
                score+=0.5;// 0.5 becase there are 2 pipes , 1 for one set of pipes 

            }
            if(collision(bird,Pipe)){
                gameOver=true;

            }

        }
        if (bird.y>boardheight){
            gameOver=true;
        }


    }
    public boolean collision(Bird a , pipe b){
        return a.x < b.x +b.width && a.x+a.width>b.x && a.y<b.y +b.heigth && a.y+a.heigth>b.y;

    }
    // this function will repaint the bird 60 times in a second
    @Override
    public void actionPerformed(ActionEvent e){
        move();
        repaint();
        if(gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }

    }
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()== KeyEvent.VK_SPACE){
            velocityY=-9;
        }
        if(gameOver){
            // reatart the same by resettingthe conditions 
            bird.y=birdY;
            velocityY=0;
            pipes.clear();
            score=0;
            gameOver=false;
            gameLoop.start();
            placePipesTimer.start();

        }
    }
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

}


