package com.sharpdroid.registroelettronico.SharpLibrary.Classi;

import java.util.Calendar;

import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ConvertiInVoto;

public class Voto {
    private String tipo;
    private String data;
    private String periodo;
    private String voto;
    private Double votod;
    private String commento;
    public static final String P1 = "q1";
    public static final String P2 = "q3";
    private boolean votoblu = false;

    public Voto() {
    }

    public void setVoto(String voto) {

        this.voto = voto;
        votod = ConvertiInVoto(voto);
    }

    public void setVoto(double voto) {
        this.votod = voto;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setData(String data) {

        Calendar c = Calendar.getInstance();
        int mm = Integer.parseInt(data.split("/")[1]);
        int cm = c.get(Calendar.MONTH);
        int cy = c.get(Calendar.YEAR);

        if (cm >= Calendar.SEPTEMBER && cm <= Calendar.DECEMBER)
            data += "/" + String.valueOf(mm >= Calendar.JANUARY && mm <= Calendar.AUGUST ? cy + 1 : cy);
        else
            data += "/" + String.valueOf(mm >= Calendar.JANUARY && mm <= Calendar.AUGUST ? cy : cy - 1);

        this.data = data;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getPeriodo() {
        return periodo;
    }

    public boolean isVotoblu() {
        return !votoblu;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }

    public void setVotoblu(boolean votoblu) {
        this.votoblu = votoblu;
    }

    public String getVoto() {
        return this.voto;
    }

    public double getVotod() {
        return this.votod;
    }

    public String getTipo() {
        return this.tipo;
    }

    public String getData() {
        return this.data;
    }

    public String getCommento() {
        return commento;
    }

    @Override
    public String toString() {
        return String.format("Voto: %1$s | Tipo: %2$s | Data: %3$s | Periodo: %4$s |Blu?: %5$s", voto, tipo, data, periodo, votoblu);
    }

}
