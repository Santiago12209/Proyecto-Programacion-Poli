import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase encargada de generar de manera pseudoaleatoria los archivos planos
 * necesarios para la prueba del sistema de ventas.
 * 
 * @author Santiago Castillo Reyes
 * @version 1.0 (Entrega 2 - Semana 5)
 */
public class GenerateInfoFiles {

    private static final String[] TIPOS_DOC = {"CC", "CE", "NIT", "PASAPORTE"};
    private static final String[] NOMBRES = {"Santiago", "Mateo", "Maria", "Jina", "Paola", "Carlos", "Andrea", "Juan", "Laura", "Diego"};
    private static final String[] APELLIDOS = {"Castillo", "Reyes", "Sierra", "Diaz", "Bayona", "Paez", "Bonilla", "Rojas", "Gomez", "Lopez"};
    private static final String[] PRODUCTOS_NOM = {"Portatil", "Mouse", "Teclado", "Monitor", "Diadema", "Camara Web", "Impresora", "Disco Duro"};

    public static void main(String[] args) {
        try {
            int cantidadProductos = 10;
            int cantidadVendedores = 5;

            createProductsFile(cantidadProductos);
            createSalesManInfoFile(cantidadVendedores);

            Random rand = new Random();
            for (int i = 1; i <= cantidadVendedores; i++) {
                String name = NOMBRES[rand.nextInt(NOMBRES.length)] + " " + APELLIDOS[rand.nextInt(APELLIDOS.length)];
                long id = 1000000000L + rand.nextInt(900000000);
                createSalesMenFile(5 + rand.nextInt(10), name, id);
            }

            System.out.println("Finalizacion exitosa: Todos los archivos de prueba fueron generados correctamente.");
        } catch (Exception e) {
            System.err.println("Error durante la generacion de archivos de prueba: " + e.getMessage());
        }
    }

    public static void createSalesMenFile(int randomSalesCount, String name, long id) {
        Random rand = new Random();
        String tipoDoc = TIPOS_DOC[rand.nextInt(TIPOS_DOC.length)];
        String fileName = "vendedor_" + id + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(tipoDoc + ";" + id);
            writer.newLine();

            for (int i = 0; i < randomSalesCount; i++) {
                int idProducto = 1 + rand.nextInt(10);
                int cantidadVendida = 1 + rand.nextInt(15);
                writer.write(idProducto + ";" + cantidadVendida + ";");
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al crear archivo de ventas para el vendedor ID " + id + ": " + e.getMessage());
        }
    }

    public static void createProductsFile(int productsCount) {
        Random rand = new Random();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("productos.txt"))) {
            for (int i = 1; i <= productsCount; i++) {
                String nombreProducto = PRODUCTOS_NOM[rand.nextInt(PRODUCTOS_NOM.length)] + " " + i;
                double precioUnitario = 10000 + (rand.nextDouble() * 490000);
                precioUnitario = Math.round(precioUnitario * 100.0) / 100.0;

                writer.write(i + ";" + nombreProducto + ";" + precioUnitario);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al crear el archivo de productos: " + e.getMessage());
        }
    }

    public static void createSalesManInfoFile(int salesmanCount) {
        Random rand = new Random();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("vendedores.txt"))) {
            for (int i = 1; i <= salesmanCount; i++) {
                String tipoDoc = TIPOS_DOC[rand.nextInt(TIPOS_DOC.length)];
                long numDoc = 1000000000L + rand.nextInt(900000000);
                String nombre = NOMBRES[rand.nextInt(NOMBRES.length)];
                String apellido = APELLIDOS[rand.nextInt(APELLIDOS.length)];

                writer.write(tipoDoc + ";" + numDoc + ";" + nombre + ";" + apellido);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al crear el archivo de vendedores: " + e.getMessage());
        }
    }
}
