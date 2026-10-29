package utn.tpi.sgsa.repository;

import utn.tpi.sgsa.domain.*;
import utn.tpi.sgsa.exception.ReglaNegocioException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class CsvSolicitudRepository extends InMemorySolicitudRepository {
    private static final String SEPARADOR = ";";
    private static final String VALOR_NULO = "NULL";

    private final File archivoCsv;
    private final AreaResponsableRepository areaRepository;

    public CsvSolicitudRepository(String rutaArchivo, AreaResponsableRepository areaRepository) {
        this.archivoCsv = new File(rutaArchivo);
        this.areaRepository = areaRepository;
        cargarDesdeArchivo();
    }

    @Override
    public void guardar(Solicitud solicitud) {
        super.guardar(solicitud);
        persistirEnArchivo();
    }

    private void persistirEnArchivo() {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(archivoCsv), StandardCharsets.UTF_8))) {
            for (Solicitud s : almacenamiento.values()) {
                writer.println(serializarSolicitud(s));
            }
        } catch (IOException ex) {
            throw new ReglaNegocioException("Error al guardar las solicitudes en el archivo CSV: " + archivoCsv.getName(), ex);
        }
    }

    private String serializarSolicitud(Solicitud s) {
        String codigoArea = s.tieneResponsableAsignado() ? s.getAreaResponsable().getCodigo() : VALOR_NULO;
        Estudiante est = s.getEstudiante();
        return String.join(SEPARADOR,
                String.valueOf(s.getId()),
                est.getLegajo(),
                est.getNombreCompleto(),
                est.getEmail(),
                est.getCarrera(),
                codigoArea,
                s.getTipo().getCodigo(),
                String.valueOf(s.getPrioridad().getCodigo()),
                String.valueOf(s.getEstado().getCodigo()),
                sanitizarTexto(s.getDescripcion()),
                s.getFechaCreacion().toString(),
                sanitizarTexto(s.getHistorialObservaciones())
        );
    }

    private void cargarDesdeArchivo() {
        if (!archivoCsv.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(archivoCsv), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                Solicitud solicitud = deserializarSolicitud(linea);
                super.guardar(solicitud);
            }
        } catch (IOException | RuntimeException ex) {
            throw new ReglaNegocioException("Error al leer el archivo de persistencia CSV: " + archivoCsv.getName(), ex);
        }
    }

    private Solicitud deserializarSolicitud(String linea) {
        String[] partes = linea.split(SEPARADOR);
        int id = Integer.parseInt(partes[0]);
        Estudiante estudiante = new Estudiante(partes[1], partes[2], partes[3], partes[4]);
        AreaResponsable area = VALOR_NULO.equals(partes[5])
                ? null
                : areaRepository.buscarPorCodigo(partes[5]).orElse(null);
        TipoSolicitud tipo = TipoSolicitud.desdeCodigo(partes[6]);
        Prioridad prioridad = Prioridad.desdeCodigo(Integer.parseInt(partes[7]));
        EstadoSolicitud estado = EstadoSolicitud.desdeCodigo(Integer.parseInt(partes[8]));
        String descripcion = partes[9];
        LocalDate fecha = LocalDate.parse(partes[10]);
        String historial = partes.length > 11 ? partes[11] : "";

        return new Solicitud(id, estudiante, area, tipo, prioridad, estado, descripcion, fecha, historial);
    }

    private String sanitizarTexto(String texto) {
        return texto == null ? "" : texto.replace(SEPARADOR, ",");
    }
}
