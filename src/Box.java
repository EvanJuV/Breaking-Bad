
import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Evan
 */
public class Box implements Serializable {
    private int iX;       // posicion en x del objeto
    private int iY;       // posicion en y del objeto
    private int iWidth;   // ancho del objeto
    private int iHeight;  // altura del objeto
    
    // Constructor por default
    public Box(int iX, int iY, int iWidth, int iHeight) {
        this.iX = iX;
        this.iY = iY;
        this.iWidth = iWidth;
        this.iHeight = iHeight;
    }
    
    // getters y setters

    public int getiX() {
        return iX;
    }

    public void setiX(int iX) {
        this.iX = iX;
    }

    public int getiY() {
        return iY;
    }

    public void setiY(int iY) {
        this.iY = iY;
    }

    public int getiWidth() {
        return iWidth;
    }

    public void setiWidth(int iWidth) {
        this.iWidth = iWidth;
    }

    public int getiHeight() {
        return iHeight;
    }

    public void setiHeight(int iHeight) {
        this.iHeight = iHeight;
    }
    
    /**
     * Metodo de acceso que regresa un nuevo rectangulo
     *
     * @return un objeto de la clase <code>Rectangle</code> que es el perimetro
     * del rectangulo
     */
    public java.awt.Rectangle getPerimetro() {
        return new java.awt.Rectangle(getiX(), getiY(), getiWidth(), getiHeight());
    }

    /**
     * Checa si el objeto <code>CelestialBody</code> intersecta a otro
     * <code>CelestialBody</code>
     *
     * @return un valor boleano <code>true</code> si lo intersecta
     * <code>false</code> en caso contrario
     */
    public boolean intersects(Box obj) {
        return getPerimetro().intersects(obj.getPerimetro());
    }
}
