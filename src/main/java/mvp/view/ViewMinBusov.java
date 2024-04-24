package mvp.view;

import mvp.Presenter;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class ViewMinBusov extends ViewOptimalizacia {

    public ViewMinBusov(Presenter presenter) {
        super(presenter);
    }

    @Override
    protected void inicializujVypocetModelu(Presenter presenter)
    {
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

        //button výpočet modelu
        buttonVypocitajModel.addActionListener(evt ->
        {
            this.vypocitajModel(presenter);
        });
    }

    @Override
    protected void inicializujInformacieOmodeli()
    {
        //
        textArea = new JTextArea();
        textArea.setEditable(false);
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
    }

    @Override
    protected void vypocitajModel(Presenter presenter)
    {
        ArrayList<String[]> turnusyUdaje = new ArrayList<>();
        ArrayList<String[][]> spojeUdaje = new ArrayList<>();
        ArrayList<String[]> udajeOturnusoch = new ArrayList<>();
        String vysledok = presenter.vykonajMinimalizaciuAutobusov(udajeOturnusoch, turnusyUdaje, spojeUdaje);
        if(Objects.equals(vysledok, "Chyba pri riešení modelu!") || Objects.equals(vysledok, "Model nemá riešenie!"))
        {
            JOptionPane.showMessageDialog(this,
                    vysledok,
                    "Chyba", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //výpis info o modeli
        textArea.setText(vysledok);

        //výpis turnusov
        String[][] turnusyPole = turnusyUdaje.toArray(new String[turnusyUdaje.size()][]);
        scrollPaneTurnusy.setVisible(true);
        scrollPaneTurnusy.remove(tableTurnusy);
        tableTurnusy = new JTable(turnusyPole, stlpceTurnusy);
        tableTurnusy.setDefaultEditor(Object.class, null);
        this.vycentrovatUdajeVtabulke(tableTurnusy);
        scrollPaneTurnusy.setViewportView(tableTurnusy);

        //výpis spojov v turnusoch
        scrollPaneSpoje.setVisible(true);
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
}
