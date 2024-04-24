package mvp.view;

import mvp.Presenter;
import udaje.Linka;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.LinkedHashMap;

public class ViewZobrazenieUdajov extends JPanel
{
    private final String[] stlpceUseky = {"Zastávka 1", "Zastávka 2", "trvanie [min]"};
    private JTable tableUseky;
    private JScrollPane scrollPaneUseky;

    private final String[] stlpceSpoje = {"ID linky", "ID spoja", "Miesto odchodu", "Čas odchodu", "Miesto príchodu", "Čas príchodu", "Dĺžka [km]", "Obsadenosť"};
    private JTable tableSpoje;
    private JScrollPane scrollPaneSpoje;

    private JScrollPane scrollPaneLinky;
    private JPanel panelLinky;

    public ViewZobrazenieUdajov()
    {
        inicializuj();
    }

    public void inicializuj()
    {
        JTabbedPane tabbedPane = new JTabbedPane();

        // useky
        JPanel panelUseky = new JPanel();
        this.scrollPaneUseky = new JScrollPane();

        javax.swing.GroupLayout panelUsekyLayout = new javax.swing.GroupLayout(panelUseky);
        panelUseky.setLayout(panelUsekyLayout);
        panelUsekyLayout.setHorizontalGroup(
                panelUsekyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelUsekyLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPaneUseky, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                                .addContainerGap())
        );
        panelUsekyLayout.setVerticalGroup(
                panelUsekyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelUsekyLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPaneUseky, 595, 595, 595)
                                .addContainerGap())
        );

        tabbedPane.addTab("Úseky", panelUseky);

        // spoje
        JPanel panelSpoje = new JPanel();
        scrollPaneSpoje = new JScrollPane();

        javax.swing.GroupLayout panelSpojeLayout = new javax.swing.GroupLayout(panelSpoje);
        panelSpoje.setLayout(panelSpojeLayout);
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
                                .addComponent(scrollPaneSpoje, 595, 595, 595)
                                .addContainerGap())
        );

        tabbedPane.addTab("Spoje", panelSpoje);

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
                                .addComponent(scrollPaneLinky, 595, 595, 595)
                                .addContainerGap())
        );

        tabbedPane.addTab("Linky", jpanelLinky);

        //celkovo
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tabbedPane)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        //tabulky
        // useky
        tableUseky = new JTable(new String[0][3], stlpceUseky);
        scrollPaneUseky.setViewportView(tableUseky);

        // spoje
        tableSpoje = new JTable(new String[0][8], stlpceSpoje);
        scrollPaneSpoje.setViewportView(tableSpoje);

        // linky
        this.panelLinky = new JPanel();
        scrollPaneLinky.setViewportView(this.panelLinky);
    }

    public void aktualizujUdaje(Presenter presenter)
    {
        // úseky
        String[][] udajeUseky = presenter.getUdajeUseky();
        if(udajeUseky != null)
        {
            scrollPaneUseky.remove(tableUseky);
            tableUseky = new JTable(udajeUseky, stlpceUseky);
            tableUseky.setDefaultEditor(Object.class, null);
            this.vycentrovatUdajeVtabulke(tableUseky);
            scrollPaneUseky.setViewportView(tableUseky);
        }

        // spoje
        String[][] udajeSpoje = presenter.getUdajeSpoje();
        if(udajeSpoje != null)
        {
            scrollPaneSpoje.remove(tableSpoje);
            tableSpoje = new JTable(udajeSpoje, stlpceSpoje);

            tableSpoje.setDefaultEditor(Object.class, null);
            TableColumn firstColumn = tableSpoje.getColumnModel().getColumn(0);
            TableColumn secondColumn = tableSpoje.getColumnModel().getColumn(1);
            firstColumn.setPreferredWidth(40);
            secondColumn.setPreferredWidth(40);
            this.vycentrovatUdajeVtabulke(tableSpoje);
            scrollPaneSpoje.setViewportView(tableSpoje);
        }

        // linky
        LinkedHashMap<Integer, Linka> linky = presenter.getLinky();
        if(linky != null)
        {
            scrollPaneLinky.remove(panelLinky);
            panelLinky = new JPanel();
            panelLinky.setLayout(new BoxLayout(panelLinky, BoxLayout.PAGE_AXIS));

            for (Linka linka : linky.values())
            {
                JPanel panelLinka = new JPanel();
                panelLinka.setLayout(new BorderLayout());

                String[][] udajeSpojeLinky = linka.vypisSpoje();
                JTable tableSpojeLinky = new JTable(udajeSpojeLinky, stlpceSpoje);

                tableSpojeLinky.setDefaultEditor(Object.class, null);
                TableColumn firstColumn = tableSpojeLinky.getColumnModel().getColumn(0);
                TableColumn secondColumn = tableSpojeLinky.getColumnModel().getColumn(1);
                firstColumn.setPreferredWidth(40);
                secondColumn.setPreferredWidth(40);
                this.vycentrovatUdajeVtabulke(tableSpojeLinky);

                JTableHeader tableHeader = tableSpojeLinky.getTableHeader();
                panelLinka.add(tableHeader, BorderLayout.NORTH);

                panelLinka.add(tableSpojeLinky, BorderLayout.CENTER);

                panelLinka.setBorder(BorderFactory.createTitledBorder("Linka " + linka.getID() + ", " + linka.getSpoje().size() + " spojov, celková obsadenosť " + linka.getObsadenost()));

                panelLinky.add(panelLinka);
                panelLinky.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            scrollPaneLinky.setViewportView(panelLinky);
        }
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