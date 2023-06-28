package DB_Anbindung;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public class GUI extends JFrame {
    DB_Anbindung datenBank;

    private JFrame insertWindow;
    private JTable ausgabeTable;

    public GUI ()   {
        datenBank = new DB_Anbindung();

        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("terminplaner");
        setResizable(false);

        insertWindow = new JFrame();
        insertWindow.setSize(600,200);
        insertWindow.setResizable(true);
        insertWindow.setLocationRelativeTo(this);

        createInsertWindow();
        createMainWindow();
//        testing();
    }

    private void testing() {
        JFrame testingFrame = new JFrame();
        testingFrame.setLayout(new GridLayout(1,0));

        String[] columnNames = {"titel","datum","startzeit","dauer","ort"};
        Object[][] data = {{"test1","heute","jetzt","keine","hier"},
        {"test2","heute2","jetzt2","keine2","hier2"}};

        final JTable table = new JTable(data, columnNames);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        testingFrame.add(scrollPane);
        testingFrame.setVisible(true);
    }

    private JTable ausgabeGenerieren(Zeitraum zeitraum) {
        String[] spaltenNamen = {"Titel", "Datum", "StartZeit", "Dauer", "Ort"};
        ResultSet numTermine;
        int anzahlTermine;

        java.util.Date currentDate = Calendar.getInstance().getTime();
        try {
            switch (zeitraum) {
                case ALLE:
                    numTermine = datenBank.search("SELECT COUNT(datum) FROM `tbl_termine` WHERE 1");
                    numTermine.next();
                    anzahlTermine = numTermine.getInt(1);
                    Object[][] data = new Object[anzahlTermine][];
                    ResultSet termine = datenBank.search("SELECT * FROM `tbl_termine` WHERE 1");

                    for(int i = 0; i < data.length; i++)    {
                        termine.next();
                        data[i] = new Object[]{termine.getString(2), termine.getDate(3), termine.getTime(4), termine.getTime(5), termine.getString(6)};
                    }

                    JTable ausgabe = new JTable(data, spaltenNamen);
                    return ausgabe;
                case HEUTE:
                    System.out.println("heute");

                    String condition = currentDate.getYear()+"-"+currentDate.getMonth()+"-"+currentDate.getDate();
                    numTermine = datenBank.search("SELECT COUNT(datum) FROM `tbl_termine` WHERE datum = "+ condition);
                    numTermine.next();
                    anzahlTermine = numTermine.getInt(1);

                    data = new Object[anzahlTermine][];
                    termine = datenBank.search("SELECT * FROM `tbl_termine` WHERE datum = "+condition);

                    for(int i = 0; i < data.length; i++)    {
                        termine.next();
                        data[i] = new Object[]{termine.getString(2), termine.getDate(3), termine.getTime(4), termine.getTime(5), termine.getString(6)};
                    }

                    ausgabe = new JTable(data, spaltenNamen);
                    return ausgabe;
                case WOCHE:
                    break;
            }

        }catch (SQLException err)   {
            System.err.println(err);
        }
        return null;
    }

    private void createMainWindow() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        JPanel ausgabe = new JPanel();
        ausgabe.setLayout(new BorderLayout());

        ausgabeTable = ausgabeGenerieren(Zeitraum.HEUTE);

        JPanel controlButtons = new JPanel();
        controlButtons.setLayout(new BoxLayout(controlButtons, BoxLayout.Y_AXIS));

        JButton alleTermine = new JButton("alle Termine ausgeben");
        alleTermine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ausgabeTable = ausgabeGenerieren(Zeitraum.ALLE);
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
                ausgabeTable = ausgabeGenerieren(Zeitraum.HEUTE);
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
                System.out.println("termine diese WWoche ausgeben");
            }
        });

        JPanel dieseWochePanel = new JPanel();
        dieseWochePanel.setLayout(new FlowLayout());
        dieseWochePanel.add(termineWoche);
        dieseWochePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

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
        controlButtons.add(Box.createHorizontalGlue());
        controlButtons.add(alleTermine);
        controlButtons.add(Box.createRigidArea(new Dimension(0,20)));
        controlButtons.add(termineHeute);
        controlButtons.add(Box.createRigidArea(new Dimension(0,20)));
        controlButtons.add(termineWoche);
        controlButtons.add(Box.createVerticalGlue());
        controlButtons.add(Box.createRigidArea(new Dimension(0,10)));
        controlButtons.add(einfuegenButton);
        controlButtons.add(Box.createRigidArea(new Dimension(0,20)));
        controlButtons.add(beendenButton);

        JScrollPane tableScrollPane = new JScrollPane(ausgabeTable);

        ausgabe.add(tableScrollPane);

        mainPanel.add(ausgabe);
        mainPanel.add(controlButtons);

        this.getContentPane().add(mainPanel);
        this.setVisible(true);
    }

    private void createInsertWindow()   {
        JPanel insertPanel = new JPanel();
        insertPanel.setLayout(new BoxLayout(insertPanel, BoxLayout.Y_AXIS));

        JPanel ueberschriftPanel = new JPanel();
        ueberschriftPanel.setLayout(new BorderLayout());

        ueberschriftPanel.add(new JLabel("neuen Termin hinzufügen"));
        ueberschriftPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JPanel einfuegePanel = new JPanel();
        einfuegePanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
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
        einfuegePanel.add(Box.createRigidArea(new Dimension(5,0)));

        JPanel controlButtons = new JPanel();
        controlButtons.setLayout(new BoxLayout(controlButtons, BoxLayout.X_AXIS));
        controlButtons.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JButton cancelButton = new JButton("Cancel");
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

                datenBank.einfuegen(titelFeld.getText(), new Date(jahr, monat, tag),startFeld.getText(), dauerFeld.getText(), ortFeld.getText());

                insertWindow.setVisible(false);

                // Termineingaben löschen
                titelFeld.setText("");
                datumFeld.setText("");
                startFeld.setText("");
                dauerFeld.setText("");
                ortFeld.setText("");
            }
        });

        controlButtons.add(Box.createHorizontalGlue());
        controlButtons.add(Box.createRigidArea(new Dimension(20,0)));
        controlButtons.add(cancelButton);
        controlButtons.add(Box.createRigidArea(new Dimension(20,0)));
        controlButtons.add(addButton);

        insertPanel.add(ueberschriftPanel);
        insertPanel.add(Box.createRigidArea(new Dimension(0,15)));
        insertPanel.add(einfuegePanel);
        insertPanel.add(Box.createRigidArea(new Dimension(0,15)));
        insertPanel.add(controlButtons);
        insertPanel.add(Box.createRigidArea(new Dimension(0,5)));

        insertWindow.add(insertPanel);
    }
}
