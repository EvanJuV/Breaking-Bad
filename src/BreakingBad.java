
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Evan
 */
public class BreakingBad extends JFrame implements ActionListener, KeyListener, Runnable {

    int iDirBar;  // Direccion de la barra
    int iBricks;  // numero de bricks en el juego
    int iMovX;    // Movimiento en x de la pelota
    int iMovY;    // Movimiento en y de la pelota
    int iLives;   // Numero de vidas
    int iScore;   // Score del juego
    int iSpeed;   // Distancia que recorre por lapso de tiempo
    int iSpeedLapse;   // Lapso de tiempo para mover pelota
    int iTipoRes;
    boolean bPause;    // variable que indica si el juega esta en pausa
    boolean bEnd;      // variable que indica que el juego ha terminado
    Box boxBall;       // pelota del juego
    Box boxBar;        // barra del jugador
    ArrayList<Box> alList = new ArrayList<>();   // arreglo de los bloques
    JButton butReset;            // boton para resetar juego
    private Image dbImage;	// Imagen a proyectar	
    private Graphics dbg;	// Objeto grafico
    private long lNow, lBefore; // Timers de control de velocidad
    ImageIcon imgBackground;
    private Image pausa; // Imagen a desplegar al estar en pausa
    private Image gameover; // Imagen a desplegar al finalizar
    String strMsg;
    Thread th;
    
    // Constructor de la clase
    BreakingBad() {
        URL eURL = this.getClass().getResource("bb.jpg");
        imgBackground = new ImageIcon(Toolkit.getDefaultToolkit().getImage(eURL));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 900);
        setLocationRelativeTo(null);
        setResizable(false);
        addKeyListener(this);
        setFocusable(true);
        setVisible(true);
        iTipoRes = 0;
        init();
        start();
    }

    public static void main(String[] args) {
        BreakingBad game = new BreakingBad();
    }

    /**
     * Metodo <I>init</I> para inicializar las variables del juego
     */
    private void init() {
        th = new Thread(this);
        iBricks = 15;
        iSpeed = 2;
        iSpeedLapse = 6;
        iScore = 0;
        iLives = 5;
        URL eURL = this.getClass().getResource("bullet.png");
        boxBall = new Box((int) (Math.random() * this.getWidth()), (int) (Math.random() * this.getHeight() / 8) + 500, 30, 30,Toolkit.getDefaultToolkit().getImage(eURL));
        URL fURL = this.getClass().getResource("hank.png");
        boxBar = new Box(450, 783, 200, 116, Toolkit.getDefaultToolkit().getImage(fURL));
        URL gURL = this.getClass().getResource("tabCrack.png");
        URL paURL=this.getClass().getResource("pausaFnl.png");
	pausa=Toolkit.getDefaultToolkit().getImage(paURL);
        URL gaURL=this.getClass().getResource("gameover.png");
	pausa=Toolkit.getDefaultToolkit().getImage(gaURL);
        bPause = false;
        bEnd = false;
        //inicializamos la pelota moviendose hacia abajo 
        iMovX = Math.random() <= 0.5 ? -1 : 1;
        iMovY = 1;

        // Inicializamos los bricks en un grid de 15 bricks
        int x = 260;
        int y = 200;
        for (int i = 0; i < iBricks; i++) {
            if (i == 5) {
                x = 320;
                y = 260;
            }
            if (i == 9) {
                x = 370;
                y = 320;
            }
            if (i == 12) {
                x = 430;
                y = 380;
            }
            if (i == 14) {
                x = 485;
                y = 440;
            }
            alList.add(new Box(x, y, 100, 50,Toolkit.getDefaultToolkit().getImage(gURL)));
            x += 110;
        }
    }

    /**
     * Metodo <I>start</I> sobrescrito de la interface <code>Runnable</code>.<P>
     * En este metodo se crea e inicializa el hilo para la animacion este metodo
     * es llamado despues del init o cuando el usuario visita otra pagina y
     * luego regresa a la pagina en donde esta este <code>JFrame</code>
     *
     */
    public void start() {
        // Declaras un hilo
        //Thread th = new Thread(this);
        // Empieza el hilo
        th.start();
        lBefore = System.currentTimeMillis();
    }

    @Override
    /**
     * Metodo <I>run</I> sobrescrito de la interfaz <code>Runnable</code>.<P>
     * Declara las acciones que realizara el JFrame al ejecutarse.
     */
    public void run() {
        // run until lives aren't 0
        while (iLives > 0 && iBricks > 0) {
            repaint();
            if(!bPause) {
                actualiza();
                checaColision();
            }
            try {
                // El thread se duerme.
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                System.out.println("Error en " + ex.toString());
            }
        }
        bEnd = true;
        repaint();
    }

    /**
     * Metodo <I>actualiza</I> utilizado para actualizar las posiciones del
     * asteroide y del planeta
     */
    public void actualiza() {
        // Cambio de direccion de la barra respecto a la tecla presionada
        if (iDirBar == 6) {
            boxBar.setiX(boxBar.getiX() + 2);
        }
        if (iDirBar == 4) {
            boxBar.setiX(boxBar.getiX() - 2);
        }
        lNow = System.currentTimeMillis();
        // Movimiento de la pelota
        if (lNow - lBefore >= iSpeedLapse) {
            boxBall.setiX(boxBall.getiX() + (iMovX * iSpeed));
            boxBall.setiY(boxBall.getiY() + (iMovY * iSpeed));
            lBefore = lNow;
        }
    }

    /**
     * Metodo <I>checaColision</I> usado para checar las colisiones del objeto
     * elefante y asteroid con las orillas del <code>JFrame</code>.
     */
    public void checaColision() {
        // Si la barra toca la pared izquierda se le impide seguir desplazandose
        if (boxBar.getiX() <= 0) {
            boxBar.setiX(0);
        } // si toca la pared derecha se le impide seguir desplazandose
        else if (boxBar.getiX() >= this.getWidth() - boxBar.getiWidth()) {
            boxBar.setiX(this.getWidth() - boxBar.getiWidth());
        }
        // Si la pelota choca contra la pared izquierda o derecha
        if (boxBall.getiX() <= 0 || boxBall.getiX() >= this.getWidth() - boxBall.getiWidth()) {
            // Cambia de direccion en x
            iMovX *= -1;
        }
        // si choca contra la pared superior
        if (boxBall.getiY() <= 0) {
            //cambia de direccion en y
            iMovY *= -1;
        }
        // si la pelota intersecta la barra
        if (boxBall.intersects(boxBar) && boxBall.getiY() <= boxBar.getiY() - boxBall.getiWidth() * 3 / 4) {
            iMovY *= -1;
        }
        // si la pelota toca el suelo perdemos una vida
        if (boxBall.getiY() >= this.getHeight()) {
            iLives--;
            boxBall.setiX((int) (Math.random() * this.getWidth()));
            boxBall.setiY((int) (Math.random() * this.getHeight() / 8) + 500);
            iMovY = 1;
            iMovX = Math.random() <= 0.5 ? -1 : 1;
            iSpeedLapse--;
        }
        // checamos choque con cada brick
        for(int i=0;i<alList.size();i++){
            if (alList.get(i).intersects(boxBall)) {
                if (boxBall.getiX() < alList.get(i).getiX() + 10 || boxBall.getiX() > alList.get(i).getiX() + alList.get(i).getiWidth() - 10) {
                    iMovX *= -1;
                } else {
                    iMovY *= -1;
                }
                // "eliminamos" el brick
                alList.remove(alList.get(i));
                iScore += 100;
                iBricks--;
            }
        }
    }

    /**
     * Metodo <I>draw</I> utilizado para dibujar en el JFrame los objetos con
     * sus respectivas imagenes
     *
     * @param g
     */
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.TYPE1_FONT, 20));
        g.drawImage(imgBackground.getImage(), 0, 0, this);
        g.drawString("Lives left: " + iLives + "   Score: " + iScore, 30, 80);
        // Si el juego esta perdido
        if (bEnd) {
            // Dibuja pantalla de fin
            g.drawImage(gameover, 0, 0, this);
            // definimos boton
            butReset = new JButton("PLAY AGAIN?");
            butReset.addActionListener(this);
            butReset.setLocation(420, 300);
            butReset.setSize(200, 50);
            add(butReset);
            super.paintComponents(g);
        }
        if(bPause){
            // Dibuja pantalla de pausa
            g.drawImage(pausa, 0, 0, this);
            g.drawString("Juego Pausado", 450, 300);
            g.drawString("G para guardar", 450, 750);
            g.drawString("L para cargar", 450, 800);
        }
        if(iTipoRes != 0) {
            switch(iTipoRes) {
                case 1:
                    strMsg = "Juego guardado exitosamente";
                    break;
                case 2:
                    strMsg = "Juego cargado exitosamente";
                    break;
                case 3:
                    strMsg = "No hay ningun juego guardado";
                    break;
                case 4:
                    strMsg = "Error al guardar";
                    break;
            }
            g.drawString(strMsg, 20, 50);
        }
        if (!bEnd && !bPause && boxBall != null && boxBar != null && !alList.isEmpty()) {
            g.setColor(Color.GRAY);
            //Dibuja la pelota en la posicion actualizada
            g.fillOval(boxBall.getiX(), boxBall.getiY(), boxBall.getiWidth(), boxBall.getiHeight());
            // Dibuja la barra en la posicion actualizada
            g.drawImage(boxBar.getImagenI(), boxBar.getiX(),boxBar.getiY(), this);
            for (Box b : alList) {
                g.drawImage(b.getImagenI(), b.getiX(),b.getiY(), this);
            }
        } else {
            //Da un mensaje mientras se carga el dibujo	
            g.drawString("No se cargo la imagen..", 20, 20);
        }
    }

    /**
     * Metodo <I>paint</I> sobrescrito de la clase <code>Applet</code>, heredado
     * de la clase Container.<P>
     * En este metodo lo que hace es actualizar el contenedor
     *
     * @param g es el <code>objeto grafico</code> usado para dibujar.
     */
    public void paint(Graphics g) {
        // Inicializan el DoubleBuffer
        if (dbImage == null) {
            dbImage = createImage(this.getSize().width, this.getSize().height);
            dbg = dbImage.getGraphics();
        }

        // Actualiza la imagen de fondo.
        dbg.setColor(getBackground());
        dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);

        // Actualiza el Foreground.
        dbg.setColor(getForeground());
        draw(dbg);

        // Dibuja la imagen actualizada
        g.drawImage(dbImage, 0, 0, this);
    }
    
    public void guardarJuego() {
        Hashtable hash = new Hashtable();
        hash.put("iDirBar", iDirBar);
        hash.put("iBricks", iBricks);
        hash.put("iMovX", iMovX);
        hash.put("iMovY", iMovY);
        hash.put("iLives", iLives);
        hash.put("iScore", iScore);
        hash.put("iSpeed", iSpeed);
        hash.put("iSpeedLapse", iSpeedLapse);
        hash.put("boxBall", boxBall);
        hash.put("boxBar", boxBar);
        hash.put("alList", alList);
        try {
            FileOutputStream fileOut = new FileOutputStream("savedGame.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(hash);
            out.close();
            fileOut.close();
            iTipoRes = 1;
        } catch(FileNotFoundException e) {
            iTipoRes = 4;
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
        System.out.println("Guardado");
    }
    
    public void cargarJuego() {
        Hashtable hash = null;
        
        try {
            FileInputStream fileIn = new FileInputStream("savedGame.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            hash = (Hashtable) in.readObject();
            in.close();
            fileIn.close();
            iTipoRes = 2;
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        } catch(FileNotFoundException e) {
            iTipoRes = 3;
            // File not found
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        iDirBar = (int) hash.put("iDirBar", iDirBar);
        iBricks = (int) hash.put("iBricks", iBricks);
        iMovX = (int) hash.put("iMovX", iMovX);
        iMovY = (int) hash.put("iMovY", iMovY);
        iLives = (int) hash.put("iLives", iLives);
        iScore = (int) hash.put("iScore", iScore);
        iSpeed = (int) hash.put("iSpeed", iSpeed);
        iSpeedLapse = (int) hash.put("iSpeedLapse", iSpeedLapse);
        boxBall = (Box) hash.put("boxBall", boxBall);
        boxBar = (Box) hash.put("boxBar", boxBar);
        alList = (ArrayList) hash.put("alList", alList);
        System.out.println("Cargado");
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        // Se detecta la tecla presionada y se cambia el valor de la variable control
        if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
            iDirBar = 6;
            return;
        } else if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
            iDirBar = 4;
            return;
        }
        // si se presiona la letra p, se pausa el juego
        if (ke.getKeyCode() == KeyEvent.VK_P) {
            bPause = !bPause;
            iTipoRes = 0;
            return;
        }
        if(bPause) {
            if(ke.getKeyCode() == KeyEvent.VK_R) {
                this.dispose();
                new BreakingBad();
                return;
            }
            if(ke.getKeyCode() == KeyEvent.VK_G) {
                guardarJuego();
                return;
            }
            if(ke.getKeyCode() == KeyEvent.VK_L) {
                cargarJuego();
                return;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        // reset de la variable de control de direccion
        iDirBar = 0;
    }
}
