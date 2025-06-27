package mvp.view;

import mvp.Presenter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import udaje.Linka;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

public class ViewMinNeobsluzenychCestujucich extends ViewMaxObsadenosti {
    private JTextField textFieldH;

    public ViewMinNeobsluzenychCestujucich(Presenter presenter) {
        super(presenter);
    }

    @Override
    protected void inicializujVypocetModelu(Presenter presenter)
    {
        JLabel labelH = new JLabel();
        this.textFieldH = new JTextField();
        this.pridajNecelociselnuKontrolu(textFieldH);

        JLabel labelPocetBusov = new JLabel();
        this.textFieldPocetBusov = new JTextField();
        this.pridajCelociselnuKontrolu(textFieldPocetBusov);

        JLabel labelCas = new JLabel();
        this.textFieldCas = new JTextField();
        this.pridajCelociselnuKontrolu(textFieldCas);

        JLabel labelGap = new JLabel();
        this.textFieldGap = new JTextField();
        this.pridajNecelociselnuKontrolu(textFieldGap);

        JLabel labelPocetVodicov = new JLabel();
        this.textFieldPocetVodicov = new JTextField();
        this.pridajCelociselnuKontrolu(textFieldPocetVodicov);

        labelH.setText("h");
        labelPocetBusov.setText("Počet busov");
        labelCas.setText("Čas. limit [s]");
        labelGap.setText("GAP [%]");
        labelPocetVodicov.setText("Počet vodičov");

        textFieldCas.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldGap.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldPocetBusov.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldPocetVodicov.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldH.setHorizontalAlignment(SwingConstants.CENTER);

        //Vypocet modelu
        buttonVypocitajModel = new javax.swing.JButton();
        buttonVypocitajModel.setText("Vypočítať model");

        panelVypocetModelu = new javax.swing.JPanel();
        javax.swing.GroupLayout panelVypocetModeluLayout = new javax.swing.GroupLayout(panelVypocetModelu);
        panelVypocetModelu.setLayout(panelVypocetModeluLayout);
        panelVypocetModeluLayout.setHorizontalGroup(
                panelVypocetModeluLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelVypocetModeluLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(labelPocetBusov, 75, 75, 75)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldPocetBusov, 22, 22, 22)
                                .addGap(5, 5, 5)
                                .addComponent(labelPocetVodicov, 85, 85, 85)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldPocetVodicov, 22, 22, 22)
                                .addGap(10, 10, 10)
                                .addComponent(labelH, 10, 10, 10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldH, 40, 40, 40)
                                .addGap(5, 5, 5)
                                .addComponent(labelCas)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldCas, 30, 30, 30)
                                .addGap(5, 5, 5)
                                .addComponent(labelGap, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldGap, 30, 30, 30)
                                .addGap(5, 5, 5)
                                .addComponent(buttonVypocitajModel)
                                .addContainerGap())
        );
        panelVypocetModeluLayout.setVerticalGroup(
                panelVypocetModeluLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelVypocetModeluLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelVypocetModeluLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(buttonVypocitajModel)
                                        .addComponent(labelPocetBusov)
                                        .addComponent(textFieldPocetBusov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(labelH)
                                        .addComponent(textFieldH)
                                        .addComponent(labelCas)
                                        .addComponent(textFieldCas)
                                        .addComponent(labelGap)
                                        .addComponent(textFieldGap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(labelPocetVodicov)
                                        .addComponent(textFieldPocetVodicov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(9, Short.MAX_VALUE))
        );


        //button výpočet modelu
        buttonVypocitajModel.addActionListener(evt ->
        {
            try {
                this.vypocitajModel(presenter);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        //linky
        // linky
        JPanel jpanelLinky = new JPanel();
        scrollPaneLinky = new JScrollPane();

        javax.swing.GroupLayout panelLinkyLayout = new javax.swing.GroupLayout(jpanelLinky);
        jpanelLinky.setLayout(panelLinkyLayout);
        panelLinkyLayout.setHorizontalGroup(
                panelLinkyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelLinkyLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPaneLinky, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                                .addContainerGap())
        );
        panelLinkyLayout.setVerticalGroup(
                panelLinkyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelLinkyLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPaneLinky, 535, 535, 535)
                                .addContainerGap())
        );

        tabbedPane.addTab("Linky", jpanelLinky);
    }

    //vypocet modelu
    @Override
    protected void vypocitajModel(Presenter presenter) throws Exception {
        int pocetBusov;
        int pocetVodicov;
        double h;
        int casLimit;
        double gap;

        String gapString = textFieldGap.getText();
        String casString = textFieldCas.getText();
        try
        {
            pocetBusov = Integer.parseInt(textFieldPocetBusov.getText());
            pocetVodicov = Integer.parseInt(textFieldPocetVodicov.getText());
            h = Double.parseDouble(textFieldH.getText());
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

        if(h < 0 || h > 1)
        {
            JOptionPane.showMessageDialog(this,
                    "Hodnota h musí byť z intervalu <0,1>",
                    "Chyba", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(pocetVodicov < pocetBusov)
        {
            JOptionPane.showMessageDialog(this,
                    "Počet vodičov nesmie byť menší ako počet autobusov!",
                    "Chyba", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<String[]> zmenyUdaje = new ArrayList<>();
        ArrayList<String[][]> spojeUdaje = new ArrayList<>();
        ArrayList<String[]> udajeOTurnusoch = new ArrayList<>();
        ArrayList<String[]> udajeOiteraciach = new ArrayList<>();
        String vysledok = presenter.vykonajMinimalizaciuNeobsluzenychCestujucich(pocetBusov, pocetVodicov,h,
                gap, casLimit, udajeOTurnusoch, zmenyUdaje, spojeUdaje,udajeOiteraciach);
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

        // linky
        LinkedHashMap<Integer, Linka> linky = presenter.getLinky();
        if(linky != null)
        {
            panelLinky = new JPanel();
            panelLinky.setLayout(new BoxLayout(panelLinky, BoxLayout.PAGE_AXIS));

            for (Linka linka : linky.values())
            {
                JPanel panelLinka = new JPanel();
                panelLinka.setLayout(new BorderLayout());

                String[][] udajeSpojeLinky = linka.vypisSpoje();
                JTable tableSpojeLinky = new JTable(udajeSpojeLinky, stlpceLinky);

                tableSpojeLinky.setDefaultEditor(Object.class, null);
                TableColumn firstColumn = tableSpojeLinky.getColumnModel().getColumn(0);
                TableColumn secondColumn = tableSpojeLinky.getColumnModel().getColumn(1);
                firstColumn.setPreferredWidth(40);
                secondColumn.setPreferredWidth(40);
                this.vycentrovatUdajeVtabulke(tableSpojeLinky);

                JTableHeader tableHeader = tableSpojeLinky.getTableHeader();
                panelLinka.add(tableHeader, BorderLayout.NORTH);

                panelLinka.add(tableSpojeLinky, BorderLayout.CENTER);

                panelLinka.setBorder(BorderFactory.createTitledBorder("Linka " + linka.getID()
                        + ", počet všetkých spojov: "+ linka.getSpoje().size()
                        + ", počet obslúžených spojov: " + linka.getPocetObsluzenychSpojov()
                        + ", relatívna obsadenosť: " + String.format("%.4f", linka.getRealnaObsadenost()))
                );

                panelLinky.add(panelLinka);
                panelLinky.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            scrollPaneLinky.setViewportView(panelLinky);
        }
        vykresliGrafy(linky, udajeOiteraciach);
    }

    private void vykresliGrafy(LinkedHashMap<Integer, Linka> linky, ArrayList<String[]> iteracie) {
        JPanel panelGrafy = new JPanel();
        panelGrafy.setLayout(new BoxLayout(panelGrafy, BoxLayout.Y_AXIS));

        // 1. Bar chart – obslúžené spoje na linku
        DefaultCategoryDataset datasetBar = new DefaultCategoryDataset();
        int obsluzenychCelkovo = 0;
        int neobsluzenychCelkovo = 0;
        for (Linka linka : linky.values()) {
            int obsluzene = linka.getPocetObsluzenychSpojov();
            int vsetky = linka.getSpoje().size();
            obsluzenychCelkovo += obsluzene;
            neobsluzenychCelkovo += (vsetky - obsluzene);
            datasetBar.addValue(obsluzene, "Obslúžené spoje", "Linka " + linka.getID());
        }
        JFreeChart barChart = ChartFactory.createBarChart(
                "Počet obslúžených spojov na linku",
                "Linka", "Počet spojov",
                datasetBar, PlotOrientation.VERTICAL, true, true, false);
        panelGrafy.add(new ChartPanel(barChart));

        // 2. Pie chart – obslúžené vs. neobslúžené
        DefaultPieDataset datasetPie = new DefaultPieDataset();
        datasetPie.setValue("Obslúžené", obsluzenychCelkovo);
        datasetPie.setValue("Neobslúžené", neobsluzenychCelkovo);
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Podiel obslúžených vs. neobslúžených spojov",
                datasetPie, true, true, false);
        panelGrafy.add(new ChartPanel(pieChart));

        // Zobrazenie všetkých grafov
        scrollPaneGrafy.setViewportView(panelGrafy);
        scrollPaneGrafy.setVisible(true);
    }

}
