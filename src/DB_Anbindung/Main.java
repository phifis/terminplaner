package DB_Anbindung;

public class Main {
    public static void main(String[] args) {
        if(args.length > 0) {
            DB_Anbindung db = new DB_Anbindung();
        }else {
            GUI gui = new GUI();
        }
    }
}


//TODO termine verschieben