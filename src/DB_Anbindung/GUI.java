package DB_Anbindung;

import javax.swing.*;
import javax.swing.plaf.basic.DefaultMenuLayout;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class GUI extends JFrame {
    DB_Anbindung datenBank;

    private JFrame insertWindow;

    private JPanel ausgabe;

    private CardLayout mainAusgabe;

    private boolean displayType;    //true = Tabelle, False = Kalender

    public GUI ()   {
        datenBank = new DB_Anbindung();

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("terminplaner");
        setResizable(false);

        insertWindow = new JFrame();
        insertWindow.setSize(800,180);
        insertWindow.setResizable(false);
        insertWindow.setLocationRelativeTo(this);

        displayType = true;

        createInsertWindow();
        createMainWindow();
//        testing();
    }

    private void testing() {
        JFrame testingFrame = new JFrame();
//        testingFrame.setLayout(new GridLayout(1,0));
//
//        String[] columnNames = {"titel","datum","startzeit","dauer","ort"};
//        Object[][] data = {{"test1","heute","jetzt","keine","hier"},
//        {"test2","heute2","jetzt2","keine2","hier2"}};
//
//        final JTable table = new JTable(data, columnNames);
//        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
//        table.setFillsViewportHeight(true);
//
//        //Create the scroll pane and add the table to it.
//        JScrollPane scrollPane = new JScrollPane(table);
//
//        //Add the scroll pane to this panel.
//        testingFrame.add(scrollPane);

        Color color = Color.getColor("BLUE");

        testingFrame.setSize(200,200);
        testingFrame.setLocationRelativeTo(null);

        JLabel testLabel = new JLabel("test");
        testLabel.setForeground(color);

        testingFrame.add(testLabel);

        testingFrame.setVisible(true);
    }

    private JTable ausgabeGenerieren(Zeitraum zeitraum) {
        int[] daysOfMonth = {0,31,28,31,30,31,30,31,31,30,31,30,31};
        ResultSet numTermine;
        int anzahlTermine;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.uuuu");
        DateTimeFormatter ltf = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate localDate = LocalDate.now();

        try {
            if(displayType) {       //ausgabe als Tabelle
                String[] spaltenNamen = {"Titel", "Datum", "Startzeit", "Dauer", "Ort"};

                switch (zeitraum) {
                    case ALLE:
                        numTermine = datenBank.search("SELECT COUNT(datum) FROM `tbl_termine` WHERE 1");
                        numTermine.next();
                        anzahlTermine = numTermine.getInt(1);
                        Object[][] data = new Object[anzahlTermine][];
                        ResultSet termine = datenBank.search("SELECT * FROM `tbl_termine` WHERE 1");
                        for (int i = 0; i < data.length; i++) {
                            termine.next();
                            data[i] = new Object[]{"<html><font color='" + termine.getString(7) + "'>" + termine.getString(2) + "</font></html>",            //titel
                                    termine.getDate(3).toLocalDate().format(dtf),   //datum
                                    termine.getTime(4).toLocalTime().format(ltf),   //startZeit
                                    termine.getTime(5).toLocalTime().format(ltf),   //dauer
                                    termine.getString(6)};                          //ort
                        }
                        JTable ausgabe = new JTable(data, spaltenNamen);
                        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                        for (int x = 0; x < ausgabe.getColumnCount(); x++) {
                            ausgabe.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
                        }
                        return ausgabe;
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
                        ausgabe = new JTable(data, spaltenNamen);
                        centerRenderer = new DefaultTableCellRenderer();
                        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                        for (int x = 0; x < ausgabe.getColumnCount(); x++) {
                            ausgabe.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
                        }
                        return ausgabe;
                    case WOCHE:
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
                        ausgabe = new JTable(data, spaltenNamen);
                        centerRenderer = new DefaultTableCellRenderer();
                        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                        for (int x = 0; x < ausgabe.getColumnCount(); x++) {
                            ausgabe.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
                        }
                        return ausgabe;
                    case NAECHSTEWOCHE:
                        endDate = LocalDate.ofYearDay(localDate.getYear(), localDate.getDayOfYear() + 7);
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
                        ausgabe = new JTable(data, spaltenNamen);
                        centerRenderer = new DefaultTableCellRenderer();
                        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                        for (int x = 0; x < ausgabe.getColumnCount(); x++) {
                            ausgabe.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
                        }
                        return ausgabe;
                    case MONAT:
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
                        ausgabe = new JTable(data, spaltenNamen);
                        centerRenderer = new DefaultTableCellRenderer();
                        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                        for (int x = 0; x < ausgabe.getColumnCount(); x++) {
                            ausgabe.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
                        }
                        return ausgabe;
                    case NAECHSTERMONAT:
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
                        ausgabe = new JTable(data, spaltenNamen);
                        centerRenderer = new DefaultTableCellRenderer();
                        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                        for (int x = 0; x < ausgabe.getColumnCount(); x++) {
                            ausgabe.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
                        }
                        return ausgabe;
                }
            } else {        //ausgabe als Kalender
                String[] spaltenNamen = {"Montag","Dienstag","Mittwoch","Donnerstag","Freitag","Samstag","Sonntag"};
                switch (zeitraum)   {
                    case WOCHE:
                        System.out.println("kalender");

                        LocalDate startDate = LocalDate.ofYearDay(localDate.getYear(), localDate.getDayOfYear() - localDate.getDayOfWeek().getValue());
                        LocalDate endDate = LocalDate.ofYearDay(localDate.getYear(), localDate.getDayOfYear() + (7 - localDate.getDayOfWeek().getValue()));
                        numTermine = datenBank.search("SELECT COUNT(datum) FROM `tbl_termine` WHERE datum > '" + startDate + "' AND datum <= '" + endDate + "'");
                        numTermine.next();
                        anzahlTermine = numTermine.getInt(1);
                        Object[][] data = new Object[1][7];
                        ResultSet termine = datenBank.search("SELECT * FROM `tbl_termine` WHERE datum > '" + startDate + "' AND datum <= '" + endDate + "'");

                        termine.next();


                        for (int i = 0; i < 7; i++) {
                            data[0][i]  = "<html>test<br>test2</html>";

                        }
                        JTable ausgabe = new JTable(data, spaltenNamen);
                        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                        for (int x = 0; x < ausgabe.getColumnCount(); x++) {
                            ausgabe.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
                        }
                        return ausgabe;

                }
            }

        } catch (SQLException err) {
            System.err.println(err);
        } catch (NullPointerException nul) {
        }
        return null;
    }

    private void createMainWindow() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        ausgabe = new JPanel();
        ausgabe.setLayout(new CardLayout());
        ausgabe.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));

        JPanel controlButtons = new JPanel();
        controlButtons.setLayout(new BoxLayout(controlButtons, BoxLayout.Y_AXIS));

        JButton alleTermine = new JButton("alle Termine ausgeben");
        alleTermine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainAusgabe.show(ausgabe,"alle");
            }
        });

        JPanel alleTerminePanel = new JPanel();
        alleTerminePanel.setLayout(new FlowLayout());
        alleTerminePanel.add(alleTermine);
        alleTerminePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton termineHeute = new JButton("termine heute ausgeben");
        termineHeute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainAusgabe.show(ausgabe,"heute");
            }
        });

        JPanel termineHeutePanel = new JPanel();
        termineHeutePanel.setLayout(new FlowLayout());
        termineHeutePanel.add(termineHeute);
        termineHeutePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton termineWoche = new JButton("termine diese Woche ausgeben");
        termineWoche.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainAusgabe.show(ausgabe, "woche");
            }
        });

        JPanel dieseWochePanel = new JPanel();
        dieseWochePanel.setLayout(new FlowLayout());
        dieseWochePanel.add(termineWoche);
        dieseWochePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton termineNaechsteWoche = new JButton("termine der nächsten Woche ausgeben");      //TODO: bessere Beschriftung
        termineNaechsteWoche.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainAusgabe.show(ausgabe, "naechste woche");
            }
        });

        JPanel naechsteWochePanel = new JPanel();
        naechsteWochePanel.setLayout(new FlowLayout());
        naechsteWochePanel.add(termineNaechsteWoche);
        naechsteWochePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton termineMonat = new JButton("termine diesen Monat ausgeben");
        termineMonat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainAusgabe.show(ausgabe, "monat");
            }
        });

        JPanel diesenMonatPanel = new JPanel();
        diesenMonatPanel.setLayout(new FlowLayout());
        diesenMonatPanel.add(termineMonat);
        diesenMonatPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton termineNaechsterMonat = new JButton("termine des nächsten Monats ausgeben");      //TODO: bessere Beschriftung
        termineNaechsterMonat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainAusgabe.show(ausgabe, "naechster monat");
            }
        });

        JPanel naechstenMonatPanel = new JPanel();
        naechstenMonatPanel.setLayout(new FlowLayout());
        naechstenMonatPanel.add(termineNaechsterMonat);
        naechstenMonatPanel.setAlignmentX(Component.CENTER_ALIGNMENT);


        JButton einfuegenButton = new JButton("neuen Termin einfügen");
        einfuegenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertWindow.setVisible(true);
            }
        });

        JPanel einfuegenPanel = new JPanel();
        einfuegenPanel.setLayout(new FlowLayout());
        einfuegenPanel.add(einfuegenButton);
        einfuegenPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton anzeigeUmschaltenButton = new JButton("Anzeige umschalten");
        anzeigeUmschaltenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayType = !displayType;
                refreshAusgabePanel();
            }
        });

        JPanel anzeigeUmschaltenPanel = new JPanel();
        anzeigeUmschaltenPanel.setLayout(new FlowLayout());
        anzeigeUmschaltenPanel.add(anzeigeUmschaltenButton);
        anzeigeUmschaltenPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton beendenButton = new JButton("Beenden");
        beendenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JPanel beendenPanel = new JPanel();
        beendenPanel.setLayout(new FlowLayout());
        beendenPanel.add(beendenButton);
        beendenPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        controlButtons.add(Box.createRigidArea(new Dimension(0,10)));
        controlButtons.add(alleTermine);
        controlButtons.add(Box.createRigidArea(new Dimension(0,20)));
        controlButtons.add(termineHeute);
        controlButtons.add(Box.createRigidArea(new Dimension(0,20)));
        controlButtons.add(termineWoche);
        controlButtons.add(Box.createRigidArea(new Dimension(0,20)));
        controlButtons.add(termineNaechsteWoche);
        controlButtons.add(Box.createRigidArea(new Dimension(0,20)));
        controlButtons.add(termineMonat);
        controlButtons.add(Box.createRigidArea(new Dimension(0,20)));
        controlButtons.add(termineNaechsterMonat);
        controlButtons.add(Box.createVerticalGlue());
        controlButtons.add(Box.createRigidArea(new Dimension(0,10)));
        controlButtons.add(einfuegenButton);
        controlButtons.add(Box.createRigidArea(new Dimension(0,20)));
        controlButtons.add(anzeigeUmschaltenButton);
        controlButtons.add(Box.createRigidArea(new Dimension(0,20)));
        controlButtons.add(beendenButton);
        controlButtons.add(Box.createRigidArea(new Dimension(0,10)));

        refreshAusgabePanel();

        mainPanel.add(ausgabe);
        mainPanel.add(controlButtons);

        this.getContentPane().add(mainPanel);
        this.setVisible(true);
    }

    private void refreshAusgabePanel(){
        ausgabe.removeAll();

        JScrollPane tableScrollPaneAlle = new JScrollPane(ausgabeGenerieren(Zeitraum.ALLE));
        JScrollPane tableScrollPaneHeute = new JScrollPane(ausgabeGenerieren(Zeitraum.HEUTE));
        JScrollPane tableScrollPaneWoche = new JScrollPane(ausgabeGenerieren(Zeitraum.WOCHE));
        JScrollPane tableScrollPaneNaechsteWoche = new JScrollPane(ausgabeGenerieren(Zeitraum.NAECHSTEWOCHE));
        JScrollPane tableScrollPaneMonat = new JScrollPane(ausgabeGenerieren(Zeitraum.MONAT));
        JScrollPane tableScrollPaneNaechstenMonat = new JScrollPane(ausgabeGenerieren(Zeitraum.NAECHSTERMONAT));


        JPanel ausgabePanelAlle = new JPanel();
        ausgabePanelAlle.setLayout(new BoxLayout(ausgabePanelAlle, BoxLayout.Y_AXIS));
        JPanel ueberschriftPanelAlle = new JPanel();
        ueberschriftPanelAlle.add(new JLabel("alle Termine"));

        ausgabePanelAlle.add(ueberschriftPanelAlle);
        ausgabePanelAlle.add(tableScrollPaneAlle);


        JPanel ausgabePanelHeute = new JPanel();
        ausgabePanelHeute.setLayout(new BoxLayout(ausgabePanelHeute, BoxLayout.Y_AXIS));
        JPanel ueberschriftPanelHeute = new JPanel();
        ueberschriftPanelHeute.add(new JLabel("Termine heute"));

        ausgabePanelHeute.add(ueberschriftPanelHeute);
        ausgabePanelHeute.add(tableScrollPaneHeute);


        JPanel ausgabePanelWoche = new JPanel();
        ausgabePanelWoche.setLayout(new BoxLayout(ausgabePanelWoche, BoxLayout.Y_AXIS));
        JPanel ueberschriftPanelWoche = new JPanel();
        ueberschriftPanelWoche.add(new JLabel("Termine diese Woche"));

        ausgabePanelWoche.add(ueberschriftPanelWoche);
        ausgabePanelWoche.add(tableScrollPaneWoche);


        JPanel ausgabePanelNaechsteWoche = new JPanel();
        ausgabePanelNaechsteWoche.setLayout(new BoxLayout(ausgabePanelNaechsteWoche, BoxLayout.Y_AXIS));
        JPanel ueberschriftPanelNaechsteWoche = new JPanel();
        ueberschriftPanelNaechsteWoche.add(new JLabel("Termine der nächsten sieben Tage"));

        ausgabePanelNaechsteWoche.add(ueberschriftPanelNaechsteWoche);
        ausgabePanelNaechsteWoche.add(tableScrollPaneNaechsteWoche);


        JPanel ausgabePanelMonat = new JPanel();
        ausgabePanelMonat.setLayout(new BoxLayout(ausgabePanelMonat, BoxLayout.Y_AXIS));
        JPanel ueberschriftPanelMonat = new JPanel();
        ueberschriftPanelMonat.add(new JLabel("Termine diesen Monat"));

        ausgabePanelMonat.add(ueberschriftPanelMonat);
        ausgabePanelMonat.add(tableScrollPaneMonat);


        JPanel ausgabePanelNaechstenMonat = new JPanel();
        ausgabePanelNaechstenMonat.setLayout(new BoxLayout(ausgabePanelNaechstenMonat, BoxLayout.Y_AXIS));
        JPanel ueberschriftPanelNaechstenMonat = new JPanel();
        ueberschriftPanelNaechstenMonat.add(new JLabel("Termine in des nächsten Monats"));

        ausgabePanelNaechstenMonat.add(ueberschriftPanelNaechstenMonat);
        ausgabePanelNaechstenMonat.add(tableScrollPaneNaechstenMonat);

        ausgabe.add(ausgabePanelAlle, "alle");
        ausgabe.add(ausgabePanelHeute, "heute");
        ausgabe.add(ausgabePanelWoche, "woche");
        ausgabe.add(ausgabePanelNaechsteWoche, "naechste woche");
        ausgabe.add(ausgabePanelMonat, "monat");
        ausgabe.add(ausgabePanelNaechstenMonat, "naechster monat");

        mainAusgabe = (CardLayout)(ausgabe.getLayout());
    }

    private void createInsertWindow()   {
        JPanel insertPanel = new JPanel();
        insertPanel.setLayout(new BoxLayout(insertPanel, BoxLayout.Y_AXIS));

        JPanel ueberschriftPanel = new JPanel();
        ueberschriftPanel.setLayout(new BorderLayout());

        ueberschriftPanel.add(new JLabel("neuen Termin hinzufügen"));
//        ueberschriftPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JPanel einfuegePanel = new JPanel();
//        einfuegePanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        einfuegePanel.setLayout(new BoxLayout(einfuegePanel,BoxLayout.X_AXIS));

        JPanel lp1 = new JPanel();
        lp1.setLayout(new FlowLayout());
        lp1.add(new JLabel("Titel"));
        lp1.setAlignmentY(Component.CENTER_ALIGNMENT);
        lp1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField titelFeld = new JTextField();

        JPanel spalte1 = new JPanel();
        spalte1.setLayout(new BoxLayout(spalte1, BoxLayout.Y_AXIS));
        spalte1.add(lp1);
        spalte1.add(titelFeld);

        JPanel lp2 = new JPanel();
        lp2.setLayout(new FlowLayout());
        lp2.add(new JLabel("Datum"));
        lp2.setAlignmentY(Component.CENTER_ALIGNMENT);
        lp2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField datumFeld = new JTextField();

        JPanel spalte2 = new JPanel();
        spalte2.setLayout(new BoxLayout(spalte2, BoxLayout.Y_AXIS));
        spalte2.add(lp2);
        spalte2.add(datumFeld);

        JPanel lp3 = new JPanel();
        lp3.setLayout(new FlowLayout());
        lp3.add(new JLabel("Startzeit"));
        lp3.setAlignmentY(Component.CENTER_ALIGNMENT);
        lp3.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField startFeld = new JTextField();

        JPanel spalte3 = new JPanel();
        spalte3.setLayout(new BoxLayout(spalte3, BoxLayout.Y_AXIS));
        spalte3.add(lp3);
        spalte3.add(startFeld);

        JPanel lp4 = new JPanel();
        lp4.setLayout(new FlowLayout());
        lp4.add(new JLabel("Dauer"));
        lp4.setAlignmentY(Component.CENTER_ALIGNMENT);
        lp4.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField dauerFeld = new JTextField();

        JPanel spalte4 = new JPanel();
        spalte4.setLayout(new BoxLayout(spalte4, BoxLayout.Y_AXIS));
        spalte4.add(lp4);
        spalte4.add(dauerFeld);

        JPanel lp5 = new JPanel();
        lp5.setLayout(new FlowLayout());
        lp5.add(new JLabel("Ort"));
        lp5.setAlignmentY(Component.CENTER_ALIGNMENT);
        lp5.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField ortFeld = new JTextField();

        JPanel spalte5 = new JPanel();
        spalte5.setLayout(new BoxLayout(spalte5, BoxLayout.Y_AXIS));
        spalte5.add(lp5);
        spalte5.add(ortFeld);

        JPanel lp6 = new JPanel();
        lp6.setLayout(new FlowLayout());
        lp6.add(new JLabel("Farbe"));
        lp6.setAlignmentY(Component.CENTER_ALIGNMENT);
        lp6.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField farbeFeld = new JTextField();

        JPanel spalte6 = new JPanel();
        spalte6.setLayout(new BoxLayout(spalte6, BoxLayout.Y_AXIS));
        spalte6.add(lp6);
        spalte6.add(farbeFeld);

        einfuegePanel.add(Box.createRigidArea(new Dimension(5,0)));
        einfuegePanel.add(spalte1);
        einfuegePanel.add(Box.createRigidArea(new Dimension(20,0)));
        einfuegePanel.add(spalte2);
        einfuegePanel.add(Box.createRigidArea(new Dimension(20,0)));
        einfuegePanel.add(spalte3);
        einfuegePanel.add(Box.createRigidArea(new Dimension(20,0)));
        einfuegePanel.add(spalte4);
        einfuegePanel.add(Box.createRigidArea(new Dimension(20,0)));
        einfuegePanel.add(spalte5);
        einfuegePanel.add(Box.createRigidArea(new Dimension(20,0)));
        einfuegePanel.add(spalte6);
        einfuegePanel.add(Box.createRigidArea(new Dimension(5,0)));

        JPanel controlButtons = new JPanel();
        controlButtons.setLayout(new BoxLayout(controlButtons, BoxLayout.X_AXIS));
//        controlButtons.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JButton cancelButton = new JButton("abbrechen");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertWindow.setVisible(false);
            }
        });

        JButton addButton = new JButton("einfügen");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                char[] datum = datumFeld.getText().toCharArray();
                int[] punkte = new int[2];

                for(int i = 0; i < datum.length; i++)   {
                    if(datum[i] == '.') {
                        if(punkte[0] == 0)  {
                            punkte[0] = i;
                        } else {
                            punkte[1] = i;
                        }
                    }
                }


                String temp = "";
                for(int i = 0; i < punkte[0]; i++) 
                    temp += datum[i];
                int tag = Integer.parseInt(temp);
                temp = "";
                for(int i = punkte[0]+1; i < punkte[1]; i++)
                    temp += datum[i];
                int monat = Integer.parseInt(temp);
                temp = "";
                for(int i = punkte[1]+1; i < datum.length; i++)
                    temp += datum[i];
                int jahr = Integer.parseInt(temp);

                String farbe = farbeFeld.getText();
                if(farbe.equals("")) {
                    farbe = "BLACK";
                }

                datenBank.einfuegen(titelFeld.getText(), LocalDate.of(jahr,monat,tag),startFeld.getText(), dauerFeld.getText(), ortFeld.getText(), farbe.toUpperCase());

                refreshAusgabePanel();

                insertWindow.setVisible(false);

                // Termineingaben löschen
                titelFeld.setText("");
                datumFeld.setText("");
                startFeld.setText("");
                dauerFeld.setText("");
                ortFeld.setText("");
                farbeFeld.setText("");
            }
        });

        controlButtons.add(Box.createHorizontalGlue());
        controlButtons.add(Box.createRigidArea(new Dimension(20,0)));
        controlButtons.add(cancelButton);
        controlButtons.add(Box.createRigidArea(new Dimension(20,0)));
        controlButtons.add(addButton);
        controlButtons.add(Box.createRigidArea(new Dimension(10,0)));

        insertPanel.add(ueberschriftPanel);
        insertPanel.add(Box.createRigidArea(new Dimension(0,15)));
        insertPanel.add(einfuegePanel);
        insertPanel.add(Box.createRigidArea(new Dimension(0,15)));
        insertPanel.add(controlButtons);
        insertPanel.add(Box.createRigidArea(new Dimension(0,5)));

        insertWindow.add(insertPanel);
    }
}
