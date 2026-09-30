import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase principal que procesa la informacion de ventas, productos y vendedores,
 * calculando los totales y generando los reportes en formato CSV.
 * 
 * @author Santiago Castillo Reyes
 * @version 1.0 (Entrega 2 - Semana 5)
 */
public class main {

    static class Producto {
        int id;
        String nombre;
        double precio;

        public Producto(int id, String nombre, double precio) {
            this.id = id;
            this.nombre = nombre;
            this.precio = precio;
        }
    }

    static class Vendedor {
        String tipoDoc;
        long numDoc;
        String nombres;
        String apellidos;
        double totalRecaudado;

        public Vendedor(String tipoDoc, long numDoc, String nombres, String apellidos) {
            this.tipoDoc = tipoDoc;
            this.numDoc = numDoc;
            this.nombres = nombres;
            this.apellidos = apellidos;
            this.totalRecaudado = 0.0;
        }
    }

    static class ProductoVendido implements Comparable<ProductoVendido> {
        String nombre;
        double precio;
        int cantidadTotal;

        public ProductoVendido(String nombre, double precio, int cantidadTotal) {
            this.nombre = nombre;
            this.precio = precio;
            this.cantidadTotal = cantidadTotal;
        }

        @Override
        public int compareTo(ProductoVendido o) {
            return Integer.compare(o.cantidadTotal, this.cantidadTotal);
        }
    }

    public static void main(String[] args) {
        try {
            Map<Integer, Producto> mapaProductos = cargarProductos("productos.txt");
            Map<Long, Vendedor> mapaVendedores = cargarVendedores("vendedores.txt");
            Map<Integer, Integer> unidadesVendidasPorProducto = new HashMap<>();

            File carpetaActual = new File(".");
            File[] archivos = carpetaActual.listFiles((dir, name) -> name.startsWith("vendedor_") && name.endsWith(".txt"));

            if (archivos != null) {
                for (File archivoVenta : archivos) {
                    procesarArchivoVentas(archivoVenta, mapaProductos, mapaVendedores, unidadesVendidasPorProducto);
                }
            }

            generarReporteVendedores(mapaVendedores);
            generarReporteProductos(mapaProductos, unidadesVendidasPorProducto);

            System.out.println("Finalizacion exitosa: Reportes generados correctamente.");

        } catch (Exception e) {
            System.err.println("Error durante la ejecucion del programa principal: " + e.getMessage());
        }
    }

    private static Map<Integer, Producto> cargarProductos(String ruta) throws IOException {
        Map<Integer, Producto> productos = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split(";");
                if (partes.length >= 3) {
                    int id = Integer.parseInt(partes[0].trim());
                    String nombre = partes[1].trim();
                    double precio = Double.parseDouble(partes[2].trim());
                    if (precio >= 0) {
                        productos.put(id, new Producto(id, nombre, precio));
                    }
                }
            }
        }
        return productos;
    }

    private static Map<Long, Vendedor> cargarVendedores(String ruta) throws IOException {
        Map<Long, Vendedor> vendedores = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split(";");
                if (partes.length >= 4) {
                    String tipoDoc = partes[0].trim();
                    long numDoc = Long.parseLong(partes[1].trim());
                    String nombres = partes[2].trim();
                    String apellidos = partes[3].trim();
                    vendedores.put(numDoc, new Vendedor(tipoDoc, numDoc, nombres, apellidos));
                }
            }
        }
        return vendedores;
    }

    private static void procesarArchivoVentas(File archivo, Map<Integer, Producto> productos, 
                                               Map<Long, Vendedor> vendedores, 
                                               Map<Integer, Integer> acumuladorProductos) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String primeraLinea = reader.readLine();
            if (primeraLinea == null || primeraLinea.trim().isEmpty()) return;

            String[] partesEncabezado = primeraLinea.split(";");
            if (partesEncabezado.length < 2) return;

            long numDocVendedor = Long.parseLong(partesEncabezado[1].trim());
            Vendedor vendedor = vendedores.get(numDocVendedor);

            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split(";");
                if (partes.length >= 2) {
                    int idProducto = Integer.parseInt(partes[0].trim());
                    int cantidad = Integer.parseInt(partes[1].trim());

                    if (cantidad > 0 && productos.containsKey(idProducto)) {
                        Producto prod = productos.get(idProducto);
                        if (vendedor != null) {
                            vendedor.totalRecaudado += (prod.precio * cantidad);
                        }
                        acumuladorProductos.put(idProducto, acumuladorProductos.getOrDefault(idProducto, 0) + cantidad);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo procesar el archivo " + archivo.getName());
        }
    }

    private static void generarReporteVendedores(Map<Long, Vendedor> vendedores) throws IOException {
        List<Vendedor> listaVendedores = new ArrayList<>(vendedores.values());
        listaVendedores.sort((v1, v2) -> Double.compare(v2.totalRecaudado, v1.totalRecaudado));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("reporte_vendedores.csv"))) {
            for (Vendedor v : listaVendedores) {
                String totalFormateado = String.format("%.2f", v.totalRecaudado).replace(",", ".");
                writer.write(v.nombres + " " + v.apellidos + ";" + totalFormateado);
                writer.newLine();
            }
        }
    }

    private static void generarReporteProductos(Map<Integer, Producto> productos, Map<Integer, Integer> unidadesVendidas) throws IOException {
        List<ProductoVendido> listaProductos = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : unidadesVendidas.entrySet()) {
            int idProd = entry.getKey();
            int cantidad = entry.getValue();
            if (productos.containsKey(idProd)) {
                Producto p = productos.get(idProd);
                listaProductos.add(new ProductoVendido(p.nombre, p.precio, cantidad));
            }
        }

        Collections.sort(listaProductos);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("reporte_productos.csv"))) {
            for (ProductoVendido pv : listaProductos) {
                String precioFormateado = String.format("%.2f", pv.precio).replace(",", ".");
                writer.write(pv.nombre + ";" + precioFormateado + ";" + pv.cantidadTotal);
                writer.newLine();
            }
        }
    }
}
