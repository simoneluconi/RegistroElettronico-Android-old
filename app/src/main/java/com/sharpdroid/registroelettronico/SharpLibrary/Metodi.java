package com.sharpdroid.registroelettronico.SharpLibrary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;

import com.sharpdroid.registroelettronico.MainActivity;
import com.sharpdroid.registroelettronico.Notifiche;
import com.sharpdroid.registroelettronico.R;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Compito;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.MyAccount;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.MyDB;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.MyUsers;
import com.sharpdroid.registroelettronico.SharpLibrary.Classi.Nota;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Metodi {
    private static boolean VotoValido(String voto) {

        try {
            double v = Double.parseDouble(voto.trim());
            Log.v("VotoValido", String.valueOf(v));
            return true;
        } catch (Exception e) {
            Log.v("VotoValido", "Non valido: " + voto);
            return false;
        }
    }

    public static int ColorByMedia(float media) {
        if (media < 55f) {
            return R.color.redmaterial;
        } else if (media >= 55f && media < 60f) {
            return R.color.orangematerial;
        } else {
            return R.color.greenmaterial;
        }
    }

    public static int ColorByMedia(double media) {
        if (media < 5.5) {
            return R.color.redmaterial;
        } else if (media >= 5.5 && media < 6f) {
            return R.color.orangematerial;
        } else {
            return R.color.greenmaterial;
        }
    }

    public static String ConvertiDimensione(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format(Locale.ITALIAN, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public static String MateriaDecente(String materia) {
        return InizialeMaiuscola(materia.trim());
    }

    public static String ProfDecente(String prof) {
        String profd = "";
        String[] insV = prof.trim().split("\\s+");
        for (String ins : insV) {
            profd += ins.substring(0, 1).toUpperCase() + ins.substring(1).toLowerCase() + " ";
        }
        return profd;
    }

    public static String InizialeMaiuscola(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    public static List<Nota> ReadNote(Context context) {
        MyDB DBNote = new MyDB(context);
        SQLiteDatabase db = DBNote.getReadableDatabase();

        List<Nota> notes = new ArrayList<>();

        Cursor c = db.rawQuery("select * from " + MyDB.NotaEntry.TABLE_NAME, null);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                Nota nota = new Nota();
                nota.setProf(c.getString(c.getColumnIndex(MyDB.NotaEntry.COLUMN_NAME_AUTORE)));
                nota.setData(c.getString(c.getColumnIndex(MyDB.NotaEntry.COLUMN_NAME_DATA)));
                nota.setContenuto(c.getString(c.getColumnIndex(MyDB.NotaEntry.COLUMN_NAME_CONTENUTO)));
                nota.setTipo(c.getString(c.getColumnIndex(MyDB.NotaEntry.COLUMN_NAME_TIPO)));
                notes.add(nota);
                c.moveToNext();
            }

        }
        db.close();
        c.close();

        return notes;
    }

    public static List<Compito> ReadAgenda(Context context) {

        MyDB DBAgenda = new MyDB(context);

        SQLiteDatabase db = DBAgenda.getReadableDatabase();
        List<Compito> compitos = new ArrayList<>();

        Cursor c = db.rawQuery("select * from " + MyDB.CompitoEntry.TABLE_NAME, null);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                Compito compito = new Compito();
                boolean tuttoilgiorno = c.getInt(c.getColumnIndex(MyDB.CompitoEntry.COLUMN_NAME_TUTTOILGIORNO)) == 1;
                compito.setTuttoIlGiorno(tuttoilgiorno);
                compito.setAutore(c.getString(c.getColumnIndex(MyDB.CompitoEntry.COLUMN_NAME_AUTORE)));
                compito.setDataInizio(c.getString(c.getColumnIndex(MyDB.CompitoEntry.COLUMN_NAME_DATAINIZIO)));
                compito.setDataFine(c.getString(c.getColumnIndex(MyDB.CompitoEntry.COLUMN_NAME_DATAFINE)));
                compito.setDataInserimento(c.getString(c.getColumnIndex(MyDB.CompitoEntry.COLUMN_NAME_DATAINSERIMENTO)));
                compito.setContenuto(c.getString(c.getColumnIndex(MyDB.CompitoEntry.COLUMN_NAME_CONTENUTO)));
                compitos.add(compito);
                c.moveToNext();
            }
        }
        db.close();
        c.close();
        return compitos;
    }

    public static List<MyAccount> ReadAccounts(Context context) {
        List<MyAccount> Accounts = new ArrayList<>();
        MyUsers DBUser = new MyUsers(context);
        SQLiteDatabase db = DBUser.getReadableDatabase();

        Cursor c = db.rawQuery("select * from " + MyUsers.UserEntry.TABLE_NAME, null);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                MyAccount account = new MyAccount();
                account.setName(c.getString(c.getColumnIndex(MyUsers.UserEntry.COLUMN_NAME_NAME)));
                account.setCodicescuola(c.getString(c.getColumnIndex(MyUsers.UserEntry.COLUMN_NAME_CODICESCUOLA)));
                account.setUsername(c.getString(c.getColumnIndex(MyUsers.UserEntry.COLUMN_NAME_USERNAME)));
                Accounts.add(account);
                c.moveToNext();
            }

        }
        db.close();
        c.close();
        return Accounts;
    }

    public static List<Compito> ReadMyAgenda(Context context) {

        MyDB DBAgenda = new MyDB(context);
        SQLiteDatabase db = DBAgenda.getReadableDatabase();

        List<Compito> compitos = new ArrayList<>();

        Cursor c = db.rawQuery("select * from " + MyDB.MyCompitoEntry.TABLE_NAME, null);
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTimeFormatter dtfInserimento = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                Compito compito = new Compito();
                compito.setAutore(c.getString(c.getColumnIndex(MyDB.MyCompitoEntry.COLUMN_NAME_AUTORE)));
                compito.setDataInizio(dtf.parseDateTime(c.getString(c.getColumnIndex(MyDB.MyCompitoEntry.COLUMN_NAME_DATA))));
                compito.setDataFine(compito.getDataInizio());
                compito.setDataInserimento(dtfInserimento.parseDateTime(c.getString(c.getColumnIndex(MyDB.MyCompitoEntry.COLUMN_NAME_DATAINSERIMENTO))));
                compito.setContenuto(c.getString(c.getColumnIndex(MyDB.MyCompitoEntry.COLUMN_NAME_CONTENUTO)));
                compitos.add(compito);
                c.moveToNext();
            }

        }
        db.close();
        c.close();
        return compitos;
    }

    public static Compito ConvertiCompito(JSONObject compitoDati) {

        Compito compito = new Compito();

        try {
            String prof = compitoDati.getString("autore_desc").trim();
            if (prof.length() > 0) {
                compito.setAutore(ProfDecente(prof));
                compito.setTuttoIlGiorno(compitoDati.getBoolean("allDay"));
                compito.setContenuto(compitoDati.getString("title").trim());
                compito.setDataInserimento(compitoDati.getString("data_inserimento"));

                compito.setDataInizio(compitoDati.getString("start").trim());
                compito.setDataFine(compitoDati.getString("end").trim());

                return compito;
            } else return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static void SaveMyCompito(Context context, ContentValues values) {
        MyDB DBAgenda = new MyDB(context);
        SQLiteDatabase db = DBAgenda.getWritableDatabase();
        long newRowId = db.insert(
                MyDB.MyCompitoEntry.TABLE_NAME,
                MyDB.MyCompitoEntry.COLUMN_NAME_NULLABLE,
                values);

        db.close();
        Log.v("CompitoInserito", "Riga: " + newRowId);
    }

    public static String ReadDidattica(Context context) {
        StringBuilder text = new StringBuilder();
        try {
            File myFile = new File(context.getFilesDir() + "/Didattica");
            BufferedReader br = new BufferedReader(new FileReader(myFile));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
            return text.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String SpaziVoti(String voti) {
        String spazi = "             ";
        if (voti.contains("+") || voti.contains("-") && !voti.equals("+") && !voti.equals("-")) {
            if (voti.contains("-") && !voti.equals("-")) spazi = "            ";
            if (voti.contains("+") && !voti.equals("+")) spazi = "           ";
        } else if (voti.contains("½")) {
            spazi = "          ";
        } else if (voti.equals("10")) {
            spazi = "           ";
        } else if (voti.equals("-")) spazi += " ";

        return spazi;
    }

    public static float MediaIpotetica(double voto, double somma, int dim) {
        double newSomma = somma + voto;
        double newDim = dim + 1;
        double newMedia = newSomma / newDim;
        return (float) newMedia;
    }


    public static void CancellaPagineLocali(Context context) {

        File file = new File(context.getFilesDir() + "/Didattica");
        if (file.exists())
            file.delete();

        SQLiteDatabase db = new MyDB(context).getWritableDatabase();
        db.execSQL("DELETE FROM " + MyDB.VotoEntry.TABLE_NAME);
        db.execSQL("DELETE FROM " + MyDB.CompitoEntry.TABLE_NAME);
        db.execSQL("DELETE FROM " + MyDB.NotaEntry.TABLE_NAME);
        db.close();
    }

    public static void AvviaNotifiche(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Notifiche.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, MainActivity.CONTROLLO_VOTI_ID, intent, 0);
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        5 * 1000, alarmIntent);
    }

    public static Bitmap ImmagineAccout(String nome) {

        Bitmap src = Bitmap.createBitmap(255, 255, Bitmap.Config.ARGB_8888);
        src.eraseColor(Color.parseColor("#03A9F4"));

        String nomef = "";
        String[] lett = nome.split("\\s+");
        for (String s :
                lett) {
            nomef += s.substring(0, 1).toUpperCase();
        }

        Canvas cs = new Canvas(src);
        Paint tPaint = new Paint();
        float reduce = tPaint.measureText(nomef);
        tPaint.setTextSize(100 - reduce);
        tPaint.setColor(Color.WHITE);
        tPaint.setStyle(Paint.Style.FILL);
        tPaint.setTextAlign(Paint.Align.CENTER);
        float x_coord = src.getWidth() / 2;
        float height = (src.getHeight() / 2) + 33;
        cs.drawText(nomef, x_coord, height, tPaint);

        return src;
    }

    public static Bitmap FileImage(String ext) {

        ext = ext.replace(".", "").toLowerCase();
        int color;
        if (ext.contains("doc"))
            color = Color.parseColor("#2b579a");
        else if (ext.contains("xls"))
            color = Color.parseColor("#217346");
        else if (ext.contains("ppt"))
            color = Color.parseColor("#b7472a");
        else if (ext.contains("pdf"))
            color = Color.parseColor("#e21e00");
        else if (ext.contains("htm"))
            color = Color.parseColor("#2196F3");
        else if (ext.contains("png") || ext.contains("jpg"))
            color = Color.parseColor("#4CAF50");
        else if (ext.contains("zip") || ext.contains("rar") || ext.contains("7z"))
            color = Color.parseColor("#FF9800");
        else color = Color.parseColor("#a2a2a2");

        Bitmap src = Bitmap.createBitmap(255, 255, Bitmap.Config.ARGB_8888);
        Canvas cs = new Canvas(src);
        Paint tPaint = new Paint();
        tPaint.setColor(color);
        cs.drawCircle(127, 127, 126, tPaint);
        float reduce = tPaint.measureText(ext);
        tPaint.setTextSize(100 - reduce);
        tPaint.setColor(Color.WHITE);
        tPaint.setStyle(Paint.Style.FILL);
        tPaint.setTextAlign(Paint.Align.CENTER);
        float x_coord = src.getWidth() / 2;
        float height = (src.getHeight() / 2) + 30;
        cs.drawText(ext, x_coord, height, tPaint);

        return src;
    }

    public static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static double ConvertiInVoto(String voto) {
        double VotoEff = -1;
        try {
            String tmp;
            if (voto.length() == 2) {
                if (voto.contains("+")) {
                    tmp = voto.replace("+", ".25").trim();
                    if (VotoValido(tmp)) {
                        VotoEff = Double.parseDouble(tmp);
                    }
                } else if (voto.contains("-")) {
                    tmp = voto.replace("-", "").trim();
                    if (VotoValido(tmp)) {
                        VotoEff = Double.parseDouble(tmp) - 0.25;
                    }
                } else if (voto.contains("½")) {
                    tmp = voto.replace("½", ".5");
                    if (VotoValido(tmp)) {
                        VotoEff = Double.parseDouble(tmp);
                    }
                } else if (VotoValido(voto)) {
                    VotoEff = Double.parseDouble(voto);
                }
            } else if (voto.length() == 1 && VotoValido(voto)) {
                VotoEff = Double.parseDouble(voto);
            } else if (voto.length() == 3) {
                if (voto.contains("-")) {
                    tmp = voto.replace("-", "");
                    if (VotoValido(tmp)) {
                        VotoEff = Double.parseDouble(tmp) - 0.25;
                    }
                } else if (voto.contains("/")) {
                    tmp = voto.substring(voto.indexOf("/") + 1);
                    if (VotoValido(tmp)) {
                        VotoEff = Double.parseDouble(tmp) - 0.25;
                    }
                }
            } else if (voto.length() == 4) {
                if (VotoValido(voto)) {
                    VotoEff = Double.parseDouble(voto);
                }
            } else if (voto.length() == 5 && voto.contains("1/2")) {
                tmp = voto.replace(" 1/2", ".5");
                if (VotoValido(tmp)) {
                    VotoEff = Double.parseDouble(tmp);
                }
            }
            return VotoEff;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static SpannableString MessaggioVoto(double Obb, double media, double somma, int dim) {
        double backups = somma, newvoto = 6;
        SpannableString mess = new SpannableString("");
        int stop = 0, contavoti = dim;
        if (media < Obb) {
            while (stop == 0) {
                somma = somma + newvoto;
                contavoti = contavoti + 1;
                media = somma / contavoti;
                if (media >= Obb) {
                    stop = 1;
                    mess = new SpannableString("Devi prendere " + newvoto + " per avere " + Obb);
                    mess.setSpan(new StyleSpan(Typeface.BOLD), 14, String.valueOf(newvoto).length() + 14, 0);
                }
                if (newvoto == 10 && stop == 0) {
                    mess = new SpannableString("Devi prendere più di 10");
                    mess.setSpan(new StyleSpan(Typeface.BOLD), 13, mess.toString().length(), 0);
                    stop = 1;
                }
                newvoto = newvoto + 0.25;
                contavoti = contavoti - 1;
                somma = backups;
            }
        } else {
            newvoto = 10;
            while (stop == 0) {
                somma = somma + newvoto;
                contavoti = contavoti + 1;
                media = somma / contavoti;
                if (media < Obb) {
                    stop = 1;
                    newvoto = newvoto + 0.25;
                    mess = new SpannableString("Non prendere meno di " + newvoto);
                    mess.setSpan(new StyleSpan(Typeface.BOLD), 21, String.valueOf(newvoto).length() + 21, 0);
                }
                if (newvoto == 1) {
                    mess = new SpannableString("Puoi stare tranquillo!");
                    stop = 1;
                }
                newvoto = newvoto - 0.25;
                contavoti = contavoti - 1;
                somma = backups;
            }
        }

        return mess;
    }

}