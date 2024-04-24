package mvp.view;

import mvp.Presenter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public abstract class ViewOptimalizacia extends JPanel {
    protected final String[] stlpceLinky = {"ID linky", "ID spoja", "Miesto odchodu", "Čas odchodu", "Miesto príchodu", "Čas príchodu", "Dĺžka [km]", "Obsadenosť", "Obslúžený"};
    protected final String[] stlpceIteracie = {"Iterácia", "Čas výpočtu modelu [s]", "Počet porušení prestávok"};
    protected final String[] stlpceTurnusy = {"Turnus", "Zmena", "Čas začiatku", "Čas konca", "Pristavenie", "Odstavenie", "Prázdne prejazdy"};
    protected final String[] stlpceSpoje = {"ID linky", "ID spoja", "Miesto odchodu", "Čas odchodu", "Miesto príchodu", "Čas príchodu",
            "Prejazd pred", "Prejazd po", "Prestávka"};

    protected JTable tableTurnusy;
    protected JScrollPane scrollPaneTurnusy;

    protected JScrollPane scrollPaneSpoje;
    protected JPanel panelSpoje;

    protected JButton buttonVypocitajModel;
    protected JPanel panelVypocetModelu;

    protected JTextArea textArea;
    protected JTabbedPane tabbedPane;

    public ViewOptimalizacia(Presenter presenter)
    {
        inicializuj(presenter);
    }


    //vykreslenia
    public void inicializuj(Presenter presenter)
    {
        tabbedPane = new JTabbedPane();

        inicializujInformacieOmodeli();
        inicializujTurnusy();
        inicializujSpoje();
        inicializujVypocetModelu(presenter);

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
    }

    abstract protected void inicializujVypocetModelu(Presenter presenter);
    abstract protected void inicializujInformacieOmodeli();

    private void inicializujTurnusy() {
        //Turnusy
        JPanel panelTurnusy = new JPanel();
        scrollPaneTurnusy = new javax.swing.JScrollPane();
        javax.swing.GroupLayout panelTurnusyLayout = new javax.swing.GroupLayout(panelTurnusy);
        panelTurnusy.setLayout(panelTurnusyLayout);
        panelTurnusyLayout.setHorizontalGroup(
                panelTurnusyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelTurnusyLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPaneTurnusy, 683, 683, 683)
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

        //turnusy
        tableTurnusy = new JTable(new String[0][7], stlpceTurnusy);
        scrollPaneTurnusy.setVisible(false);
        scrollPaneTurnusy.setViewportView(tableTurnusy);
    }
    private void inicializujSpoje() {
        //Spoje turnusov
        JPanel jpanelSpoje = new JPanel();
        scrollPaneSpoje = new JScrollPane();
        javax.swing.GroupLayout panelSpojeLayout = new javax.swing.GroupLayout(jpanelSpoje);
        jpanelSpoje.setLayout(panelSpojeLayout);
        panelSpojeLayout.setHorizontalGroup(
                panelSpojeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelSpojeLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPaneSpoje, 690, 690, 690)
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

        //spoje turnusov
        this.panelSpoje = new JPanel();
        scrollPaneSpoje.setVisible(false);
        scrollPaneSpoje.setViewportView(this.panelSpoje);
    }

    //vypocty
    abstract protected void vypocitajModel(Presenter presenter);
    protected void vycentrovatUdajeVtabulke(JTable table)
    {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    protected void pridajNecelociselnuKontrolu(JTextField textField) {
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE || c == KeyEvent.VK_PERIOD)) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }
    protected void pridajCelociselnuKontrolu(JTextField textField) {
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }
}