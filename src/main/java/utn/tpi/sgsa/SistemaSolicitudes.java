package utn.tpi.sgsa;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class SistemaSolicitudes {

    public static List<Sol> lista = new ArrayList<>();
    public static List<Resp> areas = new ArrayList<>();
    public static int ultId = 100;
    public static String arch = "solicitudes.csv";

    public static void init() {
        lista.clear();
        areas.clear();
        ultId = 100;
        areas.add(new Resp("A01", "Departamento de Alumnos", "Laura Gomez", "alumnos@utn.edu.ar"));
        areas.add(new Resp("A02", "Secretaria Academica", "Carlos Ruiz", "academica@utn.edu.ar"));
        areas.add(new Resp("A03", "Direccion de Carrera TUP", "Martin Perez", "tup@utn.edu.ar"));
        cargar();
    }

    // Long parameter list (7 parametros), Long method, Magic numbers, Duplicate code
    public static Sol reg(String leg, String nom, String mail, String carr, String tipo, String desc, boolean esUrg) {
        if (leg == null || leg.trim().isEmpty()) {
            System.out.println("ERROR: Legajo vacio");
            return null;
        }
        if (nom == null || nom.trim().isEmpty()) {
            System.out.println("ERROR: Nombre vacio");
            return null;
        }
        if (mail == null || !mail.contains("@")) {
            System.out.println("ERROR: Email invalido");
            return null;
        }
        if (desc == null || desc.trim().length() < 5) {
            System.out.println("ERROR: Descripcion muy corta");
            return null;
        }
        if (!tipo.equals("EQUIV") && !tipo.equals("CERT") && !tipo.equals("EXCEP") && !tipo.equals("BAJA")) {
            System.out.println("ERROR: Tipo de solicitud invalido");
            return null;
        }

        // Calculo de prioridad con numeros magicos (1=ALTA, 2=MEDIA, 3=BAJA)
        int prio = 3;
        if (esUrg) {
            prio = 1;
        } else {
            if (tipo.equals("EXCEP")) {
                prio = 1;
            } else if (tipo.equals("EQUIV")) {
                prio = 2;
            } else if (tipo.equals("CERT")) {
                prio = 3;
            } else if (tipo.equals("BAJA")) {
                prio = 2;
            }
        }

        ultId++;
        Est e = new Est(leg, nom, mail, carr);
        String fecha = LocalDate.now().toString();
        // Estado 1 = PENDIENTE
        Sol s = new Sol(ultId, e, null, tipo, prio, 1, desc, fecha, "Creada en fecha " + fecha);
        lista.add(s);
        guardar();
        System.out.println("[NOTIF EMAIL -> " + e.mail + "] Solicitud #" + s.id + " registrada con exito.");
        return s;
    }

    public static boolean asig(int idSol, String codArea) {
        Sol encontrada = null;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).id == idSol) {
                encontrada = lista.get(i);
                break;
            }
        }
        if (encontrada == null) {
            System.out.println("ERROR: No existe la solicitud #" + idSol);
            return false;
        }
        // Si ya esta aprobada (3) o rechazada (4) no se puede reasignar
        if (encontrada.e == 3 || encontrada.e == 4) {
            System.out.println("ERROR: No se puede asignar responsable a una solicitud cerrada.");
            return false;
        }

        Resp r = null;
        for (int i = 0; i < areas.size(); i++) {
            if (areas.get(i).cod.equalsIgnoreCase(codArea)) {
                r = areas.get(i);
                break;
            }
        }
        if (r == null) {
            System.out.println("ERROR: Area responsable no encontrada: " + codArea);
            return false;
        }

        encontrada.resp = r;
        if (encontrada.e == 1) {
            encontrada.e = 2; // Pasa automaticamente de PENDIENTE (1) a EN_REVISION (2)
        }
        encontrada.obs = encontrada.obs + " | Asignada a " + r.nomArea + " (" + r.respNom + ")";
        guardar();
        System.out.println("[NOTIF EMAIL -> " + r.emailArea + "] Nueva solicitud #" + encontrada.id + " asignada a su area.");
        return true;
    }

    public static boolean cambEst(int idSol, int nuevoEst, String coment) {
        Sol encontrada = null;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).id == idSol) {
                encontrada = lista.get(i);
                break;
            }
        }
        if (encontrada == null) {
            System.out.println("ERROR: No existe la solicitud #" + idSol);
            return false;
        }

        // Reglas de transicion de estado acopladas con numeros magicos
        // 1=PENDIENTE, 2=EN_REVISION, 3=APROBADA, 4=RECHAZADA
        if (encontrada.e == 3 || encontrada.e == 4) {
            System.out.println("ERROR: La solicitud ya se encuentra cerrada y no admite cambios.");
            return false;
        }
        if (nuevoEst < 1 || nuevoEst > 4) {
            System.out.println("ERROR: Codigo de estado invalido.");
            return false;
        }
        if ((nuevoEst == 3 || nuevoEst == 4) && encontrada.resp == null) {
            System.out.println("ERROR: No se puede aprobar o rechazar sin responsable asignado.");
            return false;
        }
        if (nuevoEst == 4 && (coment == null || coment.trim().isEmpty())) {
            System.out.println("ERROR: Para rechazar una solicitud debe indicar un motivo.");
            return false;
        }

        encontrada.e = nuevoEst;
        if (coment != null && !coment.trim().isEmpty()) {
            encontrada.obs = encontrada.obs + " | Cambio estado (" + nuevoEst + "): " + coment;
        }
        guardar();
        System.out.println("[NOTIF EMAIL -> " + encontrada.est.mail + "] Su solicitud #" + encontrada.id + " cambio de estado.");
        return true;
    }

    public static List<Sol> buscar(String filtroTipo, int filtroEst, String legajoEst) {
        List<Sol> res = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            Sol s = lista.get(i);
            boolean ok = true;
            if (filtroTipo != null && !filtroTipo.isEmpty() && !s.t.equalsIgnoreCase(filtroTipo)) {
                ok = false;
            }
            if (filtroEst > 0 && s.e != filtroEst) {
                ok = false;
            }
            if (legajoEst != null && !legajoEst.isEmpty() && !s.est.leg.equalsIgnoreCase(legajoEst)) {
                ok = false;
            }
            if (ok) {
                res.add(s);
            }
        }
        return res;
    }

    public static String generarReporte() {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("        REPORTE GENERAL DE SOLICITUDES ACADEMICAS           \n");
        sb.append("============================================================\n");

        int tot = lista.size();
        int cPend = 0, cRev = 0, cAprob = 0, cRech = 0;
        int pAlta = 0, pMedia = 0, pBaja = 0;

        for (Sol s : lista) {
            if (s.e == 1) cPend++;
            else if (s.e == 2) cRev++;
            else if (s.e == 3) cAprob++;
            else if (s.e == 4) cRech++;

            if (s.p == 1) pAlta++;
            else if (s.p == 2) pMedia++;
            else if (s.p == 3) pBaja++;

            // Feature Envy + Switch/If repetitivo para traducir codigos
            String nomEst = "DESCONOCIDO";
            if (s.e == 1) nomEst = "PENDIENTE";
            else if (s.e == 2) nomEst = "EN_REVISION";
            else if (s.e == 3) nomEst = "APROBADA";
            else if (s.e == 4) nomEst = "RECHAZADA";

            String nomPrio = "BAJA";
            if (s.p == 1) nomPrio = "ALTA";
            else if (s.p == 2) nomPrio = "MEDIA";
            else if (s.p == 3) nomPrio = "BAJA";

            String respStr = (s.resp != null) ? s.resp.nomArea + " (" + s.resp.respNom + ")" : "SIN ASIGNAR";

            sb.append(String.format("#%d | [%s] | Tipo: %-5s | Prio: %-5s | Est: %s (%s) | Resp: %s\n",
                    s.id, s.f, s.t, nomPrio, s.est.nom, s.est.leg, respStr));
            sb.append("   Estado: ").append(nomEst).append(" | Desc: ").append(s.desc).append("\n");
            sb.append("   Historial: ").append(s.obs).append("\n");
            sb.append("------------------------------------------------------------\n");
        }

        sb.append("RESUMEN POR ESTADO:\n");
        sb.append(" - Total Solicitudes: ").append(tot).append("\n");
        sb.append(" - Pendientes       : ").append(cPend).append("\n");
        sb.append(" - En Revision      : ").append(cRev).append("\n");
        sb.append(" - Aprobadas        : ").append(cAprob).append("\n");
        sb.append(" - Rechazadas       : ").append(cRech).append("\n");
        sb.append("RESUMEN POR PRIORIDAD:\n");
        sb.append(" - Prioridad Alta   : ").append(pAlta).append("\n");
        sb.append(" - Prioridad Media  : ").append(pMedia).append("\n");
        sb.append(" - Prioridad Baja   : ").append(pBaja).append("\n");
        sb.append("============================================================\n");
        return sb.toString();
    }

    public static void guardar() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(arch));
            for (Sol s : lista) {
                String codR = (s.resp != null) ? s.resp.cod : "NULL";
                pw.println(s.id + ";" + s.est.leg + ";" + s.est.nom + ";" + s.est.mail + ";" + s.est.carr + ";"
                        + codR + ";" + s.t + ";" + s.p + ";" + s.e + ";"
                        + s.desc.replace(";", ",") + ";" + s.f + ";" + s.obs.replace(";", ","));
            }
            pw.close();
        } catch (Exception ex) {
            // Bloque catch que oculta errores reales
        }
    }

    public static void cargar() {
        File f = new File(arch);
        if (!f.exists()) return;
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split(";");
                int id = Integer.parseInt(partes[0]);
                Est e = new Est(partes[1], partes[2], partes[3], partes[4]);
                Resp r = null;
                if (!partes[5].equals("NULL")) {
                    for (Resp a : areas) {
                        if (a.cod.equals(partes[5])) {
                            r = a;
                            break;
                        }
                    }
                }
                String t = partes[6];
                int p = Integer.parseInt(partes[7]);
                int est = Integer.parseInt(partes[8]);
                String desc = partes[9];
                String fecha = partes[10];
                String obs = partes.length > 11 ? partes[11] : "";
                lista.add(new Sol(id, e, r, t, p, est, desc, fecha, obs));
                if (id > ultId) ultId = id;
            }
            br.close();
        } catch (Exception ex) {
            // Ignora excepcion
        }
    }
}
