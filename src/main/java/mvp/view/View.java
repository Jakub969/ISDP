package mvp.view;

import mvp.Presenter;

import javax.swing.*;
import java.awt.*;

public class View {
    private final Presenter presenter;
    private final JPanel panel;
    private final CardLayout cardLayout;
    private final VievNastavenieUdajov vievNastavenieUdajov;
    private final ViewZobrazenieUdajov viewZobrazenieUdajov;
    private final ViewMinBusov viewMinBusov;
    private final ViewMinVodicov viewMinVodicov;
    private final ViewMaxObsadenosti viewMaxObsadenosti;
    private final ViewMaxObsluzenychSpojov viewMaxObsluzenychSpojov;
    private final ViewMinNeobsluzenychCestujucich viewMinNeobsluzenychCestujucich;
    private final ViewExperiment viewExperiment;
    public View() {
        this.presenter = new Presenter();

        JFrame frame = new JFrame("Aplikácia");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(950, 700);
        frame.setLocationRelativeTo(null);

        this.panel = new JPanel();
        this.cardLayout = new CardLayout();
        this.panel.setLayout(this.cardLayout);

        // Vytvorenie prvého panelu
        this.vievNastavenieUdajov = new VievNastavenieUdajov(presenter);
        this.panel.add(vievNastavenieUdajov, "Nastavenie vstupných údajov");

        // Vytvorenie druhého panelu
        this.viewZobrazenieUdajov = new ViewZobrazenieUdajov();
        this.panel.add(viewZobrazenieUdajov, "Zobrazenie vstupných údajov");

        // Vytvorenie tretieho panelu
        this.viewMinBusov = new ViewMinBusov(presenter);
        this.panel.add(viewMinBusov, "Minimalizácia počtu autobusov");

        // Vytvorenie štvrtého panelu
        this.viewMinVodicov = new ViewMinVodicov(presenter);
        this.panel.add(viewMinVodicov, "Minimalizácia počtu vodičov");

        // Vytvorenie piateho panelu
        this.viewMaxObsadenosti = new ViewMaxObsadenosti(presenter);
        this.panel.add(viewMaxObsadenosti, "Maximalizácia obsadenosti");

        // Vytvorenie 6. panelu
        this.viewMaxObsluzenychSpojov = new ViewMaxObsluzenychSpojov(presenter);
        this.panel.add(viewMaxObsluzenychSpojov, "Maximalizácia obslúžených spojov");

        // Vytvorenie 7. panelu
        this.viewMinNeobsluzenychCestujucich = new ViewMinNeobsluzenychCestujucich(presenter);
        this.panel.add(viewMinNeobsluzenychCestujucich, "Minimalizácia neobslúžených cestujúcich");

        // Vytvorenie 8. panelu
        this.viewExperiment = new ViewExperiment(presenter);
        this.panel.add(viewExperiment, "Experiment");

        frame.add(this.panel);
        frame.setJMenuBar(vytvorMenuBar());

        frame.setVisible(true);
    }

    // Vytvoření menu
    private JMenuBar vytvorMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");

        JMenuItem menuItem1 = new JMenuItem("Nastavenie vstupných údajov");
        menuItem1.addActionListener(e -> {
            cardLayout.show(panel, "Nastavenie vstupných údajov");
        });
        menu.add(menuItem1);

        JMenuItem menuItem2 = new JMenuItem("Zobrazenie vstupných údajov");
        menuItem2.addActionListener(e -> {
            if(this.presenter.jeProstrediePripravene())
            {
                cardLayout.show(panel, "Zobrazenie vstupných údajov");
                viewZobrazenieUdajov.aktualizujUdaje(presenter);
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Údaje nie sú načítané!",
                        "Chyba", JOptionPane.WARNING_MESSAGE);
            }
        });
        menu.add(menuItem2);

        JMenuItem menuItem3 = new JMenuItem("Minimalizácia počtu autobusov");
        menuItem3.addActionListener(e -> {
            if(this.presenter.jeProstrediePripravene())
            {
                cardLayout.show(panel, "Minimalizácia počtu autobusov");
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Údaje nie sú načítané!",
                        "Chyba", JOptionPane.WARNING_MESSAGE);
            }
        });
        menu.add(menuItem3);

        JMenuItem menuItem4 = new JMenuItem("Minimalizácia počtu vodičov");
        menuItem4.addActionListener(e -> {
            if(this.presenter.jeProstrediePripravene())
            {
                cardLayout.show(panel, "Minimalizácia počtu vodičov");
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Údaje nie sú načítané!",
                        "Chyba", JOptionPane.WARNING_MESSAGE);
            }
        });
        menu.add(menuItem4);

        JMenuItem menuItem5 = new JMenuItem("Maximalizácia obsadenosti");
        menuItem5.addActionListener(e -> {
            if(this.presenter.jeProstrediePripravene())
            {
                cardLayout.show(panel, "Maximalizácia obsadenosti");
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Údaje nie sú načítané!",
                        "Chyba", JOptionPane.WARNING_MESSAGE);
            }
        });
        menu.add(menuItem5);

        JMenuItem menuItem6 = new JMenuItem("Maximalizácia obslúžených spojov");
        menuItem6.addActionListener(e -> {
            if(this.presenter.jeProstrediePripravene())
            {
                cardLayout.show(panel, "Maximalizácia obslúžených spojov");
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Údaje nie sú načítané!",
                        "Chyba", JOptionPane.WARNING_MESSAGE);
            }
        });
        menu.add(menuItem6);

        JMenuItem menuItem7 = new JMenuItem("Minimalizácia neobslúžených cestujúcich");
        menuItem7.addActionListener(e -> {
            if(this.presenter.jeProstrediePripravene())
            {
                cardLayout.show(panel, "Minimalizácia neobslúžených cestujúcich");
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Údaje nie sú načítané!",
                        "Chyba", JOptionPane.WARNING_MESSAGE);
            }
        });
        menu.add(menuItem7);

        JMenuItem menuItem8 = new JMenuItem("Experiment");
        menuItem8.addActionListener(e -> {
            if(this.presenter.jeProstrediePripravene())
            {
                cardLayout.show(panel, "Experiment");
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Údaje nie sú načítané!",
                        "Chyba", JOptionPane.WARNING_MESSAGE);
            }
        });
        menu.add(menuItem8);

        menuBar.add(menu);
        return menuBar;
    }
}