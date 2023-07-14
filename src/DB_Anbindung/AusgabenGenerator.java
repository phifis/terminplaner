package DB_Anbindung;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AusgabenGenerator {
    private final int[] daysOfMonth = {0,31,28,31,30,31,30,31,31,30,31,30,31};
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.uuuu");
    private final DateTimeFormatter ltf = DateTimeFormatter.ofPattern("HH:mm");
    private ResultSet numTermine;
    private int anzahlTermine;
    private LocalDate localDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private JTable ausgabe;
    private Object[][] data;
    private String[] spaltenNamen;
    private int linesMax;

    private DB_Anbindung datenBank;

    public AusgabenGenerator(DB_Anbindung datenBank)  {
        this.datenBank = datenBank;
    }

    public JTable ausgabeNachInput(String titel, LocalDate datum, LocalDate startZeit, LocalDate dauer, String ort, String farbe)   {
        String queryTitel;
        String queryDatum;
        String queryStartZeit;
        String queryDauer;
        String queryOrt;
        String queryFarbe;

        if(titel == null || titel.equals(""))
            queryTitel = "";
        else
            queryTitel = " titel LIKE '%"+titel+"%' OR";

        if(datum == null)
            queryDatum = "";
        else
            queryDatum = " datum LIKE '%"+datum.toString()+"%' OR";

        if(startZeit == null)
            queryStartZeit = "";
        else
            queryStartZeit = " startzeit LIKE '%"+startZeit.format(ltf)+"&' OR";

        if(dauer == null)
            queryDauer = "";
        else
            queryDauer = " dauer LIKE '%"+dauer.format(ltf)+"%' OR";

        if(ort == null || ort.equals(""))
            queryOrt = "";
        else
            queryOrt = " ort LIKE '%"+ort+"%' OR";

        if(farbe == null || farbe.equals(""))
            queryFarbe = "";
        else
            queryFarbe = " farbe LIKE '%"+farbe.toUpperCase()+"%' OR";

//        System.out.println(queryTitel+"|"+queryDatum+"|"+queryStartZeit+"|"+queryDauer+"|"+queryOrt+"|"+queryFarbe);

        try{
            numTermine = datenBank.search("SELECT COUNT(datum) FROM `tbl_termine` WHERE"+queryTitel+queryDatum+queryStartZeit+queryDauer+queryOrt+queryFarbe+" 0");

            numTermine.next();
            anzahlTermine = numTermine.getInt(1);

            data = new Object[anzahlTermine][];

            ResultSet termine = datenBank.search("SELECT * FROM `tbl_termine` WHERE"+queryTitel+queryDatum+queryStartZeit+queryDauer+queryOrt+queryFarbe+" 0");


            for (int i = 0; i < data.length; i++) {
                termine.next();
                data[i] = new Object[]{"<html><font color='" + termine.getString(7) + "'>" + termine.getString(2) + "</font></html>",            //titel
                        termine.getDate(3).toLocalDate().format(dtf),   //datum
                        termine.getTime(4).toLocalTime().format(ltf),   //startZeit
                        termine.getTime(5).toLocalTime().format(ltf),   //dauer
                        termine.getString(6)};                          //ort
            }

            spaltenNamen = new String[] {"Titel", "Datum", "Startzeit", "Dauer", "Ort"};

            ausgabe = new JTable(data, spaltenNamen);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            for (int x = 0; x < ausgabe.getColumnCount(); x++) {
                ausgabe.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
            }

            return ausgabe;

        }catch (SQLException sqlException)  {
            System.err.println(sqlException);
        }
        return null;
    }

    public JTable ausgabeGenerieren(Zeitraum zeitraum, boolean displayType) {
        localDate = LocalDate.now();

        try {
            switch (zeitraum) {
                case ALLE:
                    numTermine = datenBank.search("SELECT COUNT(datum) FROM `tbl_termine` WHERE 1");
                    numTermine.next();
                    anzahlTermine = numTermine.getInt(1);
                    data = new Object[anzahlTermine][];
                    ResultSet termine = datenBank.search("SELECT * FROM `tbl_termine` WHERE 1");
                    for (int i = 0; i < data.length; i++) {
                        termine.next();
                        data[i] = new Object[]{"<html><font color='" + termine.getString(7) + "'>" + termine.getString(2) + "</font></html>",            //titel
                                termine.getDate(3).toLocalDate().format(dtf),   //datum
                                termine.getTime(4).toLocalTime().format(ltf),   //startZeit
                                termine.getTime(5).toLocalTime().format(ltf),   //dauer
                                termine.getString(6)};                          //ort
                    }
                    break;



                     case HEUTE:
                    numTermine = datenBank.search("SELECT COUNT(datum) FROM `tbl_termine` WHERE datum = '" + localDate + "'");
                    numTermine.next();
                    anzahlTermine = numTermine.getInt(1);
                    data = new Object[anzahlTermine][];
                    termine = datenBank.search("SELECT * FROM `tbl_termine` WHERE datum = '" + localDate + "'");
                    for (int i = 0; i < data.length; i++) {
                        termine.next();
                        data[i] = new Object[]{"<html><font color='" + termine.getString(7) + "'>" + termine.getString(2) + "</font></html>",            //titel
                                termine.getDate(3).toLocalDate().format(dtf),   //datum
                                termine.getTime(4).toLocalTime().format(ltf),   //startzeit
                                termine.getTime(5).toLocalTime().format(ltf),   //dauer
                                termine.getString(6)};                          //ort
                    }
                    break;



                case WOCHE:
                    if(displayType) {       //als Tabelle ausgeben
                        LocalDate startDate = LocalDate.ofYearDay(localDate.getYear(), localDate.getDayOfYear() - localDate.getDayOfWeek().getValue());
                        LocalDate endDate = LocalDate.ofYearDay(localDate.getYear(), localDate.getDayOfYear() + (7 - localDate.getDayOfWeek().getValue()));
                        numTermine = datenBank.search("SELECT COUNT(datum) FROM `tbl_termine` WHERE datum > '" + startDate + "' AND datum <= '" + endDate + "'");
                        numTermine.next();
                        anzahlTermine = numTermine.getInt(1);
                        data = new Object[anzahlTermine][];
                        termine = datenBank.search("SELECT * FROM `tbl_termine` WHERE datum > '" + startDate + "' AND datum <= '" + endDate + "'");
                        for (int i = 0; i < data.length; i++) {
                            termine.next();
                            data[i] = new Object[]{"<html><font color='" + termine.getString(7) + "'>" + termine.getString(2) + "</font></html>",            //titel
                                    termine.getDate(3).toLocalDate().format(dtf),   //datum
                                    termine.getTime(4).toLocalTime().format(ltf),   //startzeit
                                    termine.getTime(5).toLocalTime().format(ltf),   //dauer
                                    termine.getString(6)};                          //ort
                        }


                    } else { // als Kalender ausgeben
                        LocalDate startDate = LocalDate.ofYearDay(localDate.getYear(), localDate.getDayOfYear() - localDate.getDayOfWeek().getValue());
                        LocalDate endDate = LocalDate.ofYearDay(localDate.getYear(), localDate.getDayOfYear() + (7 - localDate.getDayOfWeek().getValue()));

                        data = new Object[1][7];
                        termine = datenBank.search("SELECT * FROM `tbl_termine` WHERE datum > '" + startDate + "' AND datum <= '" + endDate + "' ORDER BY datum");

                        numTermine = datenBank.search("SELECT COUNT(datum) FROM `tbl_termine` WHERE datum > '" + startDate + "' AND datum <= '" + endDate + "'");
                        numTermine.next();
                        anzahlTermine = numTermine.getInt(1);

                        if(anzahlTermine > 0)
                            termine.next();

                        String cellString = "<html>";
                        int linesAktuell ;
                        linesMax = 2;
                        LocalDate tagesDatum = startDate;

                        for (int i = 0; i < 7; i++) { //geht durch alle Tage der Woche
                            linesAktuell = 2;
                            tagesDatum = tagesDatum.plusDays(1);

                            cellString += tagesDatum.format(dtf) + "<br><br>";

                            if(anzahlTermine > 0) {
                                while (tagesDatum.isEqual(termine.getDate("datum").toLocalDate())) {      //geht durch alle Termine an einem tag
                                    linesAktuell++;
                                    if (linesAktuell > linesMax) linesMax = linesAktuell;

                                    cellString += "<font color='" + termine.getString("farbe") + "'>" + termine.getString("titel") + "</font><br>";
                                    if (termine.getRow() < anzahlTermine) termine.next();
                                    else break;
                                }
                            }
                            cellString += "</html>";
                            data[0][i]  = cellString;
                            cellString = "<html>";

                        }
                    }
                    break;


                case NAECHSTEWOCHE:
                    if(displayType) {   //ausgabe als Tabelle
                        LocalDate endDate = LocalDate.ofYearDay(localDate.getYear(), localDate.getDayOfYear() + 7);
                        numTermine = datenBank.search("SELECT COUNT(datum) FROM `tbl_termine` WHERE datum >= '" + localDate + "' AND datum <= '" + endDate + "'");
                        numTermine.next();
                        anzahlTermine = numTermine.getInt(1);
                        data = new Object[anzahlTermine][];
                        termine = datenBank.search("SELECT * FROM `tbl_termine` WHERE datum >= '" + localDate + "' AND datum <= '" + endDate + "'");
                        for (int i = 0; i < data.length; i++) {
                            termine.next();
                            data[i] = new Object[]{"<html><font color='" + termine.getString(7) + "'>" + termine.getString(2) + "</font></html>",            //titel
                                    termine.getDate(3).toLocalDate().format(dtf),   //datum
                                    termine.getTime(4).toLocalTime().format(ltf),   //startzeit
                                    termine.getTime(5).toLocalTime().format(ltf),   //dauer
                                    termine.getString(6)};                          //ort
                        }


                    } else {        //ausgabe als Kalender
                        endDate = LocalDate.ofYearDay(localDate.getYear(), localDate.getDayOfYear() + 7);

                        if(localDate.getDayOfWeek().getValue() == 1)
                            data = new Object[1][7];
                        else
                            data = new Object[2][7];

                        termine = datenBank.search("SELECT * FROM `tbl_termine` WHERE datum >= '" + localDate + "' AND datum <= '" + endDate + "' ORDER BY datum");

                        numTermine = datenBank.search("SELECT COUNT(datum) FROM `tbl_termine` WHERE datum >= '" + localDate + "' AND datum <= '" + endDate + "'");
                        numTermine.next();
                        anzahlTermine = numTermine.getInt(1);

                        if(anzahlTermine>0) termine.next();

                        String cellString = "<html>";
                        int linesAktuell ;
                        linesMax = 2;
                        LocalDate tagesDatum = localDate;

                        boolean startReached = false;
                        boolean endReached = false;

                        for (int j = 0; j < data.length; j++) {
                            for (int i = 0; i < 7; i++) { //geht durch alle Tage der Woche
                                if(localDate.getDayOfWeek().getValue()-1 == i)    {
                                    if(startReached)
                                        endReached = true;
                                    startReached = true;
                                }
                                if(startReached && !endReached)    {
                                    linesAktuell = 2;
                                    cellString += tagesDatum.format(dtf) + "<br><br>";
                                    if(anzahlTermine > 0) {
                                        while (tagesDatum.isEqual(termine.getDate("datum").toLocalDate())) {      //geht durch alle Termine an einem tag
                                            linesAktuell++;
                                            if (linesAktuell > linesMax) linesMax = linesAktuell;
                                            cellString += "<font color='" + termine.getString("farbe") + "'>" + termine.getString("titel") + "</font><br>";
                                            if (termine.getRow() < anzahlTermine) termine.next();
                                            else break;
                                        }
                                    }
                                    cellString += "</html>";
                                    data[j][i] = cellString;
                                    cellString = "<html>";
                                    tagesDatum = tagesDatum.plusDays(1);
                                }
                            }
                        }
                    }
                    break;



                case MONAT:
                    if(displayType) {       //ausgabe als Tabelle
                        startDate = LocalDate.ofYearDay(localDate.getYear(), localDate.getDayOfYear() - localDate.getDayOfMonth());
                        endDate = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), daysOfMonth[localDate.getMonthValue()]);
                        numTermine = datenBank.search("SELECT COUNT(datum) FROM `tbl_termine` WHERE datum >= '" + startDate + "' AND datum <= '" + endDate + "'");
                        numTermine.next();
                        anzahlTermine = numTermine.getInt(1);
                        data = new Object[anzahlTermine][];
                        termine = datenBank.search("SELECT * FROM `tbl_termine` WHERE datum > '" + startDate + "' AND datum <= '" + endDate + "'");
                        for (int i = 0; i < data.length; i++) {
                            termine.next();
                            data[i] = new Object[]{"<html><font color='" + termine.getString(7) + "'>" + termine.getString(2) + "</font></html>",            //titel
                                    termine.getDate(3).toLocalDate().format(dtf),   //datum
                                    termine.getTime(4).toLocalTime().format(ltf),   //startzeit
                                    termine.getTime(5).toLocalTime().format(ltf),   //dauer
                                    termine.getString(6)};                          //ort
                        }
                    } else {        //als Kalender ausgeben
                        startDate = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), 1);
                        endDate = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), daysOfMonth[localDate.getMonthValue()]);

                        if(7-startDate.getDayOfWeek().getValue()<daysOfMonth[localDate.getMonthValue()]%7)
                            data = new Object[(daysOfMonth[localDate.getMonthValue()] / 7)+2][7];
                        else
                            data = new Object[(daysOfMonth[localDate.getMonthValue()] / 7)+1][7];

                        termine = datenBank.search("SELECT * FROM `tbl_termine` WHERE datum > '" + startDate + "' AND datum <= '" + endDate + "' ORDER BY datum");

                        numTermine = datenBank.search("SELECT COUNT(datum) FROM `tbl_termine` WHERE datum > '" + startDate + "' AND datum <= '" + endDate + "'");
                        numTermine.next();
                        anzahlTermine = numTermine.getInt(1);

                        if(anzahlTermine > 0)
                            termine.next();

                        String cellString = "<html>";
                        int linesAktuell ;
                        linesMax = 2;
                        LocalDate tagesDatum = startDate;

                        boolean startReached = false;
                        boolean endReached = false;

                        for(int j = 0; j < data.length; j++) {
                            for (int i = 0; i < 7; i++) { //geht durch alle Tage der Woche
                                if(startDate.getDayOfWeek().getValue()-1 == i)
                                    startReached = true;
                                if(tagesDatum.isEqual(endDate.plusDays(1)))
                                    endReached = true;

                                if(startReached && !endReached) {
                                    linesAktuell = 2;
                                    cellString += tagesDatum.format(dtf) + "<br><br>";
                                    if (anzahlTermine > 0) {
                                        while (tagesDatum.isEqual(termine.getDate("datum").toLocalDate())) {      //geht durch alle Termine an einem tag
                                            linesAktuell++;
                                            if (linesAktuell > linesMax) linesMax = linesAktuell;
                                            cellString += "<font color='" + termine.getString("farbe") + "'>" + termine.getString("titel") + "</font><br>";
                                            if (termine.getRow() < anzahlTermine) termine.next();
                                            else break;
                                        }
                                    }
                                    tagesDatum = tagesDatum.plusDays(1);
                                    cellString += "</html>";
                                    data[j][i] = cellString;
                                    cellString = "<html>";
                                }
                            }
                        }
                    }

                    break;


                case NAECHSTERMONAT:
                    if(displayType) {   //als Tabelle ausgeben
                        endDate = LocalDate.of(localDate.getYear(), localDate.getMonthValue() + 1, localDate.getDayOfMonth());
                        numTermine = datenBank.search("SELECT COUNT(datum) FROM `tbl_termine` WHERE datum >= '" + localDate + "' AND datum <= '" + endDate + "'");
                        numTermine.next();
                        anzahlTermine = numTermine.getInt(1);
                        data = new Object[anzahlTermine][];
                        termine = datenBank.search("SELECT * FROM `tbl_termine` WHERE datum >= '" + localDate + "' AND datum <= '" + endDate + "'");
                        for (int i = 0; i < data.length; i++) {
                            termine.next();
                            data[i] = new Object[]{"<html><font color='" + termine.getString(7) + "'>" + termine.getString(2) + "</font></html>",            //titel
                                    termine.getDate(3).toLocalDate().format(dtf),   //datum
                                    termine.getTime(4).toLocalTime().format(ltf),   //startzeit
                                    termine.getTime(5).toLocalTime().format(ltf),   //dauer
                                    termine.getString(6)};                          //ort
                        }
                    } else {        //als Kalender ausgeben
//                        localDate= LocalDate.of(2023,07,16);
                        endDate = LocalDate.ofYearDay(localDate.getYear(), localDate.getDayOfYear() + 30);


                        if(7-startDate.getDayOfWeek().getValue()>30%7)
                            data = new Object[(30 / 7)+2][7];
                        else
                            data = new Object[(30 / 7)+1][7];

                        termine = datenBank.search("SELECT * FROM `tbl_termine` WHERE datum >= '" + localDate + "' AND datum <= '" + endDate + "' ORDER BY datum");

                        numTermine = datenBank.search("SELECT COUNT(datum) FROM `tbl_termine` WHERE datum >= '" + localDate + "' AND datum <= '" + endDate + "'");
                        numTermine.next();
                        anzahlTermine = numTermine.getInt(1);

                        if(anzahlTermine>0) termine.next();

                        String cellString = "<html>";
                        int linesAktuell ;
                        linesMax = 2;
                        LocalDate tagesDatum = localDate;

                        boolean startReached = false;
                        boolean endReached = false;

                        for (int j = 0; j < data.length; j++) {
                            for (int i = 0; i < 7; i++) { //geht durch alle Tage der Woche
                                if(localDate.getDayOfWeek().getValue()-1 == i)    {
                                    startReached = true;
                                }
                                if(tagesDatum.isEqual(endDate))
                                    endReached = true;
                                if(startReached && !endReached)    {
                                    linesAktuell = 2;
                                    cellString += tagesDatum.format(dtf) + "<br><br>";
                                    if(anzahlTermine > 0) {
                                        while (tagesDatum.isEqual(termine.getDate("datum").toLocalDate())) {      //geht durch alle Termine an einem tag
                                            linesAktuell++;
                                            if (linesAktuell > linesMax) linesMax = linesAktuell;
                                            cellString += "<font color='" + termine.getString("farbe") + "'>" + termine.getString("titel") + "</font><br>";
                                            if (termine.getRow() < anzahlTermine) termine.next();
                                            else break;
                                        }
                                    }
                                    cellString += "</html>";
                                    data[j][i] = cellString;
                                    cellString = "<html>";
                                    tagesDatum = tagesDatum.plusDays(1);
                                }
                            }
                        }
                    }
                    break;
            }


        } catch (SQLException err) {
            System.err.println(err);
        } catch (NullPointerException nul) {
        }
        if(displayType)
            spaltenNamen = new String[]{"Titel", "Datum", "Startzeit", "Dauer", "Ort"};
        else
            spaltenNamen = new String[]{"Montag","Dienstag","Mittwoch","Donnerstag","Freitag","Samstag","Sonntag"};

        ausgabe = new JTable(data, spaltenNamen);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int x = 0; x < ausgabe.getColumnCount(); x++) {
            ausgabe.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
        }

        if (!displayType)   {
            ausgabe.setRowHeight(linesMax * 15 + 8);
            DefaultTableCellRenderer colorRender = new DefaultTableCellRenderer();
            colorRender.setBackground(Color.CYAN);
            ausgabe.getColumnModel().getColumn(localDate.getDayOfWeek().getValue()-1).setHeaderRenderer(colorRender);


            return ausgabe;
        }



        return ausgabe;
    }
}
