package Pedidos;

import java.util.List;

public class Pedido {
    private int id;
    private String cliente;
    private List<LineaPedido> lineas;
    private double total;

    public Pedido(int id, String cliente, List<LineaPedido> lineas, double total) {
        this.id = id;
        this.cliente = cliente;
        this.lineas = lineas;
        this.total = total;
    }

    public int getId() { return id; }
    public String getCliente() { return cliente; }
    public List<LineaPedido> getLineas() { return lineas; }
    public double getTotal() { return total; }
}