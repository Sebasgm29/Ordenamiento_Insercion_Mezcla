package servicios;

import modelos.Documento;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DocumentoServicio {
    private static String[] encabezados = new String[]{"#", "Primer Apellido", "Segundo Apellido", "Nombres",
            "Documento"};

    public static String[] getEncabezados() {
        return encabezados;
    }

    private static List<Documento> documentos = Collections.emptyList();

    public static void cargarDatos(String nombreArchivo) {
        var ruta = Paths.get(nombreArchivo);

        try (Stream<String> lineas = Files.lines(ruta)) {

            documentos = lineas.skip(1)
                    .filter(linea -> linea != null & !linea.isBlank())
                    .map(linea -> linea.split("[,;]"))
                    .filter(textos -> textos.length >= 4)
                    .map(textos -> new Documento(textos[0], textos[1], textos[2], textos[3]))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            documentos = Collections.emptyList();
        }
    }

    public static String[][] getDatos() {
        AtomicInteger fila = new AtomicInteger(1);
        return documentos.stream()
                .map(documento -> new String[]{
                        String.valueOf(fila.getAndIncrement()),
                        documento.getApellido1(),
                        documento.getApellido2(),
                        documento.getNombre(),
                        documento.getDocumento()
                })
                .collect(Collectors.toList())
                .toArray(String[][]::new);
    }

    /********** Ordenamientos **********/

    public static boolean esMayor(Documento d1, Documento d2, int criterio) {
        if (criterio == 0) // primero nombre completo y luego el tipo de documento
        {
            return d1.getNombreCompleto().compareTo(d2.getNombreCompleto()) > 0 ||
                    (d1.getNombreCompleto().equals(d2.getNombreCompleto()) &&
                            d1.getDocumento().compareTo(d2.getDocumento()) > 0);
        } else // primero el tipo de documento y luego el nombre completo
        {
            return d1.getDocumento().compareTo(d2.getDocumento()) > 0 ||
                    (d1.getDocumento().equals(d2.getDocumento()) &&
                            d1.getNombreCompleto().compareTo(d2.getNombreCompleto()) > 0);
        }
    }

    private static void intercambiar(int i, int j) {
        if (0 <= i && i < documentos.size() && 0 <= j && j < documentos.size() && i != j) {
            var aux = documentos.get(i);
            documentos.set(i, documentos.get(j));
            documentos.set(j, aux);
        }
    }

    public static void ordenarBurbuja(int criterio) {
        for (int i = 0; i < documentos.size() - 1; i++) {
            for (int j = i + 1; j < documentos.size(); j++) {
                if (esMayor(documentos.get(i), documentos.get(j), criterio)) {
                    intercambiar(i, j);
                }
            }
        }
    }

    private static int ubicarPivote(int inicio, int fin, int criterio) {
        int pivote = inicio;
        var documentoPivote = documentos.get(pivote);
        for (int i = inicio + 1; i <= fin; i++) {
            if (esMayor(documentoPivote, documentos.get(i), criterio)) {
                pivote++;
                if (i != pivote)
                    intercambiar(i, pivote);

            }
        }
        if (inicio != pivote)
            intercambiar(inicio, pivote);
        return pivote;
    }

    private static void ordenarRapido(int inicio, int fin, int criterio) {
        if (inicio >= fin) {
            return;
        } else {
            int pivote = ubicarPivote(inicio, fin, criterio);
            ordenarRapido(inicio, pivote - 1, criterio);
            ordenarRapido(pivote + 1, fin, criterio);
        }
    }

    public static void ordenarRapido(int criterio) {
        ordenarRapido(0, documentos.size() - 1, criterio);
    }

    // ********************** Ordenamiento por Inserción **********************
    public static void ordenarInsercion(int criterio) {
        for (int i = 1; i < documentos.size(); i++) {
            var actual = documentos.get(i);
            int j = i - 1;
            while (j >= 0 && esMayor(documentos.get(j), actual, criterio)) {
                documentos.set(j + 1, documentos.get(j));
                j--;
            }
            documentos.set(j + 1, actual);
        }
    }

    // ********************** Ordenamiento por Mezcla **********************
    private static void mezclar(int inicio, int mitad, int fin, int criterio) {
        List<Documento> temporal = new ArrayList<>();
        int i = inicio;
        int j = mitad + 1;

        while (i <= mitad && j <= fin) {
            if (!esMayor(documentos.get(i), documentos.get(j), criterio)) {
                temporal.add(documentos.get(i++));
            } else {
                temporal.add(documentos.get(j++));
            }
        }

        while (i <= mitad) {
            temporal.add(documentos.get(i++));
        }

        while (j <= fin) {
            temporal.add(documentos.get(j++));
        }

        for (int k = 0; k < temporal.size(); k++) {
            documentos.set(inicio + k, temporal.get(k));
        }
    }

    private static void ordenarMezcla(int inicio, int fin, int criterio) {
        if (inicio >= fin) {
            return;
        }
        int mitad = inicio + (fin - inicio) / 2;
        ordenarMezcla(inicio, mitad, criterio);
        ordenarMezcla(mitad + 1, fin, criterio);
        mezclar(inicio, mitad, fin, criterio);
    }

    public static void ordenarMezcla(int criterio) {
        if (documentos != null && documentos.size() > 1) {
            ordenarMezcla(0, documentos.size() - 1, criterio);
        }
    }

}
