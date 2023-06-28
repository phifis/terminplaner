package Testing;

import java.sql.Time;
import java.util.Date;

public class Termin {
    private int id;
    private String name;
    private Date datum;
    private Time start;
    private Time dauer;
    private String ort;

    public Termin(int id, String name, Date datum, Time start, Time dauer, String ort) {
        this.id = id;
        this.name = name;
        this.datum = datum;
        this.start = start;
        this.dauer = dauer;
        this.ort = ort;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public Date getDatum() {return datum;}
    public void setDatum(Date datum) {this.datum = datum;}
    public Time getStart() {return start;}
    public void setStart(Time start) {this.start = start;}
    public Time getDauer() {return dauer;}
    public void setDauer(Time dauer) {this.dauer = dauer;}
    public String getOrt() {return ort;}
    public void setOrt(String ort) {this.ort = ort;}

    public void ausgeben()  {
        System.out.println("ID: "+id);
        System.out.println("Name: "+name);
        System.out.println("Ort: "+ort);
        System.out.println("Beginn: "+datum.getDate() + " - "+start);
        System.out.println("Dauer: "+dauer);
    }

}
