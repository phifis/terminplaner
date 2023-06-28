package DB_Anbindung;

import java.sql.*;
import java.util.Calendar;
import java.util.Date;

public class DB_Anbindung {
    private String query;
    private Statement stmt;
    private ResultSet rs;
    private Connection conn;

    public DB_Anbindung()  {
        init();
    }

    private void init() {
        String url = "jdbc:mysql://localhost:3306/termine";
        String user = "root";
        String password = "1234";

        try	{
            Connection conn = DriverManager.getConnection(url,user,password);

            System.out.println("erfolgreich mit Datenbank verbunden!");
            this.conn = conn;

//            einfuegen("test",new java.sql.Date(2023,06,23), new Time(13,00,00),new Time(1,0,0),"BCÜ");

            //Einfuegen und Verändern
            query = "Select t_id from tbl_termine where 1";
//            query = "Insert into tbl_termine (titel,datum,startzeit,dauer,ort) Values ('Sport','2023,06,15','14:30','1:30','Bitterbachhalle')";
            //query = "Update tbl_personen set nachname = 'Lidtke' where nachname = 'Lidke'";
//            query = "Delete from tbl_termine where 1";

            stmt = conn.createStatement();
            stmt.execute(query);

            // Auslesen
            query = "Select * from tbl_termine order by t_id";

            //wir benötigen ein Statement-Objekt, um Aktivitäten auf der DB auszuführen
            stmt = conn.createStatement();

            //das ergebnis kommt in ein Resultset Object
            rs = stmt.executeQuery(query);

            //Spaltennamen ausgeben
            int columns = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= columns; i++)	{
                System.out.print(String.format("%-15s",rs.getMetaData().getColumnLabel(i)));
            }
            System.out.println();
            System.out.println("----------------------------------------------------");

            while(rs.next()) {
                for (int i = 1; i <= columns; i++ )	{
                    System.out.print(String.format("%-15s",rs.getString(i)));
                }
                System.out.println();
            }
            System.out.println("------------------------------------------------------------------------------");
            System.out.println();

            terminCheck();


        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void close() {
        try {
            System.out.println("close");

            rs.close();
            stmt.close();
        }catch(SQLException e){
            System.err.println(e);
        }
    }

    public ResultSet search(String query)    {
        try {
            stmt = conn.createStatement();
            //das ergebnis kommt in ein Resultset Object
            return stmt.executeQuery(query);
        } catch (SQLException e)    {
            System.err.println(e);
            return null;
        }
    }

    public void einfuegen(String titel, java.sql.Date datum, String startzeit, String dauer, String ort) {
        try{
            boolean vorhanden = false;

            query = "Select * from tbl_termine";
            //wir benötigen ein Statement-Objekt, um Aktivitäten auf der DB auszuführen
            stmt = conn.createStatement();
            //das ergebnis kommt in ein Resultset Object
            rs = stmt.executeQuery(query);

            while(rs.next())    {
                if(titel.equals(rs.getString("titel")))  {
                    System.out.println("bereits vorhanden");
                    vorhanden = true;
                    break;
                }
            }

            if(!vorhanden)  {
                query = "INSERT INTO `tbl_termine`(`titel`, `datum`, `startzeit`, `dauer`, `ort`) VALUES ('"+titel+"','"+datum.getYear()+"-"+datum.getMonth()+"-"+datum.getDate()+"','"+startzeit+"','"+dauer+"','"+ort+"')";
                //wir benötigen ein Statement-Objekt, um Aktivitäten auf der DB auszuführen
                stmt = conn.createStatement();
                //das ergebnis kommt in ein Resultset Object
                stmt.execute(query);
            }
        }catch (SQLException e) {
            System.err.println(e);
        }
    }

    public void terminCheck()  {
        Date currentDate = Calendar.getInstance().getTime();
        try {
            // Auslesen
            query = "Select * from tbl_termine";
            //wir benötigen ein Statement-Objekt, um Aktivitäten auf der DB auszuführen
            stmt = conn.createStatement();
            //das ergebnis kommt in ein Resultset Object
            rs = stmt.executeQuery(query);
            System.out.println("Termine heute:");

            //Spaltennamen ausgeben
            int columns = rs.getMetaData().getColumnCount();
            for (int i = 2; i <= columns; i++)	{
                System.out.print(String.format("%-15s",rs.getMetaData().getColumnLabel(i)));
            }
            System.out.println();
            System.out.println("------------------------------------------------------------------------------");

            while (rs.next()){
                if(currentDate.getYear() == rs.getDate("datum").getYear()
                && currentDate.getMonth() == rs.getDate("datum").getMonth()
                && currentDate.getDate() == rs.getDate("datum").getDate())  {
                    for (int i = 2; i <= columns; i++ )	{
                        System.out.print(String.format("%-15s",rs.getString(i)));
                    }
                    System.out.println();
                }
            }

            System.out.println("------------------------------------------------------------------------------");
            System.out.println();


        } catch (SQLException e){
            System.err.println(e.getMessage());
        }
    }

}
