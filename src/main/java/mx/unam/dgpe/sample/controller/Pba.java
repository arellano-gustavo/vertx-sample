package mx.unam.dgpe.sample.controller;

import java.util.ArrayList;
import java.util.List;

public class Pba {
    static int lenX = 5;
    static int lenY = 5;
    Cuadro[][] caja = null;

    /**
     * Este es un ejemplo de documentación javaDoc
     * @param args
     */
    public static void main(String[] args) {
        Pba p = new Pba();
        p.ok2();
    }
    
    public void ok2() {
        caja = new Cuadro[lenX][lenY];
        for(int i=0; i<lenX; i++) {
          for(int j=0; j<lenY; j++) {
              caja[i][j] = new Cuadro(i,j,1);
          }
        }
        ok(caja[0][0]);
    }
    
    public void ok(Cuadro c) { 
        if(c.x==lenX && c.y==lenY) {
             // and it has a 1 on it
            System.out.println(c.x+"  "+c.y);
            return;
        }
        List<Cuadro> candidates = getCandidates(caja, c.x, c.y);
        for(Cuadro cu : candidates) {
            if(cu.value==1) {
                ok(cu);
                System.out.println(cu.x+"  "+cu.y);
            }
        }
    }
    
    public List<Cuadro> getCandidates(Cuadro[][] caja, int x, int y) {
        List<Cuadro> list = new ArrayList<>();
        if (x+1<lenX) list.add(caja[x+1][y]);
        if (x-1>=0)   list.add(caja[x-1][y]);
        if (y+1<lenY) list.add(caja[x][y+1]);
        if (y-1>=0)   list.add(caja[x][y-1]);
        return list;
    }
    
    
  
    
    class Cuadro {
        int x;
        int y;
        int value;
        Cuadro(int x, int y, int value) {
            this.x = x;
            this.y = y;
            this.value = value;
        }
    }
    

}
