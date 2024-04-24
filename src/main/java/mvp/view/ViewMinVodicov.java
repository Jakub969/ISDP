package mvp.view;

import mvp.Presenter;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class ViewMinVodicov extends ViewOptimalizacia {
    protected JTable tableIteracie;
    protected JScrollPane scrollPaneIteracie;

    protected JTextField textFieldPocetBusov;
    protected JTextField textFieldCas;
    protected JTextField textFieldGap;

    public ViewMinVodicov(Presenter presenter) {
        super(presenter);
    }

    @Override
    protected void inicializujVypocetModelu(Presenter presenter)
    {
        JLabel labelPocetBusov = new JLabel();
        this.textFieldPocetBusov = new JTextField();
        this.pridajCelociselnuKontrolu(textFieldPocetBusov);
        JLabel labelCas = new JLabel();
        this.textFieldCas = new JTextField();
        this.pridajCelociselnuKontrolu(textFieldCas);
        JLabel labelGap = new JLabel();
        this.textFieldGap = new JTextField();
        this.pridajNecelociselnuKontrolu(textFieldGap);

        labelPocetBusov.setText("Počet autobusov");
        labelCas.setText("Časový limit [s]");
        labelGap.setText("GAP [%]");

        //Vypocet modelu
        buttonVypocitajModel = new javax.swing.JButton();
        buttonVypocitajModel.setText("Vypočítať model");

        panelVypocetModelu = new javax.swing.JPanel();
        javax.swing.GroupLayout panelVypocetModeluLayout = new javax.swing.GroupLayout(panelVypocetModelu);
        panelVypocetModelu.setLayout(panelVypocetModeluLayout);
        panelVypocetModeluLayout.setHorizontalGroup(
                panelVypocetModeluLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelVypocetModeluLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(labelPocetBusov, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(textFieldPocetBusov, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(labelCas)
                                .addGap(10, 10, 10)
                                .addComponent(textFieldCas, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(labelGap, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(textFieldGap, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(buttonVypocitajModel)
                                .addGap(13, 13, 13))
        );
        panelVypocetModeluLayout.setVerticalGroup(
                panelVypocetModeluLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelVypocetModeluLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelVypocetModeluLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(buttonVypocitajModel)
                                        .addComponent(labelPocetBusov)
                                        .addComponent(textFieldPocetBusov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(labelCas)
                                        .addComponent(textFieldCas)
                                        .addComponent(labelGap)
                                        .addComponent(textFieldGap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(9, Short.MAX_VALUE))
        );

        textFieldCas.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldGap.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldPocetBusov.setHorizontalAlignment(SwingConstants.CENTER);
        //button výpočet modelu
        buttonVypocitajModel.addActionListener(evt ->
        {
            this.vypocitajModel(presenter);
        });
    }

    @Override
    protected void inicializujInformacieOmodeli()
    {
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPaneTextArea = new JScrollPane();
        JPanel panelinfo = new JPanel();

        scrollPaneIteracie = new javax.swing.JScrollPane();
        tableIteracie = new JTable(new String[0][3], stlpceIteracie);
        tableIteracie.setDefaultEditor(Object.class, null);
        TableColumn firstColumn = tableIteracie.getColumnModel().getColumn(0);
        firstColumn.setMaxWidth(60);
        this.vycentrovatUdajeVtabulke(tableIteracie);
        scrollPaneIteracie.setViewportView(tableIteracie);

        javax.swing.GroupLayout panelinfoLayout = new javax.swing.GroupLayout(panelinfo);
        panelinfo.setLayout(panelinfoLayout);
        panelinfoLayout.setHorizontalGroup(
                panelinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelinfoLayout.createSequentialGroup()
                                .addGap(175, 175, 175)
                                .addGroup(panelinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(scrollPaneIteracie, 350, 350, 350)
                                        .addComponent(scrollPaneTextArea, 350, 350, 350))
                                .addContainerGap(195, 195))
        );
        panelinfoLayout.setVerticalGroup(
                panelinfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelinfoLayout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addComponent(scrollPaneTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(scrollPaneIteracie, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(201, Short.MAX_VALUE))
        );

        textArea.setColumns(20);
        textArea.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14));
        textArea.setRows(5);
        scrollPaneTextArea.setViewportView(textArea);

        tabbedPane.addTab("Informácie o modeli", panelinfo);
    }

    //vypocet modelu
    @Override
    protected void vypocitajModel(Presenter presenter)
    {
        int pocetBusov;
        int casLimit;
        double gap;

        String gapString = textFieldGap.getText();
        String casString = textFieldCas.getText();
        try
        {
            pocetBusov = Integer.parseInt(textFieldPocetBusov.getText());
            if(gapString.isEmpty())
                gap = 0;
            else
                gap = Double.parseDouble(gapString) / 100;
            if(casString.isEmpty())
                casLimit = -1;
            else
                casLimit = Integer.parseInt(textFieldCas.getText());
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this,
                    "Chybné parametre",
                    "Chyba", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ArrayList<String[]> zmenyUdaje = new ArrayList<>();
        ArrayList<String[][]> spojeUdaje = new ArrayList<>();
        ArrayList<String[]> udajeOTurnusoch = new ArrayList<>();
        ArrayList<String[]> udajeOiteraciach = new ArrayList<>();
        String vysledok = presenter.vykonajMinimalizaciuVodicov(pocetBusov, gap, casLimit, udajeOTurnusoch, zmenyUdaje, spojeUdaje,udajeOiteraciach);
        if(Objects.equals(vysledok, "Chyba pri riešení modelu!") || Objects.equals(vysledok, "Model nemá riešenie!"))
        {
            JOptionPane.showMessageDialog(this,
                    vysledok,
                    "Chyba", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //výpis info o modeli
        textArea.setText(vysledok);
        scrollPaneIteracie.remove(tableIteracie);
        tableIteracie = new JTable(udajeOiteraciach.toArray(new String[0][]), stlpceIteracie);
        tableIteracie.setDefaultEditor(Object.class, null);
        TableColumn firstCol = tableIteracie.getColumnModel().getColumn(0);
        firstCol.setMaxWidth(60);
        this.vycentrovatUdajeVtabulke(tableIteracie);
        scrollPaneIteracie.setViewportView(tableIteracie);

        //výpis zmien
        String[][] zmenyPole = zmenyUdaje.toArray(new String[zmenyUdaje.size()][]);
        scrollPaneTurnusy.setVisible(true);
        scrollPaneTurnusy.remove(tableTurnusy);
        tableTurnusy = new JTable(zmenyPole, stlpceTurnusy);
        tableTurnusy.setDefaultEditor(Object.class, null);
        this.vycentrovatUdajeVtabulke(tableTurnusy);
        scrollPaneTurnusy.setViewportView(tableTurnusy);

        //výpis spojov v zmenách
        scrollPaneSpoje.setVisible(true);
        scrollPaneSpoje.remove(panelSpoje);
        panelSpoje = new JPanel();
        panelSpoje.setLayout(new BoxLayout(panelSpoje, BoxLayout.PAGE_AXIS));

        int count = 0;
        for (int i = 0; i < udajeOTurnusoch.size(); i++)
        {
            JPanel panelTurnus = new JPanel();
            String[] udajeOturnuse = udajeOTurnusoch.get(i);
            panelTurnus.setBorder(BorderFactory.createTitledBorder("Turnus: " + udajeOturnuse[0] + ", trvanie turnusu: " + udajeOturnuse[2]));
            panelTurnus.setLayout(new BoxLayout(panelTurnus, BoxLayout.PAGE_AXIS));
            int newCount = count + Integer.parseInt(udajeOturnuse[1]);
            //
            for (int j = count; j < newCount; j++)
            {
                JPanel panelZmena = new JPanel();
                String[][] zmena = spojeUdaje.get(j);
                panelZmena.setBorder(BorderFactory.createTitledBorder("Zmena: " + zmenyPole[j][1] +
                        ", trvanie zmeny: " + zmenyPole[j][7] + ", trvanie jazdy: " + zmenyPole[j][8]));
                panelZmena.setLayout(new BorderLayout());

                JTable tableSpojeTurnusu = new JTable(zmena, stlpceSpoje);
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
                panelZmena.add(tableHeader, BorderLayout.NORTH);
                panelZmena.add(tableSpojeTurnusu, BorderLayout.CENTER);
                panelTurnus.add(panelZmena);
                if(j == count)
                    panelTurnus.add(Box.createRigidArea(new Dimension(0, 5)));
            }
            count = newCount;

            //

            panelSpoje.add(panelTurnus);
            panelSpoje.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        scrollPaneSpoje.setViewportView(panelSpoje);
    }
}
