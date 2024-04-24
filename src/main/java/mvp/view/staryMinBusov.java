

/*
package mvp.view;

import mvp.Presenter;
import udaje.Linka;
import udaje.Turnus;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

public class ViewMinBusov extends JPanel {
    private final String[] stlpceTurnusy = {"Turnus", "Čas začiatku", "Čas konca", "Pristavenie", "Odstavenie", "Prázdne prejazdy"};
    private final String[] stlpceSpoje = {"ID linky", "ID spoja", "Miesto odchodu", "Čas odchodu", "Miesto príchodu", "Čas príchodu",
    "Prejazd pred", "Prejazd po", "Prestávka"};

    private JTable tableTurnusy;
    private JScrollPane scrollPaneTurnusy;

    private JScrollPane scrollPaneSpoje;
    private JPanel panelSpoje;

    private javax.swing.JButton buttonVypocitajModel;
    private javax.swing.JPanel panelVypocetModelu;

    private JTextArea textArea;

    public ViewMinBusov(Presenter presenter)
    {
        inicializuj(presenter);
    }

    public void inicializuj(Presenter presenter)
    {
        JTabbedPane tabbedPane = new JTabbedPane();

        //Vypocet modelu
        buttonVypocitajModel = new javax.swing.JButton();
        buttonVypocitajModel.setText("Vypočítať model");

        panelVypocetModelu = new javax.swing.JPanel();
        javax.swing.GroupLayout panelVypocetModeluLayout = new javax.swing.GroupLayout(panelVypocetModelu);
        panelVypocetModelu.setLayout(panelVypocetModeluLayout);
        panelVypocetModeluLayout.setHorizontalGroup(
                panelVypocetModeluLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelVypocetModeluLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(buttonVypocitajModel)
                                .addGap(280, 280, 280))
        );
        panelVypocetModeluLayout.setVerticalGroup(
                panelVypocetModeluLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelVypocetModeluLayout.createSequentialGroup()
                                .addContainerGap(9, Short.MAX_VALUE)
                                .addComponent(buttonVypocitajModel)
                                .addContainerGap())
        );

        //
        textArea = new JTextArea();
        JScrollPane scrollPaneTextArea = new JScrollPane();
        JPanel panelinfo = new JPanel();

        javax.swing.GroupLayout panelinfoLayout = new javax.swing.GroupLayout(panelinfo);
        panelinfo.setLayout(panelinfoLayout);
        panelinfoLayout.setHorizontalGroup(
                panelinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelinfoLayout.createSequentialGroup()
                                .addGap(175, 175, 175)
                                .addComponent(scrollPaneTextArea, 350, 350, 350)
                                .addContainerGap(195, 195))
        );
        panelinfoLayout.setVerticalGroup(
                panelinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelinfoLayout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addComponent(scrollPaneTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(504, 504))
        );
        textArea.setColumns(20);
        textArea.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14));
        textArea.setRows(5);
        scrollPaneTextArea.setViewportView(textArea);

        tabbedPane.addTab("Informácie o modeli", panelinfo);
        //Turnusy
        JPanel panelTurnusy = new JPanel();
        scrollPaneTurnusy = new javax.swing.JScrollPane();
        javax.swing.GroupLayout panelTurnusyLayout = new javax.swing.GroupLayout(panelTurnusy);
        panelTurnusy.setLayout(panelTurnusyLayout);
        panelTurnusyLayout.setHorizontalGroup(
                panelTurnusyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelTurnusyLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPaneTurnusy, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                                .addContainerGap())
        );
        panelTurnusyLayout.setVerticalGroup(
                panelTurnusyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelTurnusyLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPaneTurnusy, 537, 537, 537)
                                .addContainerGap())
        );

        tabbedPane.addTab("Turnusy", panelTurnusy);

        //Spoje turnusov
        JPanel jpanelSpoje = new JPanel();
        scrollPaneSpoje = new JScrollPane();
        javax.swing.GroupLayout panelSpojeLayout = new javax.swing.GroupLayout(jpanelSpoje);
        jpanelSpoje.setLayout(panelSpojeLayout);
        panelSpojeLayout.setHorizontalGroup(
                panelSpojeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelSpojeLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPaneSpoje, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                                .addContainerGap())
        );
        panelSpojeLayout.setVerticalGroup(
                panelSpojeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelSpojeLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPaneSpoje, 537, 537, 537)
                                .addContainerGap())
        );

        tabbedPane.addTab("Spoje turnusov", jpanelSpoje);

        //celkovo
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tabbedPane)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panelVypocetModelu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panelVypocetModelu, 40, 40,40)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 719, Short.MAX_VALUE))
        );

        //tabulky
        //turnusy
        tableTurnusy = new JTable(new String[0][6], stlpceTurnusy);
        scrollPaneTurnusy.setViewportView(tableTurnusy);

        //spoje turnusov
        this.panelSpoje = new JPanel();
        scrollPaneSpoje.setViewportView(this.panelSpoje);

        //button výpočet modelu
        buttonVypocitajModel.addActionListener(evt ->
        {
            this.vypocitajModel(presenter);
        });
    }

    public void vypocitajModel(Presenter presenter)
    {
        ArrayList<String[]> turnusyUdaje = new ArrayList<>();
        ArrayList<String[][]> spojeUdaje = new ArrayList<>();
        ArrayList<String[]> udajeOturnusoch = new ArrayList<>();
        String vysledok = presenter.vykonajMinimalizaciuAutobusov(udajeOturnusoch, turnusyUdaje, spojeUdaje);
        if(Objects.equals(vysledok, "Chyba pri riešení modelu!"))
        {
            JOptionPane.showMessageDialog(this,
                    "Nastala chyba pri riešení modelu!",
                    "Chyba", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //výpis info o modeli
        textArea.setText(vysledok);

        //výpis turnusov
        String[][] turnusyPole = turnusyUdaje.toArray(new String[turnusyUdaje.size()][]);
        scrollPaneTurnusy.remove(tableTurnusy);
        tableTurnusy = new JTable(turnusyPole, stlpceTurnusy);
        tableTurnusy.setDefaultEditor(Object.class, null);
        this.vycentrovatUdajeVtabulke(tableTurnusy);
        scrollPaneTurnusy.setViewportView(tableTurnusy);

        //výpis spojov v turnusoch
        scrollPaneSpoje.remove(panelSpoje);
        panelSpoje = new JPanel();
        panelSpoje.setLayout(new BoxLayout(panelSpoje, BoxLayout.PAGE_AXIS));

        for (int i = 0; i < udajeOturnusoch.size(); i++)
        {
            String[][] turnus = spojeUdaje.get(i);
            String[] udajeOturnuse = udajeOturnusoch.get(i);

            JPanel panelTurnus = new JPanel();
            panelTurnus.setLayout(new BorderLayout());

            JTable tableSpojeTurnusu = new JTable(turnus, stlpceSpoje);
            tableSpojeTurnusu.setDefaultEditor(Object.class, null);
            TableColumn firstColumn = tableSpojeTurnusu.getColumnModel().getColumn(0);
            TableColumn secondColumn = tableSpojeTurnusu.getColumnModel().getColumn(1);
            TableColumn thirdColumn = tableSpojeTurnusu.getColumnModel().getColumn(2);
            TableColumn fifthColumn = tableSpojeTurnusu.getColumnModel().getColumn(4);
            firstColumn.setPreferredWidth(50);
            secondColumn.setPreferredWidth(50);
            thirdColumn.setPreferredWidth(87);
            fifthColumn.setPreferredWidth(87);
            this.vycentrovatUdajeVtabulke(tableSpojeTurnusu);

            JTableHeader tableHeader = tableSpojeTurnusu.getTableHeader();
            panelTurnus.add(tableHeader, BorderLayout.NORTH);

            panelTurnus.add(tableSpojeTurnusu, BorderLayout.CENTER);

            panelTurnus.setBorder(BorderFactory.createTitledBorder("Turnus: " + udajeOturnuse[0] + ", trvanie turnusu: " + udajeOturnuse[2]));

            panelSpoje.add(panelTurnus);
            panelSpoje.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        scrollPaneSpoje.setViewportView(panelSpoje);

    }

    private void vycentrovatUdajeVtabulke(JTable table)
    {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}

 */