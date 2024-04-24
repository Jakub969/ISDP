package mvp.view;

import mvp.Presenter;

import javax.swing.*;
import java.util.ArrayList;

public class ViewExperiment extends JPanel {
    protected final String[] stlpceExp = {"Počet busov", "Počet vodičov", "h", "GAP", "Čas", "Počet obs. spojov", "GAP", "Čas"};

    private final JTextField textFieldPocetBusov;
    private final JTextField textFieldCas;
    private final JTextField textFieldGap;
    private final JTextField textFieldPocetVodicov;
    private final JScrollPane scrollPaneTabulka;
    private JTable tableTabulka;

    public ViewExperiment(Presenter presenter)
    {
        JPanel panelVypocetModelu = new javax.swing.JPanel();
        JButton buttonVykonajExperiment = new javax.swing.JButton();
        JLabel labelPocetBusov = new javax.swing.JLabel();
        textFieldPocetBusov = new javax.swing.JTextField();
        JLabel labelCas = new javax.swing.JLabel();
        textFieldCas = new javax.swing.JTextField();
        JLabel labelGap = new javax.swing.JLabel();
        textFieldGap = new javax.swing.JTextField();
        JLabel labelPocetVodicov = new javax.swing.JLabel();
        this.textFieldPocetVodicov = new javax.swing.JTextField();
        JPanel panelTabulka = new JPanel();
        scrollPaneTabulka = new JScrollPane();
        tableTabulka = new JTable();

        buttonVykonajExperiment.setText("Vykonaj experiment");
        labelPocetBusov.setText("Počet autobusov");
        labelCas.setText("Časový limit [s]");
        labelGap.setText("GAP [%]");
        labelPocetVodicov.setText("Počet vodičov");

        javax.swing.GroupLayout panelVykonanieExperimentuLayout = new javax.swing.GroupLayout(panelVypocetModelu);
        panelVypocetModelu.setLayout(panelVykonanieExperimentuLayout);
        panelVykonanieExperimentuLayout.setHorizontalGroup(
                panelVykonanieExperimentuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelVykonanieExperimentuLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(labelPocetBusov, 100, 100, 100)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldPocetBusov, 25, 25, 25)
                                .addGap(10, 10, 10)
                                .addComponent(labelPocetVodicov, 85, 85, 85)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldPocetVodicov, 25, 25, 25)
                                .addGap(10, 10, 10)
                                .addComponent(labelCas)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldCas, 35, 35, 35)
                                .addGap(10, 10, 10)
                                .addComponent(labelGap, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldGap, 35, 35, 35)
                                .addGap(10, 10, 10)
                                .addComponent(buttonVykonajExperiment)
                                .addContainerGap())
        );
        panelVykonanieExperimentuLayout.setVerticalGroup(
                panelVykonanieExperimentuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelVykonanieExperimentuLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelVykonanieExperimentuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(buttonVykonajExperiment)
                                        .addComponent(labelPocetBusov)
                                        .addComponent(textFieldPocetBusov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(labelCas)
                                        .addComponent(textFieldCas)
                                        .addComponent(labelGap)
                                        .addComponent(textFieldGap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(labelPocetVodicov)
                                        .addComponent(textFieldPocetVodicov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(9, Short.MAX_VALUE))
        );

        scrollPaneTabulka.setViewportView(tableTabulka);

        javax.swing.GroupLayout panelTabulkaLayout = new javax.swing.GroupLayout(panelTabulka);
        panelTabulka.setLayout(panelTabulkaLayout);
        panelTabulkaLayout.setHorizontalGroup(
                panelTabulkaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelTabulkaLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(scrollPaneTabulka, 671, 671, 671)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelTabulkaLayout.setVerticalGroup(
                panelTabulkaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelTabulkaLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(scrollPaneTabulka, 523, 523, 523)
                                .addContainerGap(171, 171))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(panelTabulka, 700,700,700)
                                        .addComponent(panelVypocetModelu, 700,700,700))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panelVypocetModelu, 40, 40,40)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelTabulka, 730, 730, 730)
                                .addContainerGap())
        );

        //button výpočet modelu
        buttonVykonajExperiment.addActionListener(evt ->
        {
            this.vykonajExperiment(presenter);
        });
    }

    private void vykonajExperiment(Presenter presenter)
    {
        int pocetBusov;
        int pocetVodicov;
        int casLimit;
        double gap;

        String gapString = textFieldGap.getText();
        String casString = textFieldCas.getText();
        try
        {
            pocetBusov = Integer.parseInt(textFieldPocetBusov.getText());
            pocetVodicov = Integer.parseInt(textFieldPocetVodicov.getText());
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

        if(pocetVodicov < pocetBusov)
        {
            JOptionPane.showMessageDialog(this,
                    "Počet vodičov nesmie byť menší ako počet autobusov!",
                    "Chyba", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<String[]> infoObehoch = new ArrayList<>();
        presenter.vykonajExperiment(infoObehoch, pocetBusov, pocetVodicov, casLimit, gap);

        scrollPaneTabulka.remove(tableTabulka);
        tableTabulka = new JTable(infoObehoch.toArray(new String[0][]), stlpceExp);
        scrollPaneTabulka.setViewportView(tableTabulka);
    }
}
