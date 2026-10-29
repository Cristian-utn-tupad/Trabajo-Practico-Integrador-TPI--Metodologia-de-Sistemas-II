package utn.tpi.sgsa;

import utn.tpi.sgsa.domain.EstadoSolicitud;
import utn.tpi.sgsa.domain.Solicitud;
import utn.tpi.sgsa.domain.TipoSolicitud;
import utn.tpi.sgsa.dto.CriterioBusquedaSolicitud;
import utn.tpi.sgsa.dto.DatosRegistroSolicitud;
import utn.tpi.sgsa.exception.ReglaNegocioException;
import utn.tpi.sgsa.repository.AreaResponsableRepository;
import utn.tpi.sgsa.repository.CsvSolicitudRepository;
import utn.tpi.sgsa.repository.InMemoryAreaResponsableRepository;
import utn.tpi.sgsa.repository.SolicitudRepository;
import utn.tpi.sgsa.service.ConsolaEmailNotificador;
import utn.tpi.sgsa.service.GeneradorReporteSolicitudes;
import utn.tpi.sgsa.service.GestionSolicitudesService;
import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String ARCHIVO_PRODUCCION = "solicitudes.csv";
    private static final String ARCHIVO_DEMO = "solicitudes_demo.csv";

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--demo")) {
            ejecutarDemo();
            return;
        }

        GestionSolicitudesService servicio = construirServicioConPersistenciaCsv(ARCHIVO_PRODUCCION);
        iniciarMenuInteractivo(servicio);
    }

    private static GestionSolicitudesService construirServicioConPersistenciaCsv(String nombreArchivo) {
        AreaResponsableRepository areaRepository = new InMemoryAreaResponsableRepository();
        SolicitudRepository solicitudRepository = new CsvSolicitudRepository(nombreArchivo, areaRepository);
        return new GestionSolicitudesService(
                solicitudRepository,
                areaRepository,
                new ConsolaEmailNotificador(),
                new GeneradorReporteSolicitudes()
        );
    }

    private static void iniciarMenuInteractivo(GestionSolicitudesService servicio) {
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

            String opcion = sc.nextLine().trim();
            try {
                switch (opcion) {
                    case "1" -> opcionRegistrarSolicitud(sc, servicio);
                    case "2" -> opcionAsignarResponsable(sc, servicio);
                    case "3" -> opcionCambiarEstado(sc, servicio);
                    case "4" -> opcionBuscarSolicitudes(sc, servicio);
                    case "5" -> System.out.println(servicio.emitirReporteGeneral());
                    case "6" -> ejecutarDemo();
                    case "0" -> salir = true;
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (ReglaNegocioException | NumberFormatException ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
        }
    }

    private static void opcionRegistrarSolicitud(Scanner sc, GestionSolicitudesService servicio) {
        System.out.print("Legajo estudiante: ");
        String legajo = sc.nextLine();
        System.out.print("Nombre completo: ");
        String nombre = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Carrera: ");
        String carrera = sc.nextLine();
        System.out.print("Tipo (EQUIV / CERT / EXCEP / BAJA): ");
        String tipo = sc.nextLine().toUpperCase();
        System.out.print("Descripcion del pedido: ");
        String descripcion = sc.nextLine();
        System.out.print("Es urgente? (S/N): ");
        boolean urgente = sc.nextLine().equalsIgnoreCase("S");

        DatosRegistroSolicitud datos = DatosRegistroSolicitud.crear(
                legajo, nombre, email, carrera, tipo, descripcion, urgente);
        servicio.registrarSolicitud(datos);
    }

    private static void opcionAsignarResponsable(Scanner sc, GestionSolicitudesService servicio) {
        System.out.print("ID de solicitud: ");
        int idSolicitud = Integer.parseInt(sc.nextLine());
        System.out.print("Codigo de area (A01=Alumnos, A02=Academica, A03=Dir. TUP): ");
        String codigoArea = sc.nextLine();
        servicio.asignarResponsable(idSolicitud, codigoArea);
    }

    private static void opcionCambiarEstado(Scanner sc, GestionSolicitudesService servicio) {
        System.out.print("ID de solicitud: ");
        int idSolicitud = Integer.parseInt(sc.nextLine());
        System.out.print("Nuevo estado (2=EN_REVISION, 3=APROBADA, 4=RECHAZADA): ");
        int codigoEstado = Integer.parseInt(sc.nextLine());
        System.out.print("Comentario / Resolucion: ");
        String comentario = sc.nextLine();
        servicio.cambiarEstado(idSolicitud, EstadoSolicitud.desdeCodigo(codigoEstado), comentario);
    }

    private static void opcionBuscarSolicitudes(Scanner sc, GestionSolicitudesService servicio) {
        System.out.print("Filtrar por Tipo (ENTER para todos): ");
        String fTipoStr = sc.nextLine().trim();
        TipoSolicitud fTipo = fTipoStr.isEmpty() ? null : TipoSolicitud.desdeCodigo(fTipoStr);

        System.out.print("Filtrar por Estado (0 para todos, 1=PEND, 2=REV, 3=APROB, 4=RECH): ");
        String fEstStr = sc.nextLine().trim();
        int codigoEstado = fEstStr.isEmpty() ? 0 : Integer.parseInt(fEstStr);
        EstadoSolicitud fEstado = codigoEstado == 0 ? null : EstadoSolicitud.desdeCodigo(codigoEstado);

        System.out.print("Filtrar por Legajo (ENTER para todos): ");
        String fLegajo = sc.nextLine().trim();

        CriterioBusquedaSolicitud criterio = new CriterioBusquedaSolicitud(fTipo, fEstado, fLegajo);
        List<Solicitud> encontrados = servicio.buscarSolicitudes(criterio);
        System.out.println("Resultados encontrados: " + encontrados.size());
        for (Solicitud s : encontrados) {
            System.out.println(" -> #" + s.getId() + " | " + s.getTipo().getCodigo()
                    + " | Legajo: " + s.getEstudiante().getLegajo()
                    + " | Desc: " + s.getDescripcion());
        }
    }

    public static void ejecutarDemo() {
        File archivoTemporal = new File(ARCHIVO_DEMO);
        archivoTemporal.delete();

        GestionSolicitudesService servicio = construirServicioConPersistenciaCsv(ARCHIVO_DEMO);

        System.out.println(">>> INICIANDO DEMOSTRACION FUNCIONAL DEL SISTEMA BASE <<<\n");

        // 1. Registrar solicitudes utilizando Parameter Object (DatosRegistroSolicitud)
        Solicitud s1 = servicio.registrarSolicitud(DatosRegistroSolicitud.crear(
                "50123", "Ana Martinez", "ana.martinez@alumno.utn.edu.ar", "TUP",
                "EQUIV", "Solicitud de equivalencia de Programacion I cursada en ISI", false));
        Solicitud s2 = servicio.registrarSolicitud(DatosRegistroSolicitud.crear(
                "50456", "Juan Cruz", "juan.cruz@alumno.utn.edu.ar", "TUP",
                "EXCEP", "Excepcion de correlatividad para cursar Metodologia de Sistemas II", true));
        Solicitud s3 = servicio.registrarSolicitud(DatosRegistroSolicitud.crear(
                "50789", "Lucia Fernandez", "lucia.f@alumno.utn.edu.ar", "TUP",
                "CERT", "Certificado de alumno regular para presentar en el trabajo", false));
        Solicitud s4 = servicio.registrarSolicitud(DatosRegistroSolicitud.crear(
                "50123", "Ana Martinez", "ana.martinez@alumno.utn.edu.ar", "TUP",
                "BAJA", "Baja temporal de la materia Ingles II por superposicion horaria", false));

        // 2. Asignar responsables
        servicio.asignarResponsable(s1.getId(), "A03"); // Direccion TUP
        servicio.asignarResponsable(s2.getId(), "A02"); // Secretaria Academica
        servicio.asignarResponsable(s3.getId(), "A01"); // Depto Alumnos

        // 3. Cambiar estados
        servicio.cambiarEstado(s1.getId(), EstadoSolicitud.APROBADA,
                "Se aprueba equivalencia segun resolucion 142/26");
        servicio.cambiarEstado(s2.getId(), EstadoSolicitud.RECHAZADA,
                "No cumple con el porcentaje minimo de materias aprobadas");

        // 4. Buscar / Filtrar solicitudes de Ana Martinez
        List<Solicitud> deAna = servicio.buscarSolicitudes(new CriterioBusquedaSolicitud(null, null, "50123"));
        System.out.println("\n[BUSQUEDA] Solicitudes del legajo 50123 encontradas: " + deAna.size());

        // 5. Generar reporte
        System.out.println("\n" + servicio.emitirReporteGeneral());

        archivoTemporal.delete();
    }
}
