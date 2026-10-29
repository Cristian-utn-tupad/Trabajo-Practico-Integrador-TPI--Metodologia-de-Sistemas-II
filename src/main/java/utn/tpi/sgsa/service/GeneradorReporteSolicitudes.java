package utn.tpi.sgsa.service;

import utn.tpi.sgsa.domain.*;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class GeneradorReporteSolicitudes {
    private static final String LINEA_DOBLE = "============================================================\n";
    private static final String LINEA_SIMPLE = "------------------------------------------------------------\n";

    public String generarReporteGeneral(List<Solicitud> solicitudes) {
        StringBuilder sb = new StringBuilder();
        escribirEncabezado(sb);

        Map<EstadoSolicitud, Integer> conteoPorEstado = inicializarConteoEstados();
        Map<Prioridad, Integer> conteoPorPrioridad = inicializarConteoPrioridades();

        for (Solicitud solicitud : solicitudes) {
            conteoPorEstado.merge(solicitud.getEstado(), 1, Integer::sum);
            conteoPorPrioridad.merge(solicitud.getPrioridad(), 1, Integer::sum);
            escribirDetalleSolicitud(sb, solicitud);
        }

        escribirResumenEstadistico(sb, solicitudes.size(), conteoPorEstado, conteoPorPrioridad);
        return sb.toString();
    }

    private void escribirEncabezado(StringBuilder sb) {
        sb.append(LINEA_DOBLE);
        sb.append("        REPORTE GENERAL DE SOLICITUDES ACADEMICAS           \n");
        sb.append(LINEA_DOBLE);
    }

    private void escribirDetalleSolicitud(StringBuilder sb, Solicitud s) {
        String textoResponsable = s.tieneResponsableAsignado()
                ? s.getAreaResponsable().formatearResumen()
                : "SIN ASIGNAR";

        sb.append(String.format("#%d | [%s] | Tipo: %-5s | Prio: %-5s | Est: %s (%s) | Resp: %s\n",
                s.getId(),
                s.getFechaCreacion(),
                s.getTipo().getCodigo(),
                s.getPrioridad().getEtiqueta(),
                s.getEstudiante().getNombreCompleto(),
                s.getEstudiante().getLegajo(),
                textoResponsable));
        sb.append("   Estado: ").append(s.getEstado().getEtiqueta())
                .append(" | Desc: ").append(s.getDescripcion()).append("\n");
        sb.append("   Historial: ").append(s.getHistorialObservaciones()).append("\n");
        sb.append(LINEA_SIMPLE);
    }

    private void escribirResumenEstadistico(StringBuilder sb,
                                            int total,
                                            Map<EstadoSolicitud, Integer> porEstado,
                                            Map<Prioridad, Integer> porPrioridad) {
        sb.append("RESUMEN POR ESTADO:\n");
        sb.append(" - Total Solicitudes: ").append(total).append("\n");
        sb.append(" - Pendientes       : ").append(porEstado.get(EstadoSolicitud.PENDIENTE)).append("\n");
        sb.append(" - En Revision      : ").append(porEstado.get(EstadoSolicitud.EN_REVISION)).append("\n");
        sb.append(" - Aprobadas        : ").append(porEstado.get(EstadoSolicitud.APROBADA)).append("\n");
        sb.append(" - Rechazadas       : ").append(porEstado.get(EstadoSolicitud.RECHAZADA)).append("\n");
        sb.append("RESUMEN POR PRIORIDAD:\n");
        sb.append(" - Prioridad Alta   : ").append(porPrioridad.get(Prioridad.ALTA)).append("\n");
        sb.append(" - Prioridad Media  : ").append(porPrioridad.get(Prioridad.MEDIA)).append("\n");
        sb.append(" - Prioridad Baja   : ").append(porPrioridad.get(Prioridad.BAJA)).append("\n");
        sb.append(LINEA_DOBLE);
    }

    private Map<EstadoSolicitud, Integer> inicializarConteoEstados() {
        Map<EstadoSolicitud, Integer> mapa = new EnumMap<>(EstadoSolicitud.class);
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            mapa.put(estado, 0);
        }
        return mapa;
    }

    private Map<Prioridad, Integer> inicializarConteoPrioridades() {
        Map<Prioridad, Integer> mapa = new EnumMap<>(Prioridad.class);
        for (Prioridad prioridad : Prioridad.values()) {
            mapa.put(prioridad, 0);
        }
        return mapa;
    }
}
