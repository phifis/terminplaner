package DB_Anbindung;

import com.mysql.cj.result.OffsetTimeValueFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;

public class GUI extends JFrame {
    private DB_Anbindung datenBank;
    private AusgabenGenerator ausgabenGenerator;

    private JFrame insertWindow;
    private JFrame removeWindow;
    private JFrame editWindow;

    private JPanel ausgabe;
    private JPanel displaySearchPanel;

    private JTextField editTitelFeld;
    private JTextField editDatumFeld;
    private JTextField editStartFeld;
    private JTextField editDauerFeld;
    private JTextField editOrtFeld;
    private JTextField editFarbeFeld;

    private JTable[] ausgabeTabellen;

    private CardLayout mainAusgabe;

    private String lastDisplayType;

    private MouseListener headerListener;

    private boolean displayType;    //true = Tabelle, False = Kalender

    private int currentDisplayedTimeWindow;
    private int editID;

    public GUI ()   {
        datenBank = new DB_Anbindung();
        ausgabenGenerator = new AusgabenGenerator(datenBank);

        headerListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ausgabenGenerator.order_byChange(e.getPoint());

                refreshAusgabePanel();
            }
        };

        ausgabenGenerator.setHeaderListener(headerListener);

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("terminplaner");
        setResizable(false);

        insertWindow = new JFrame();
        insertWindow.setMinimumSize(new Dimension(700,150));
        insertWindow.setResizable(false);
        insertWindow.setLocationRelativeTo(this);

        editWindow = new JFrame();
        editWindow.setMinimumSize(new Dimension(700,150));
        editWindow.setResizable(false);
        editWindow.setLocationRelativeTo(this);

        removeWindow = new JFrame();
        removeWindow.setResizable(false);
        removeWindow.setLocationRelativeTo(this);

        displayType = true;
        lastDisplayType = "alle";
        currentDisplayedTimeWindow = 0;
        ausgabeTabellen = new JTable[6];

        displaySearchPanel = new JPanel();

        createInsertWindow();
        createEditWindow();
        createRemoveWindow();
        createMainWindow();
//        testing();
    }

    private void testing() {
        JFrame testingFrame = new JFrame();
        JPanel testingPanel = new JPanel();
        testingPanel.setLayout(new BoxLayout(testingPanel, BoxLayout.Y_AXIS));

        String[] columnNames = {"titel","datum","startzeit","dauer","ort"};
        Object[][] data = {{"test1","heute","jetzt","keine","hier"},
        {"test2","heute2","jetzt2","keine2","hier2"}};

        final JTable table = new JTable(data, columnNames);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
//        testingFrame.add(table);

        // listener
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                String name = table.getColumnName(col);
                System.out.println("Column index selected " + col + " " + name);
            }
        });



        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton titelButton = new JButton("Titel");
        titelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("titel");
            }
        });

        JButton datumButton = new JButton("Datum");
        datumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Datum");
            }
        });

        JButton startZeitButton = new JButton("Startzeit");
        startZeitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Startzeit");
            }
        });

        JButton dauerButton = new JButton("Dauer");
        dauerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Dauer");
            }
        });

        JButton ortButton = new JButton("Ort");
        ortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Ort");
            }
        });

        buttonPanel.add(titelButton);
        buttonPanel.add(datumButton);
        buttonPanel.add(startZeitButton);
        buttonPanel.add(dauerButton);
        buttonPanel.add(ortButton);

//        testingPanel.add(buttonPanel);
        testingPanel.add(scrollPane);


        testingFrame.setLocationRelativeTo(null);

        testingFrame.add(testingPanel);
        testingFrame.pack();
        testingFrame.setVisible(true);
    }

    private void createMainWindow() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setMaximumSize(new Dimension(1000,20));

        JTextField queryField = new JTextField();

        ActionListener searchAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchQuery = queryField.getText();
                boolean istDatum = false;

                char[] searchQueryChar = searchQuery.toCharArray();
                for(int i = 0;i< searchQueryChar.length; i++)   {
                    if(searchQueryChar[i] == '.')   {
                        istDatum = true;
                    }
                }

                JScrollPane ausgabePane;

                if(istDatum)    {
                    ausgabePane = new JScrollPane(ausgabenGenerator.ausgabeNachInput(null,convertToLocalDate(searchQuery),null,null,null,null));
                } else {
                    ausgabePane = new JScrollPane(ausgabenGenerator.ausgabeNachInput(searchQuery,null,null,null,searchQuery,searchQuery));
                }

                displaySearchPanel = new JPanel();
                displaySearchPanel.setLayout(new BoxLayout(displaySearchPanel, BoxLayout.Y_AXIS));

                JPanel ueberschriftSearchPanel = new JPanel();
                ueberschriftSearchPanel.add(new JLabel("Ausgabe der Suche"));

                displaySearchPanel.add(ueberschriftSearchPanel);
                displaySearchPanel.add(ausgabePane);

                ausgabe.add(displaySearchPanel,"suche");

                mainAusgabe = (CardLayout)ausgabe.getLayout();

                lastDisplayType = "suche";

                mainAusgabe.show(ausgabe,"suche");

            }
        };

        queryField.addActionListener(searchAction);

        JButton searchButton = new JButton("suchen");
        searchButton.addActionListener(searchAction);

        searchPanel.add(Box.createRigidArea(new Dimension(5,0)));
        searchPanel.add(queryField);
        searchPanel.add(Box.createRigidArea(new Dimension(10,0)));
        searchPanel.add(searchButton);
        searchPanel.add(Box.createRigidArea(new Dimension(5,0)));


        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.X_AXIS));

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
                lastDisplayType = "alle";
                currentDisplayedTimeWindow = 0;
            }
        });

        JPanel alleTerminePanel = new JPanel();
        alleTerminePanel.setLayout(new FlowLayout());
        alleTerminePanel.add(alleTermine);
        alleTerminePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton termineHeute = new JButton("Termine heute ausgeben");
        termineHeute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainAusgabe.show(ausgabe,"heute");
                lastDisplayType = "heute";
                currentDisplayedTimeWindow = 1;
            }
        });

        JPanel termineHeutePanel = new JPanel();
        termineHeutePanel.setLayout(new FlowLayout());
        termineHeutePanel.add(termineHeute);
        termineHeutePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton termineWoche = new JButton("Termine diese Woche ausgeben");
        termineWoche.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainAusgabe.show(ausgabe, "woche");
                lastDisplayType = "woche";
                currentDisplayedTimeWindow = 2;
            }
        });

        JPanel dieseWochePanel = new JPanel();
        dieseWochePanel.setLayout(new FlowLayout());
        dieseWochePanel.add(termineWoche);
        dieseWochePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton termineNaechsteWoche = new JButton("Termine der nächsten Woche ausgeben");
        termineNaechsteWoche.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainAusgabe.show(ausgabe, "naechste woche");
                lastDisplayType = "naechste woche";
                currentDisplayedTimeWindow = 3;
            }
        });

        JPanel naechsteWochePanel = new JPanel();
        naechsteWochePanel.setLayout(new FlowLayout());
        naechsteWochePanel.add(termineNaechsteWoche);
        naechsteWochePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton termineMonat = new JButton("Termine diesen Monat ausgeben");
        termineMonat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainAusgabe.show(ausgabe, "monat");
                lastDisplayType = "monat";
                currentDisplayedTimeWindow = 4;
            }
        });

        JPanel diesenMonatPanel = new JPanel();
        diesenMonatPanel.setLayout(new FlowLayout());
        diesenMonatPanel.add(termineMonat);
        diesenMonatPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton termineNaechsterMonat = new JButton("Termine des nächsten Monats ausgeben");
        termineNaechsterMonat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainAusgabe.show(ausgabe, "naechster monat");
                lastDisplayType = "naechster monat";
                currentDisplayedTimeWindow = 5;
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

        JButton editButton = new JButton("einen Termin bearbeiten");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentRow = ausgabeTabellen[currentDisplayedTimeWindow].getSelectedRow();
                final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.uuuu");
                final DateTimeFormatter ltf = DateTimeFormatter.ofPattern("HH:mm");

                if(currentRow > -1){
                    ResultSet termin = datenBank.search("SELECT * FROM `tbl_termine` WHERE titel = '"+
                            removeHTMLFormatting(ausgabeTabellen[currentDisplayedTimeWindow].getValueAt(currentRow,0).toString())+
                            "' AND datum = '"+
                            convertToLocalDate(ausgabeTabellen[currentDisplayedTimeWindow].getValueAt(currentRow,1).toString())+
                            "'");

                    try {
                        termin.next();
                        editID = termin.getInt(1);
                        editTitelFeld.setText(termin.getString(2));
                        editDatumFeld.setText(termin.getDate(3).toLocalDate().format(dtf));
                        editStartFeld.setText(termin.getTime(4).toLocalTime().format(ltf));
                        editDauerFeld.setText(termin.getTime(5).toLocalTime().format(ltf));
                        editOrtFeld.setText(termin.getString(6));
                        editFarbeFeld.setText(termin.getString(7).toLowerCase());
                    } catch (SQLException sql){
                        System.err.println(sql);
                    }
                    editWindow.setVisible(true);
                }
                else    System.out.println("Bitte zuerst eine Zeile auswählen!");

            }
        });

        JButton entfernenButton = new JButton("einen Termin entfernen");
        entfernenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeWindow.setVisible(true);
            }
        });

        JPanel entfernenPanel = new JPanel(new FlowLayout());
        entfernenPanel.add(entfernenButton);
        entfernenPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton anzeigeUmschaltenButton = new JButton("Anzeige zu Kalender umschalten");
        anzeigeUmschaltenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayType = !displayType;
                if(displayType)
                    anzeigeUmschaltenButton.setText("Anzeige zu Kalender umschalten");
                else
                    anzeigeUmschaltenButton.setText("Anzeige zu Tabelle umschalten");
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
        controlButtons.add(editButton);
        controlButtons.add(Box.createRigidArea(new Dimension(0,20)));
        controlButtons.add(entfernenButton);
        controlButtons.add(Box.createRigidArea(new Dimension(0,20)));
        controlButtons.add(anzeigeUmschaltenButton);
        controlButtons.add(Box.createRigidArea(new Dimension(0,20)));
        controlButtons.add(beendenButton);
        controlButtons.add(Box.createRigidArea(new Dimension(0,10)));

        refreshAusgabePanel();

        displayPanel.add(ausgabe);
        displayPanel.add(controlButtons);

        mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
        mainPanel.add(searchPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
        mainPanel.add(displayPanel);

        this.getContentPane().add(mainPanel);
        this.setVisible(true);
    }

    private void refreshAusgabePanel(){
        ausgabe.removeAll();

        ausgabeTabellen[0] = ausgabenGenerator.ausgabeGenerieren(Zeitraum.ALLE, true);
        ausgabeTabellen[1] = ausgabenGenerator.ausgabeGenerieren(Zeitraum.HEUTE, true);
        ausgabeTabellen[2] = ausgabenGenerator.ausgabeGenerieren(Zeitraum.WOCHE, displayType);
        ausgabeTabellen[3] = ausgabenGenerator.ausgabeGenerieren(Zeitraum.NAECHSTEWOCHE, displayType);
        ausgabeTabellen[4] = ausgabenGenerator.ausgabeGenerieren(Zeitraum.MONAT, displayType);
        ausgabeTabellen[5] = ausgabenGenerator.ausgabeGenerieren(Zeitraum.NAECHSTERMONAT, displayType);

        JScrollPane tableScrollPaneAlle = new JScrollPane(ausgabeTabellen[0]);
        JScrollPane tableScrollPaneHeute = new JScrollPane(ausgabeTabellen[1]);
        JScrollPane tableScrollPaneWoche = new JScrollPane(ausgabeTabellen[2]);
        JScrollPane tableScrollPaneNaechsteWoche = new JScrollPane(ausgabeTabellen[3]);
        JScrollPane tableScrollPaneMonat = new JScrollPane(ausgabeTabellen[4]);
        JScrollPane tableScrollPaneNaechstenMonat = new JScrollPane(ausgabeTabellen[5]);

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
        ausgabe.add(displaySearchPanel,"suche");

        mainAusgabe = (CardLayout)(ausgabe.getLayout());
        mainAusgabe.show(ausgabe,lastDisplayType);
    }

    private void createRemoveWindow()   {
        JPanel removePanel = new JPanel();
        removePanel.setLayout(new BoxLayout(removePanel, BoxLayout.Y_AXIS));

        JPanel previewPanel = new JPanel(new CardLayout());

        JPanel ueberschriftPanel = new JPanel(new FlowLayout());
        JLabel ueberschriftLabel = new JLabel("einen vorhandenen Termin anhand von Titel, Datum, Ort oder Farbe entfernen");
        ueberschriftLabel.setFont(new Font("Calibri",Font.BOLD,18));
        ueberschriftPanel.add(ueberschriftLabel);

        JPanel erklaerungsTextPanel = new JPanel(new FlowLayout());
        JLabel erklaerungsTextLabel = new JLabel("<html>unten die gewollten Parameter eingeben, nach denen die Termine entfernt werden.<br>der Vorschau-Knopf zeit alle einträge an, für die mindestens eine Eigenschaft übereinstimmt.<br>der Entfernen-Knopf entfernt alle Einträge, für die alle gewählten Eigenschaften übereinstimmen.</html>");
//        erklaerungsTextLabel.setFont(new Font("Calibri", Font.BOLD,12));
        erklaerungsTextPanel.add(erklaerungsTextLabel);

        JPanel eingabePanel = new JPanel();
        eingabePanel.setLayout(new BoxLayout(eingabePanel, BoxLayout.X_AXIS));

        JPanel spalte1 = new JPanel();
        spalte1.setLayout(new BoxLayout(spalte1, BoxLayout.Y_AXIS));

        JPanel spalte2 = new JPanel();
        spalte2.setLayout(new BoxLayout(spalte2, BoxLayout.Y_AXIS));

        JPanel spalte3 = new JPanel();
        spalte3.setLayout(new BoxLayout(spalte3, BoxLayout.Y_AXIS));

        JPanel spalte4 = new JPanel();
        spalte4.setLayout(new BoxLayout(spalte4, BoxLayout.Y_AXIS));

        JPanel lp1 = new JPanel(new FlowLayout());
        lp1.add(new JLabel("Titel"));

        JTextField titelFeld = new JTextField();

        JPanel lp2 = new JPanel(new FlowLayout());
        lp2.add(new JLabel("Datum"));

        JTextField datumFeld = new JTextField();

        JPanel lp3 = new JPanel(new FlowLayout());
        lp3.add(new JLabel("Ort"));

        JTextField ortFeld = new JTextField();

        JPanel lp4 = new JPanel(new FlowLayout());
        lp4.add(new JLabel("Farbe"));

        JTextField farbeFeld = new JTextField();

        spalte1.add(lp1);
        spalte1.add(titelFeld);

        spalte2.add(lp2);
        spalte2.add(datumFeld);

        spalte3.add(lp3);
        spalte3.add(ortFeld);

        spalte4.add(lp4);
        spalte4.add(farbeFeld);

        eingabePanel.add(Box.createRigidArea(new Dimension(5,0)));
        eingabePanel.add(spalte1);
        eingabePanel.add(Box.createRigidArea(new Dimension(30,0)));
        eingabePanel.add(spalte2);
        eingabePanel.add(Box.createRigidArea(new Dimension(30,0)));
        eingabePanel.add(spalte3);
        eingabePanel.add(Box.createRigidArea(new Dimension(30,0)));
        eingabePanel.add(spalte4);
        eingabePanel.add(Box.createRigidArea(new Dimension(5,0)));

        JPanel controlButtons = new JPanel();
        controlButtons.setLayout(new BoxLayout(controlButtons, BoxLayout.X_AXIS));

        JButton cancelButton = new JButton("abbrechen");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeWindow.setVisible(false);
            }
        });

        JButton previewButton = new JButton("vorschau");
        previewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(datumFeld.getText().equals(""))
                    previewPanel.add(ausgabenGenerator.ausgabeNachInput(titelFeld.getText(),null,null,null, ortFeld.getText(), farbeFeld.getText()),"ausgabe");
                else
                    previewPanel.add(ausgabenGenerator.ausgabeNachInput(titelFeld.getText(),convertToLocalDate(datumFeld.getText()),null,null, ortFeld.getText(), farbeFeld.getText()),"ausgabe");

                CardLayout previewLayout = (CardLayout)previewPanel.getLayout();
                previewLayout.show(previewPanel, "ausgabe");

                removeWindow.pack();
            }
        });

        JButton removeButton = new JButton("entfernen");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String queryTitel;
                String queryDatum;
                String queryOrt;
                String queryFarbe;
                int count = 0;

                if(titelFeld.getText().equals(""))
                    queryTitel = "";
                else {
                    queryTitel = " titel = '" + titelFeld.getText() + "' AND";
                    count ++;
                }

                if(ortFeld.getText().equals(""))
                    queryOrt = "";
                else {
                    queryOrt = " ort = '" + ortFeld.getText() + "' AND";
                    count ++;
                }

                if(farbeFeld.getText().equals(""))
                    queryFarbe = "";
                else {
                    queryFarbe = " farbe = '" + farbeFeld.getText() + "' AND";
                    count ++;
                }

                if(!datumFeld.getText().equals("")) {
                    LocalDate datum = convertToLocalDate(datumFeld.getText());

                    queryDatum = " datum = '"+datum+"' AND";
                    count ++;
                } else {
                    queryDatum = "";
                }

                datenBank.execute("DELETE FROM `tbl_termine` WHERE"+queryTitel+queryDatum+queryOrt+queryFarbe+" "+(count>0)
                        +";");

                removeWindow.setVisible(false);
                titelFeld.setText("");
                datumFeld.setText("");
                ortFeld.setText("");
                farbeFeld.setText("");

                CardLayout previewLayout = (CardLayout)previewPanel.getLayout();
                previewLayout.show(previewPanel, "empty");
                removeWindow.pack();

                refreshAusgabePanel();
            }
        });

        controlButtons.add(Box.createHorizontalGlue());
        controlButtons.add(cancelButton);
        controlButtons.add(Box.createRigidArea(new Dimension(20,0)));
        controlButtons.add(previewButton);
        controlButtons.add(Box.createRigidArea(new Dimension(20,0)));
        controlButtons.add(removeButton);
        controlButtons.add(Box.createRigidArea(new Dimension(5,0)));

        previewPanel.add(new JPanel(new BorderLayout()), "empty");

        removePanel.add(Box.createRigidArea(new Dimension(0,10)));
        removePanel.add(ueberschriftPanel);
//        removePanel.add(Box.createRigidArea(new Dimension(0,5)));
        removePanel.add(erklaerungsTextPanel);
        removePanel.add(Box.createRigidArea(new Dimension(0,15)));
        removePanel.add(eingabePanel);
        removePanel.add(Box.createRigidArea(new Dimension(0,15)));
        removePanel.add(controlButtons);
        removePanel.add(Box.createRigidArea(new Dimension(0,15)));
        removePanel.add(previewPanel);
        removePanel.add(Box.createRigidArea(new Dimension(0,5)));

        removeWindow.add(removePanel);
        removeWindow.pack();
        removeWindow.setLocationRelativeTo(this);
    }

    private void createEditWindow()   {
        JPanel editPanel = new JPanel();
        editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));

        JPanel ueberschriftPanel = new JPanel();
        ueberschriftPanel.setLayout(new FlowLayout());

        JLabel uebderschriftLabel = new JLabel("einen vorhandenen Termin Aktualisieren");
        uebderschriftLabel.setFont(new Font("Calibri",Font.BOLD, 18));

        ueberschriftPanel.add(uebderschriftLabel);
//        ueberschriftPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JPanel erlaerungsPanel = new JPanel(new FlowLayout());
        JLabel erklaetungsLabel = new JLabel("<html>Zum eingeben eines Termins die gewüschten Daten eingeben.<br>" +
                "Wird das Feld für Farbe freigelassen, dann wird standardmäßig Schwarz als Farbe verwendet.<br>" +
                "Titel und Ort kann als beliebiges Wort gewählt werden. für die Farbe muss das englische Wort verwendet werden.<br>" +
                "Für das Feld Datum muss der Tag im Format 'tt.mm.jjjj' oder 'ttmmjj' eingegeben werden.<br>" +
                "Für die Felder Startzeit und Dauer muss das Format 'SS:mm' vorliegen</html>");

        erlaerungsPanel.add(erklaetungsLabel);

        JPanel einfuegePanel = new JPanel();
//        einfuegePanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        einfuegePanel.setLayout(new BoxLayout(einfuegePanel,BoxLayout.X_AXIS));

        JPanel lp1 = new JPanel();
        lp1.setLayout(new FlowLayout());
        lp1.add(new JLabel("Titel"));
        lp1.setAlignmentY(Component.CENTER_ALIGNMENT);
        lp1.setAlignmentX(Component.CENTER_ALIGNMENT);

        editTitelFeld = new JTextField();
        editTitelFeld.setMinimumSize(new Dimension(50,20));

        JPanel spalte1 = new JPanel();
        spalte1.setLayout(new BoxLayout(spalte1, BoxLayout.Y_AXIS));
        spalte1.add(lp1);
        spalte1.add(editTitelFeld);

        JPanel lp2 = new JPanel();
        lp2.setLayout(new FlowLayout());
        lp2.add(new JLabel("Datum"));
        lp2.setAlignmentY(Component.CENTER_ALIGNMENT);
        lp2.setAlignmentX(Component.CENTER_ALIGNMENT);

        editDatumFeld = new JTextField();
        editDatumFeld.setMinimumSize(new Dimension(50,20));

        JPanel spalte2 = new JPanel();
        spalte2.setLayout(new BoxLayout(spalte2, BoxLayout.Y_AXIS));
        spalte2.add(lp2);
        spalte2.add(editDatumFeld);

        JPanel lp3 = new JPanel();
        lp3.setLayout(new FlowLayout());
        lp3.add(new JLabel("Startzeit"));
        lp3.setAlignmentY(Component.CENTER_ALIGNMENT);
        lp3.setAlignmentX(Component.CENTER_ALIGNMENT);

        editStartFeld = new JTextField();
        editStartFeld.setMinimumSize(new Dimension(50,20));

        JPanel spalte3 = new JPanel();
        spalte3.setLayout(new BoxLayout(spalte3, BoxLayout.Y_AXIS));
        spalte3.add(lp3);
        spalte3.add(editStartFeld);

        JPanel lp4 = new JPanel();
        lp4.setLayout(new FlowLayout());
        lp4.add(new JLabel("Dauer"));
        lp4.setAlignmentY(Component.CENTER_ALIGNMENT);
        lp4.setAlignmentX(Component.CENTER_ALIGNMENT);

        editDauerFeld = new JTextField();
        editDauerFeld.setMinimumSize(new Dimension(50,20));

        JPanel spalte4 = new JPanel();
        spalte4.setLayout(new BoxLayout(spalte4, BoxLayout.Y_AXIS));
        spalte4.add(lp4);
        spalte4.add(editDauerFeld);

        JPanel lp5 = new JPanel();
        lp5.setLayout(new FlowLayout());
        lp5.add(new JLabel("Ort"));
        lp5.setAlignmentY(Component.CENTER_ALIGNMENT);
        lp5.setAlignmentX(Component.CENTER_ALIGNMENT);

        editOrtFeld = new JTextField();
        editOrtFeld.setMinimumSize(new Dimension(50,20));

        JPanel spalte5 = new JPanel();
        spalte5.setLayout(new BoxLayout(spalte5, BoxLayout.Y_AXIS));
        spalte5.add(lp5);
        spalte5.add(editOrtFeld);

        JPanel lp6 = new JPanel();
        lp6.setLayout(new FlowLayout());
        lp6.add(new JLabel("Farbe"));
        lp6.setAlignmentY(Component.CENTER_ALIGNMENT);
        lp6.setAlignmentX(Component.CENTER_ALIGNMENT);
        lp6.setMinimumSize(new Dimension(50,20));

        editFarbeFeld = new JTextField();
        editFarbeFeld.setMinimumSize(new Dimension(50,20));

        JPanel spalte6 = new JPanel();
        spalte6.setLayout(new BoxLayout(spalte6, BoxLayout.Y_AXIS));
        spalte6.add(lp6);
        spalte6.add(editFarbeFeld);

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
                editWindow.setVisible(false);
            }
        });

        JButton addButton = new JButton("aktualisieren");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String titel = editTitelFeld.getText();
                LocalDate datum = convertToLocalDate(editDatumFeld.getText());
                String startzeit = editStartFeld.getText();
                String dauer = editDauerFeld.getText();
                String ort = editOrtFeld.getText();

                String farbe = editFarbeFeld.getText();
                if(farbe.equals("")) {
                    farbe = "BLACK";
                }

                if(datenBank.bereitsVorhanden(titel,datum, editID))    System.out.println("Bereits Vorhanden!");
                else datenBank.execute("UPDATE `tbl_termine` SET `titel` = '"+titel+"', `datum` = '"+datum+"', `startzeit` = '"+
                        startzeit+"', `dauer` = '"+dauer+"', `ort` = '"+ort+"', `farbe` = '"+farbe+"' WHERE `t_id` = '"+editID+"'");



                refreshAusgabePanel();

                editWindow.setVisible(false);

                // Termineingaben löschen
                editTitelFeld.setText("");
                editDatumFeld.setText("");
                editStartFeld.setText("");
                editDauerFeld.setText("");
                editDauerFeld.setText("");
                editFarbeFeld.setText("");
            }
        });

        controlButtons.add(Box.createHorizontalGlue());
        controlButtons.add(Box.createRigidArea(new Dimension(20,0)));
        controlButtons.add(cancelButton);
        controlButtons.add(Box.createRigidArea(new Dimension(20,0)));
        controlButtons.add(addButton);
        controlButtons.add(Box.createRigidArea(new Dimension(10,0)));

        editPanel.add(ueberschriftPanel);
        editPanel.add(erlaerungsPanel);
        editPanel.add(Box.createRigidArea(new Dimension(0,15)));
        editPanel.add(einfuegePanel);
        editPanel.add(Box.createRigidArea(new Dimension(0,15)));
        editPanel.add(controlButtons);
        editPanel.add(Box.createRigidArea(new Dimension(0,5)));

        editWindow.add(editPanel);

        editWindow.pack();
    }

    private void createInsertWindow()   {
        JPanel insertPanel = new JPanel();
        insertPanel.setLayout(new BoxLayout(insertPanel, BoxLayout.Y_AXIS));

        JPanel ueberschriftPanel = new JPanel();
        ueberschriftPanel.setLayout(new FlowLayout());

        JLabel uebderschriftLabel = new JLabel("einen neuen Termine einfügen");
        uebderschriftLabel.setFont(new Font("Calibri",Font.BOLD, 18));

        ueberschriftPanel.add(uebderschriftLabel);
//        ueberschriftPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JPanel erlaerungsPanel = new JPanel(new FlowLayout());
        JLabel erklaetungsLabel = new JLabel("<html>Zum eingeben eines Termins die gewüschten Daten eingeben.<br>" +
                "Wird das Feld für Farbe freigelassen, dann wird standardmäßig Schwarz als Farbe verwendet.<br>" +
                "Titel und Ort kann als beliebiges Wort gewählt werden. für die Farbe muss das englische Wort verwendet werden.<br>" +
                "Für das Feld Datum muss der Tag im Format 'tt.mm.jjjj' oder 'ttmmjj' eingegeben werden.<br>" +
                "Für die Felder Startzeit und Dauer muss das Format 'SS:mm' vorliegen</html>");

        erlaerungsPanel.add(erklaetungsLabel);

        JPanel einfuegePanel = new JPanel();
//        einfuegePanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        einfuegePanel.setLayout(new BoxLayout(einfuegePanel,BoxLayout.X_AXIS));

        JPanel lp1 = new JPanel();
        lp1.setLayout(new FlowLayout());
        lp1.add(new JLabel("Titel"));
        lp1.setAlignmentY(Component.CENTER_ALIGNMENT);
        lp1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField titelFeld = new JTextField();
        titelFeld.setMinimumSize(new Dimension(50,20));

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
        datumFeld.setMinimumSize(new Dimension(50,20));

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
        startFeld.setMinimumSize(new Dimension(50,20));

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
        dauerFeld.setMinimumSize(new Dimension(50,20));

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
        ortFeld.setMinimumSize(new Dimension(50,20));

        JPanel spalte5 = new JPanel();
        spalte5.setLayout(new BoxLayout(spalte5, BoxLayout.Y_AXIS));
        spalte5.add(lp5);
        spalte5.add(ortFeld);

        JPanel lp6 = new JPanel();
        lp6.setLayout(new FlowLayout());
        lp6.add(new JLabel("Farbe"));
        lp6.setAlignmentY(Component.CENTER_ALIGNMENT);
        lp6.setAlignmentX(Component.CENTER_ALIGNMENT);
        lp6.setMinimumSize(new Dimension(50,20));

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

                String farbe = farbeFeld.getText();
                if(farbe.equals("")) {
                    farbe = "BLACK";
                }

                datenBank.einfuegen(titelFeld.getText(), convertToLocalDate(datumFeld.getText()),startFeld.getText(), dauerFeld.getText(), ortFeld.getText(), farbe.toUpperCase());

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
        insertPanel.add(erlaerungsPanel);
        insertPanel.add(Box.createRigidArea(new Dimension(0,15)));
        insertPanel.add(einfuegePanel);
        insertPanel.add(Box.createRigidArea(new Dimension(0,15)));
        insertPanel.add(controlButtons);
        insertPanel.add(Box.createRigidArea(new Dimension(0,5)));

        insertWindow.add(insertPanel);

        insertWindow.pack();
    }

    private String removeHTMLFormatting(String eingabe)    {
        char[] chars = eingabe.toCharArray();
        boolean insideFormatting = false;
        String ausgabe = "";

        for(int i = 0; i < chars.length; i++) {
            if (chars[i] == '<')        insideFormatting = true;
            else if(chars[i] == '>')    insideFormatting = false;
            else if(!insideFormatting)  ausgabe += chars[i];
        }

        return ausgabe;
    }

    private LocalDate convertToLocalDate(String dateInput)    {
        char[] datum = dateInput.toCharArray();
        int[] punkte = new int[2];
        int jahr;
        int tag;
        int monat;

        boolean containsDots = false;
        for(int i = 0; i < datum.length; i++)
            if(datum[i] == '.')
                containsDots = true;

        if(containsDots) {
            for (int i = 0; i < datum.length; i++) {
                if (datum[i] == '.') {
                    if (punkte[0] == 0) {
                        punkte[0] = i;
                    } else {
                        punkte[1] = i;
                    }
                }
            }
            String temp = "";
            for (int i = 0; i < punkte[0]; i++)
                temp += datum[i];
            tag = Integer.parseInt(temp);
            temp = "";
            for (int i = punkte[0] + 1; i < punkte[1]; i++)
                temp += datum[i];
            monat = Integer.parseInt(temp);
            temp = "";
            for (int i = punkte[1] + 1; i < datum.length; i++)
                temp += datum[i];
            jahr = Integer.parseInt(temp);
        } else {
            tag = Character.getNumericValue(datum[0])*10 + Character.getNumericValue(datum[1]);
            monat = Character.getNumericValue(datum[2])*10 + Character.getNumericValue(datum[3]);
            jahr = 2000 + Character.getNumericValue(datum[4])*10 + Character.getNumericValue(datum[5]);
        }

        return LocalDate.of(jahr,monat,tag);
    }
}
