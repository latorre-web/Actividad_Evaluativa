import java.util.Scanner;
class Servidor {
    String nombre;
    String origen;
    String critico;
    int intentosTotales;
    int intentosFallidos;

    // Comprueba todas las reglas de validación y el trim quita espacios
    boolean datosValidos() {
        if (nombre.trim().isEmpty()) {
            return false;
        }
        String o = origen.trim();
        if (!o.equalsIgnoreCase("interno") && !o.equalsIgnoreCase("externo")) {
            return false;
        }
        String c = critico.trim();
        if (!c.equalsIgnoreCase("si") && !c.equalsIgnoreCase("no")) {
            return false;
        }
        if (intentosTotales <= 0) {
            return false;
        }
        if (intentosFallidos < 0 || intentosFallidos > intentosTotales) {
            return false;
        }
        return true;
    }
    // El orden de los if respeta la prioridad
    String evaluarEstado() {
        if (!datosValidos()) {
            return "DATOS INVALIDOS";
        }
        boolean esExterno = origen.trim().equalsIgnoreCase("externo");
        boolean esCritico = critico.trim().equalsIgnoreCase("si");

        if (intentosFallidos >= 5 || (esCritico && esExterno && intentosFallidos >= 3)) {
            return "BLOQUEADO";
        }
        if ((intentosFallidos >= 3 && intentosFallidos <= 4) || (esExterno && intentosFallidos >= 2)) {
            return "ALERTA";
        }
        return "NORMAL";
    }

    double porcentajeFallos() {
        return intentosFallidos * 100.0 / intentosTotales;
    }
    // Convierte el estado en un número para poder comparar servidores
    int nivelPrioridad() {
        String estado = evaluarEstado();
        if (estado.equals("BLOQUEADO")) {
            return 3;
        } else if (estado.equals("ALERTA")) {
            return 2;
        } else if (estado.equals("NORMAL")) {
            return 1;
        }
        return 0; // los datos son invalidos
    }

  
    void mostrarResumen() {
        String porcentaje = "N/D";
        // Solo se divide si los datos son válidos
        if (datosValidos()) {
            double redondeado = Math.round(porcentajeFallos() * 10) / 10.0;
            porcentaje = redondeado + "%";
        }
        System.out.printf("%-6s | %d/%d | %s | %s%n",
                nombre.trim(), intentosFallidos, intentosTotales, porcentaje, evaluarEstado());
    }
}

public class Main {

    // Pide los cinco datos de un servidor y devuelve el objeto creado
    static Servidor leerServidor(Scanner sc, int numero) {
        Servidor s = new Servidor();
        System.out.println("--- Servidor " + numero + " ---");
        System.out.print("Nombre: ");
        s.nombre = sc.nextLine();
        System.out.print("Origen (interno/externo): ");
        s.origen = sc.nextLine();
        System.out.print("Critico (si/no): ");
        s.critico = sc.nextLine();
        System.out.print("Intentos totales: ");
        s.intentosTotales = sc.nextInt();
        System.out.print("Intentos fallidos: ");
        s.intentosFallidos = sc.nextInt();
        sc.nextLine(); // limpia el salto de línea que deja nextInt()
        return s;
    }

  
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Servidor servidor1 = leerServidor(sc, 1);
        Servidor servidor2 = leerServidor(sc, 2);

        System.out.println("========== REPORTE DE SEGURIDAD ==========");
        servidor1.mostrarResumen();
        servidor2.mostrarResumen();

        int nivel1 = servidor1.nivelPrioridad();
        int nivel2 = servidor2.nivelPrioridad();

        if (nivel1 == 0 || nivel2 == 0) {
            System.out.println("COMPARACION NO DISPONIBLE");
        } else if (nivel1 == nivel2) {
            System.out.println("IGUAL PRIORIDAD");
        } else if (nivel1 > nivel2) {
            System.out.println("ATENCION PRIORITARIA: " + servidor1.nombre.trim());
        } else {
            System.out.println("ATENCION PRIORITARIA: " + servidor2.nombre.trim());
        }

        sc.close();
    }
}




/*
 PREGUNTAS CONCEPTUALES
 
  1. ¿Por qué dos objetos de la misma clase pueden almacenar valores diferentes en sus atributos?
    Porque los atributos son de instancia, cada vez que hacemos new se reserva
    memoria nueva para ese objeto. Así, servidor1 y servidor2 tienen cada uno
    su propio nombre y origen, aunque los dos sigan la misma clase.
 
  2. ¿Qué diferencia existe entre Servidor b = a; y Servidor b = new Servidor();?
    Con b = a solo se copia la referencia, así que a y b son el mismo objeto
    con dos nombres distintos, si modifico b.nombre, también cambia a.nombre.
    Con new Servidor() se crea un objeto distinto en memoria y los cambios
    en b no afectan en a.
 
  3. ¿Por qué es importante comprobar datosValidos() antes de calcular el porcentaje?
    Para no hacer cálculos con datos incorrectos. Si los intentos totales son 0
    habría una división entre cero, y si hay más fallidos que totales el
    porcentaje pasaría del 100% y esto no tendría sentido. Por eso, con datos
    inválidos se muestra N/D.
*/
