package mx.qbits.crypto.executor.model;

public class Operacion {
    private double valor;
    private double cantidad;
    private String accion;
    private String sender;
    
    public Operacion() {
    }
    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }
    public double getValor() {
        return valor;
    }
    public void setValor(double valor) {
        this.valor = valor;
    }
    public double getCantidad() {
        return cantidad;
    }
    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }
    public String getAccion() {
        return accion;
    }
    public void setAccion(String accion) {
        this.accion = accion;
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" accion: ");
        sb.append(this.accion);
        sb.append(" cantidad: ");
        sb.append(this.cantidad);
        sb.append(" valor: ");
        sb.append(this.valor);
        sb.append(" sender: ");
        sb.append(this.sender);
        return sb.toString();
    }
}
