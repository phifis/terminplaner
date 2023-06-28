package Testing;

import java.sql.Time;
import java.util.Date;

public class TestingMain {
    public static void main(String[] args) {
        Termin [] termine = new Termin[5];

        termine[0] = new Termin(0,"test",new Date(2023,06,14), new Time(12,0,0), new Time(1,0,0),"hier");

        termine[0].ausgeben();
    }
}
