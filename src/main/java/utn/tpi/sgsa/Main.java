package utn.tpi.sgsa;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--demo")) {
            ejecutarDemo();
            return;
        }

        SistemaSolicitudes.init();
        Scanner sc = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("\n=== SISTEMA DE GESTION DE SOLICITUDES ACADEMICAS (SGSA) ===");
            System.out.println("1. Registrar nueva solicitud");
            System.out.println("2. Asignar area responsable");
            System.out.println("3. Cambiar estado de solicitud");
            System.out.println("4. Buscar / Filtrar solicitudes");
            System.out.println("5. Mostrar reporte general");
            System.out.println("6. Ejecutar escenario de demostracion");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");

            String op = sc.nextLine();
            switch (op) {
                case "1":
                    System.out.print("Legajo estudiante: ");
                    String leg = sc.nextLine();
                    System.out.print("Nombre completo: ");
                    String nom = sc.nextLine();
                    System.out.print("Email: ");
                    String mail = sc.nextLine();
                    System.out.print("Carrera: ");
                    String carr = sc.nextLine();
                    System.out.print("Tipo (EQUIV / CERT / EXCEP / BAJA): ");
                    String tipo = sc.nextLine().toUpperCase();
                    System.out.print("Descripcion del pedido: ");
                    String desc = sc.nextLine();
                    System.out.print("Es urgente? (S/N): ");
                    boolean urg = sc.nextLine().equalsIgnoreCase("S");
                    SistemaSolicitudes.reg(leg, nom, mail, carr, tipo, desc, urg);
                    break;
                case "2":
                    System.out.print("ID de solicitud: ");
                    int idAsig = Integer.parseInt(sc.nextLine());
                    System.out.print("Codigo de area (A01=Alumnos, A02=Academica, A03=Dir. TUP): ");
                    String area = sc.nextLine();
                    SistemaSolicitudes.asig(idAsig, area);
                    break;
                case "3":
                    System.out.print("ID de solicitud: ");
                    int idEst = Integer.parseInt(sc.nextLine());
                    System.out.print("Nuevo estado (2=EN_REVISION, 3=APROBADA, 4=RECHAZADA): ");
                    int nEst = Integer.parseInt(sc.nextLine());
                    System.out.print("Comentario / Resolucion: ");
                    String com = sc.nextLine();
                    SistemaSolicitudes.cambEst(idEst, nEst, com);
                    break;
                case "4":
                    System.out.print("Filtrar por Tipo (ENTER para todos): ");
                    String fTipo = sc.nextLine();
                    System.out.print("Filtrar por Estado (0 para todos, 1=PEND, 2=REV, 3=APROB, 4=RECH): ");
                    String fEstStr = sc.nextLine();
                    int fEst = fEstStr.isEmpty() ? 0 : Integer.parseInt(fEstStr);
                    System.out.print("Filtrar por Legajo (ENTER para todos): ");
                    String fLeg = sc.nextLine();
                    List<Sol> encontrados = SistemaSolicitudes.buscar(fTipo, fEst, fLeg);
                    System.out.println("Resultados encontrados: " + encontrados.size());
                    for (Sol s : encontrados) {
                        System.out.println(" -> #" + s.id + " | " + s.t + " | Legajo: " + s.est.leg + " | Desc: " + s.desc);
                    }
                    break;
                case "5":
                    System.out.println(SistemaSolicitudes.generarReporte());
                    break;
                case "6":
                    ejecutarDemo();
                    break;
                case "0":
                    salir = true;
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    public static void ejecutarDemo() {
        SistemaSolicitudes.arch = "solicitudes_demo.csv";
        new File(SistemaSolicitudes.arch).delete();
        SistemaSolicitudes.init();

        System.out.println(">>> INICIANDO DEMOSTRACION FUNCIONAL DEL SISTEMA BASE <<<\n");

        // 1. Registrar solicitudes
        Sol s1 = SistemaSolicitudes.reg("50123", "Ana Martinez", "ana.martinez@alumno.utn.edu.ar", "TUP",
                "EQUIV", "Solicitud de equivalencia de Programacion I cursada en ISI", false);
        Sol s2 = SistemaSolicitudes.reg("50456", "Juan Cruz", "juan.cruz@alumno.utn.edu.ar", "TUP",
                "EXCEP", "Excepcion de correlatividad para cursar Metodologia de Sistemas II", true);
        Sol s3 = SistemaSolicitudes.reg("50789", "Lucia Fernandez", "lucia.f@alumno.utn.edu.ar", "TUP",
                "CERT", "Certificado de alumno regular para presentar en el trabajo", false);
        Sol s4 = SistemaSolicitudes.reg("50123", "Ana Martinez", "ana.martinez@alumno.utn.edu.ar", "TUP",
                "BAJA", "Baja temporal de la materia Ingles II por superposicion horaria", false);

        // 2. Asignar responsables
        SistemaSolicitudes.asig(s1.id, "A03"); // Direccion TUP
        SistemaSolicitudes.asig(s2.id, "A02"); // Secretaria Academica
        SistemaSolicitudes.asig(s3.id, "A01"); // Depto Alumnos

        // 3. Cambiar estados
        SistemaSolicitudes.cambEst(s1.id, 3, "Se aprueba equivalencia segun resolucion 142/26");
        SistemaSolicitudes.cambEst(s2.id, 4, "No cumple con el porcentaje minimo de materias aprobadas");

        // 4. Buscar / Filtrar solicitudes de Ana Martinez
        List<Sol> deAna = SistemaSolicitudes.buscar("", 0, "50123");
        System.out.println("\n[BUSQUEDA] Solicitudes del legajo 50123 encontradas: " + deAna.size());

        // 5. Generar reporte
        System.out.println("\n" + SistemaSolicitudes.generarReporte());

        new File(SistemaSolicitudes.arch).delete();
    }
}
