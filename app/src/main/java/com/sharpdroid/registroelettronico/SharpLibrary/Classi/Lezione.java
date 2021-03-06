package com.sharpdroid.registroelettronico.SharpLibrary.Classi;

public class Lezione {

    private String prof;
    private String data;
    private String descrizione;

    public Lezione()
    {

    }

    public String getProf() {
        return prof;
    }

    public String getData() {
        return data;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setProf(String prof) {
        this.prof = prof;
    }

    @Override
    public String toString() {
        return String.format("[%1$s] %2$s: %3$s", data, prof, descrizione);
    }
}
