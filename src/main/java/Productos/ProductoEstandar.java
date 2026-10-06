package Productos;

public class ProductoEstandar extends Producto {
    private String categoria;

    public ProductoEstandar(int id, String nombre, double precio, int stock, String categoria) {
        super(id, nombre, precio, stock);
        this.categoria = categoria;
    }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    @Override
    public String toString() {
        return super.toString() + " | Categoría: " + categoria;
    }
}