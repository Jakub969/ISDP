package mvp.view;

import mvp.Presenter;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Objects;

public class VievNastavenieUdajov extends JPanel
{
    private javax.swing.JTextField textFieldCkm;
    private javax.swing.JTextField textFieldCvodic;
    private javax.swing.JTextField textFieldDepo;
    private javax.swing.JTextField textFieldIntervalPOJ;
    private javax.swing.JTextField textFieldIntervalBP;
    private javax.swing.JTextField textFieldMaxJazda;
    private javax.swing.JTextField textFieldMaxZmena;
    private javax.swing.JTextField textFieldR1;
    private javax.swing.JTextField textFieldR2;
    private javax.swing.JTextField textFieldSucetBP;
    private javax.swing.JTextField textFieldSucetPOJ;
    private javax.swing.JTextField textFieldTrvanieBP;
    private javax.swing.JTextField textFieldTrvaniePOJ;
    public VievNastavenieUdajov(Presenter presenter)
    {
        inicializuj(presenter);
    }

    public void inicializuj(Presenter presenter)
    {
        JLabel labelNadpisKonstanty = new JLabel();
        JLabel labelDepo = new JLabel();
        this.textFieldCkm = new JTextField();
        textFieldCkm.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel labelR1 = new JLabel();
        this.textFieldR1 = new JTextField();
        textFieldR1.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel labelR2 = new JLabel();
        this.textFieldMaxZmena = new JTextField();
        textFieldMaxZmena.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel labelCvodic = new JLabel();
        this.textFieldMaxJazda = new JTextField();
        textFieldMaxJazda.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel labelCkm = new JLabel();
        this.textFieldDepo = new JTextField();
        textFieldDepo.setHorizontalAlignment(SwingConstants.CENTER);
        this.textFieldTrvanieBP = new JTextField();
        textFieldTrvanieBP.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel labelMaxJazda = new JLabel();
        this.textFieldCvodic = new JTextField();
        textFieldCvodic.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel labelMaxZmena = new JLabel();
        this.textFieldR2 = new JTextField();
        textFieldR2.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel labelTrvanieBP = new JLabel();
        JLabel labelTrvaniePOJ = new JLabel();
        this.textFieldTrvaniePOJ = new JTextField();
        textFieldTrvaniePOJ.setHorizontalAlignment(SwingConstants.CENTER);
        this.textFieldSucetBP = new JTextField();
        textFieldSucetBP.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel labelSucetBP = new JLabel();
        JSeparator separator = new JSeparator();
        JSeparator separatorKonstanty = new JSeparator();
        this.textFieldSucetPOJ = new JTextField();
        textFieldSucetPOJ.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel labelSucetPOJ = new JLabel();
        this.textFieldIntervalBP = new JTextField();
        textFieldIntervalBP.setHorizontalAlignment(SwingConstants.CENTER);
        this.textFieldIntervalPOJ = new JTextField();
        textFieldIntervalPOJ.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel labelIntervalBP = new JLabel();
        JLabel labelIntervalPOJ = new JLabel();
        JButton buttonZmeny = new JButton();
        JLabel labelNadpisSubory = new JLabel();
        JButton buttonMartinVrutky = new JButton();
        JLabel labelMartin = new JLabel();
        JLabel labelVlastneUdaje = new JLabel();
        JButton buttonUseky = new JButton();
        JButton buttonSpoje = new JButton();
        JButton buttonSpojeAuto = new JButton();
        JLabel labelUseky = new JLabel();
        JLabel labelSpoje = new JLabel();
        JLabel labelSpojeAuto = new JLabel();

        setPreferredSize(new java.awt.Dimension(700, 600));

        labelNadpisKonstanty.setFont(new java.awt.Font("sansserif", Font.BOLD, 18)); // NOI18N
        labelNadpisKonstanty.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelNadpisKonstanty.setText("Konštanty");

        labelDepo.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N
        labelDepo.setLabelFor(textFieldDepo);
        labelDepo.setText("Číslo zastávky reprezentujúce depo");

        textFieldCkm.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N

        labelR1.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N
        labelR1.setLabelFor(textFieldR1);
        labelR1.setText("Časová rezerva medzi spojmi [min]");

        textFieldR1.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N

        labelR2.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N
        labelR2.setLabelFor(textFieldR2);
        labelR2.setText("Časová rezerva v depe [min]");

        textFieldMaxZmena.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N

        labelCvodic.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N
        labelCvodic.setLabelFor(textFieldCvodic);
        labelCvodic.setText("Cena vodiča [€ / deň]");

        textFieldMaxJazda.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N

        labelCkm.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N
        labelCkm.setLabelFor(textFieldCkm);
        labelCkm.setText("Cena vozidla za kilometer [€]");

        textFieldDepo.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N

        textFieldTrvanieBP.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N

        labelMaxJazda.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N
        labelMaxJazda.setLabelFor(textFieldMaxJazda);
        labelMaxJazda.setText("Maximálny čas jazdy [min]");

        textFieldCvodic.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N

        labelMaxZmena.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N
        labelMaxZmena.setLabelFor(textFieldMaxZmena);
        labelMaxZmena.setText("Maximálne trvanie zmeny [min]");

        textFieldR2.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N

        labelTrvanieBP.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N
        labelTrvanieBP.setLabelFor(textFieldTrvanieBP);
        labelTrvanieBP.setText("Trvanie bezpečnostnej prestávky [min]");

        labelTrvaniePOJ.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N
        labelTrvaniePOJ.setLabelFor(textFieldTrvaniePOJ);
        labelTrvaniePOJ.setText("Trvanie prestávky na odpočinok [min]");

        textFieldTrvaniePOJ.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N

        textFieldSucetBP.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N

        labelSucetBP.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N
        labelSucetBP.setLabelFor(textFieldSucetBP);
        labelSucetBP.setText("Minimálny súčet trvania BP [min]");

        separatorKonstanty.setOrientation(javax.swing.SwingConstants.VERTICAL);

        textFieldSucetPOJ.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N

        labelSucetPOJ.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N
        labelSucetPOJ.setLabelFor(textFieldSucetPOJ);
        labelSucetPOJ.setText("Minimálny súčet trvania PnOaJ  [min]");

        textFieldIntervalBP.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N

        textFieldIntervalPOJ.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N

        labelIntervalBP.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N
        labelIntervalBP.setLabelFor(textFieldIntervalBP);
        labelIntervalBP.setText("Dĺžka časového okna pre BP [min]");

        labelIntervalPOJ.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N
        labelIntervalPOJ.setLabelFor(textFieldIntervalPOJ);
        labelIntervalPOJ.setText("Dĺžka časového okna pre PnOaJ [min]");

        buttonZmeny.setText("Uložiť zmeny");

        labelNadpisSubory.setFont(new java.awt.Font("sansserif", Font.BOLD, 18)); // NOI18N
        labelNadpisSubory.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelNadpisSubory.setText("Súbory so vstupnými údajmi");

        buttonMartinVrutky.setText("Načítať údaje");

        labelMartin.setFont(new java.awt.Font("sansserif", Font.BOLD, 14)); // NOI18N
        labelMartin.setLabelFor(buttonMartinVrutky);
        labelMartin.setText("Údaje Martin - Vrútky");

        labelVlastneUdaje.setFont(new java.awt.Font("sansserif", Font.BOLD, 14)); // NOI18N
        labelVlastneUdaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelVlastneUdaje.setText("Vlastné údaje");

        buttonUseky.setText("Načítať úseky");
        buttonSpoje.setText("Načítať spoje");
        buttonSpojeAuto.setText("Načítaj dáta(csv, xml, json)");

        labelUseky.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelUseky.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N
        labelUseky.setLabelFor(buttonUseky);
        labelUseky.setText("Súbor nie je vybraný");

        labelSpoje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelSpoje.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N
        labelSpoje.setLabelFor(buttonSpoje);
        labelSpoje.setText("Súbor nie je vybraný");

        labelSpojeAuto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelSpojeAuto.setFont(new java.awt.Font("sansserif", Font.PLAIN, 14)); // NOI18N
        labelSpojeAuto.setLabelFor(buttonSpojeAuto);
        labelSpojeAuto.setText("Súbor nie je vybraný");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(separator, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(labelNadpisKonstanty, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(12, 12, 12)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(labelCvodic, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                .addComponent(textFieldCvodic, 64, 64, 64))
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(labelR2, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                .addComponent(textFieldR2, 64, 64, 64))
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(labelR1, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                .addComponent(textFieldR1, 64, 64, 64))
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(labelDepo, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                .addComponent(textFieldDepo, 64, 64, 64))
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(labelMaxZmena, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(textFieldMaxZmena, 64, 64, 64))
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(labelMaxJazda, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                .addComponent(textFieldMaxJazda, 64, 64, 64))
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(labelCkm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                .addComponent(textFieldCkm, 64, 64, 64)))
                                                                .addGap(15, 15, 15)
                                                                .addComponent(separatorKonstanty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addGroup(layout.createSequentialGroup()
                                                                                        .addComponent(labelIntervalPOJ, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addGap(18, 18, 18)
                                                                                        .addComponent(textFieldIntervalPOJ, 64, 64, 64))
                                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                                                .addComponent(labelIntervalBP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                .addComponent(labelSucetPOJ, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                .addComponent(labelSucetBP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                .addComponent(labelTrvaniePOJ, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                .addComponent(labelTrvanieBP, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE))
                                                                                        .addGap(18, 18, 18)
                                                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                .addComponent(textFieldTrvanieBP, 64, 64, 64)
                                                                                                .addComponent(textFieldTrvaniePOJ, 64, 64, 64)
                                                                                                .addComponent(textFieldSucetBP, 64, 64, 64)
                                                                                                .addComponent(textFieldSucetPOJ, 64, 64, 64)
                                                                                                .addComponent(textFieldIntervalBP, 64, 64, 64))
                                                                                        .addGap(11, 11, 11)))
                                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                                .addComponent(buttonZmeny)
                                                                                .addGap(110, 110, 110)))))
                                                .addGap(0, 5, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(labelNadpisSubory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addGap(67, 67, 67)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(buttonUseky, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(labelUseky, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(labelSpoje, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(buttonSpoje, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(labelSpojeAuto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(buttonSpojeAuto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addComponent(labelVlastneUdaje, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(labelMartin, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(66, 66, 66))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(buttonMartinVrutky)
                                                .addGap(95, 95, 95))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(labelNadpisSubory, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelMartin)
                                        .addComponent(labelVlastneUdaje))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(labelUseky)
                                                        .addComponent(labelSpoje)
                                                        .addComponent(labelSpojeAuto))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, 20)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(buttonUseky)
                                                        .addComponent(buttonSpoje)
                                                        .addComponent(buttonSpojeAuto))
                                                .addGap(36, 36, 36))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(25, 25, 25)
                                                .addComponent(buttonMartinVrutky)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(labelNadpisKonstanty, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(23, 23, 23)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(textFieldDepo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(labelDepo)
                                                                        .addComponent(labelTrvanieBP)
                                                                        .addComponent(textFieldTrvanieBP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(26, 26, 26)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(textFieldR1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(labelR1)
                                                                        .addComponent(labelTrvaniePOJ)
                                                                        .addComponent(textFieldTrvaniePOJ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(26, 26, 26)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(textFieldR2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(labelR2)
                                                                        .addComponent(textFieldSucetBP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(labelSucetBP))
                                                                .addGap(26, 26, 26)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                        .addComponent(textFieldCvodic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(labelCvodic))
                                                                                .addGap(26, 26, 26)
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                        .addComponent(textFieldCkm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(labelCkm))
                                                                                .addGap(26, 26, 26)
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                        .addComponent(textFieldMaxJazda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(labelMaxJazda))
                                                                                .addGap(26, 26, 26)
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                        .addComponent(textFieldMaxZmena, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(labelMaxZmena)))
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                        .addComponent(textFieldSucetPOJ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(labelSucetPOJ))
                                                                                .addGap(26, 26, 26)
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                        .addComponent(textFieldIntervalBP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(labelIntervalBP))
                                                                                .addGap(26, 26, 26)
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                        .addComponent(textFieldIntervalPOJ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(labelIntervalPOJ)))))
                                                        .addComponent(separatorKonstanty, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(buttonZmeny, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(22, 22, 22))
        );

        // ------- KONTROLY --------
        pridajCelociselnuKontrolu(textFieldDepo);
        pridajCelociselnuKontrolu(textFieldR1);
        pridajCelociselnuKontrolu(textFieldR2);
        pridajCelociselnuKontrolu(textFieldCvodic);
        pridajCelociselnuKontrolu(textFieldCkm);
        pridajCelociselnuKontrolu(textFieldMaxJazda);
        pridajCelociselnuKontrolu(textFieldMaxZmena);

        pridajCelociselnuKontrolu(textFieldTrvanieBP);
        pridajCelociselnuKontrolu(textFieldTrvaniePOJ);
        pridajCelociselnuKontrolu(textFieldSucetBP);
        pridajCelociselnuKontrolu(textFieldSucetPOJ);
        pridajCelociselnuKontrolu(textFieldTrvanieBP);
        pridajCelociselnuKontrolu(textFieldTrvaniePOJ);

        // -------
        naplnKonstanty(presenter);

        // ------- AKCIE ----------
        buttonZmeny.addActionListener(evt ->
        {
            JOptionPane.showMessageDialog(this,
                    presenter.nastavKonstanty(
                            Integer.parseInt(textFieldDepo.getText()),
                            Integer.parseInt(textFieldR1.getText()),
                            Integer.parseInt(textFieldR2.getText()),
                            Integer.parseInt(textFieldCvodic.getText()),
                            Integer.parseInt(textFieldCkm.getText()),
                            Integer.parseInt(textFieldMaxJazda.getText()),
                            Integer.parseInt(textFieldMaxZmena.getText()),
                            Integer.parseInt(textFieldTrvanieBP.getText()),
                            Integer.parseInt(textFieldTrvaniePOJ.getText()),
                            Integer.parseInt(textFieldSucetBP.getText()),
                            Integer.parseInt(textFieldSucetPOJ.getText()),
                            Integer.parseInt(textFieldIntervalBP.getText()),
                            Integer.parseInt(textFieldIntervalPOJ.getText())
                    ),
                    "Informácia", JOptionPane.INFORMATION_MESSAGE);
        });
        buttonMartinVrutky.addActionListener(evt ->
        {
            JOptionPane.showMessageDialog(this,
                    presenter.nacitajSpojeUseky("dist.txt", "trips.txt"),
                    "Informácia", JOptionPane.INFORMATION_MESSAGE);
        });
        buttonUseky.addActionListener(evt ->
        {
            JFileChooser jfc = new JFileChooser();
            int returnState = jfc.showOpenDialog(this);
            if (returnState == JFileChooser.APPROVE_OPTION)
            {
                String msg = presenter.nacitajUseky(jfc.getSelectedFile());
                JOptionPane.showMessageDialog(this, msg,"Informácia", JOptionPane.INFORMATION_MESSAGE);
                if(Objects.equals(msg, "Načítanie úsekov bolo úspešné."))
                    labelUseky.setText(jfc.getSelectedFile().getName());
            }
        });
        buttonSpoje.addActionListener(evt ->
        {
            JFileChooser jfc = new JFileChooser();
            int returnState = jfc.showOpenDialog(this);
            if (returnState == JFileChooser.APPROVE_OPTION)
            {
                String msg = presenter.nacitajSpoje(jfc.getSelectedFile());
                JOptionPane.showMessageDialog(this,
                        msg,
                        "Informácia", JOptionPane.INFORMATION_MESSAGE);
                if(Objects.equals(msg, "Načítanie spojov bolo úspešné."))
                    labelSpoje.setText(jfc.getSelectedFile().getName());
            }
        });
        buttonSpojeAuto.addActionListener(evt -> {
            JFileChooser jfc = new JFileChooser();

            jfc.setFileFilter(new FileNameExtensionFilter(
                    "Spoje (txt, csv, xml, json)",
                    "txt", "csv", "xml", "json"
            ));

            int returnState = jfc.showOpenDialog(this);
            if (returnState == JFileChooser.APPROVE_OPTION) {
                File file = jfc.getSelectedFile();

                String msg = presenter.nacitajSpojeAuto(file);

                JOptionPane.showMessageDialog(
                        this,
                        msg,
                        "Informácia",
                        JOptionPane.INFORMATION_MESSAGE
                );

                if (Objects.equals(msg, "Načítanie spojov bolo úspešné."))
                    labelSpojeAuto.setText(file.getName());
            }
        });

    }

    /**
     * Metóda, ktorá kontroluje, či užívateľ do textového poľa zadáva celé číslo.
     * @param textField textfield.
     */
    private void pridajCelociselnuKontrolu(JTextField textField) {
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
    private void naplnKonstanty(Presenter presenter)
    {
        String[] konstanty = new String[13];
        presenter.ziskajKonstanty(konstanty);
        textFieldDepo.setText(konstanty[0]);
        textFieldR1.setText(konstanty[1]);
        textFieldR2.setText(konstanty[2]);
        textFieldCvodic.setText(konstanty[3]);
        textFieldCkm.setText(konstanty[4]);
        textFieldMaxJazda.setText(konstanty[5]);
        textFieldMaxZmena.setText(konstanty[6]);
        textFieldTrvanieBP.setText(konstanty[7]);
        textFieldTrvaniePOJ.setText(konstanty[8]);
        textFieldSucetBP.setText(konstanty[9]);
        textFieldSucetPOJ.setText(konstanty[10]);
        textFieldIntervalBP.setText(konstanty[11]);
        textFieldIntervalPOJ.setText(konstanty[12]);
    }
}