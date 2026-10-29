package utn.tpi.sgsa;

public class Sol {
    public int id;
    public Est est;
    public Resp resp;
    public String t; // "EQUIV", "CERT", "EXCEP", "BAJA"
    public int p; // 1=ALTA, 2=MEDIA, 3=BAJA
    public int e; // 1=PENDIENTE, 2=EN_REVISION, 3=APROBADA, 4=RECHAZADA
    public String desc;
    public String f;
    public String obs;

    public Sol(int id, Est est, Resp resp, String t, int p, int e, String desc, String f, String obs) {
        this.id = id;
        this.est = est;
        this.resp = resp;
        this.t = t;
        this.p = p;
        this.e = e;
        this.desc = desc;
        this.f = f;
        this.obs = obs;
    }
}
