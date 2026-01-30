package subory;

import mvp.Model;
import udaje.Dvojica;
import udaje.Linka;
import udaje.Spoj;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
 * Načítavač údajov z Plain Text formátu.
 * Formát úsekov: u v c (oddelené medzerami)
 * Formát spojov: idLinky idSpoja miestoOdchodu casOdchodu miestoPrichodu casPrichodu trvanie dlzka obsadenost
 */
public class PlainTextDataLoader implements IDataLoader {
    
    @Override
    public void nacitajUseky(File suborUseky, LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky) throws Exception {
        Scanner scanner = new Scanner(suborUseky);
        nacitajUsekyZoScannera(scanner, useky);
    }
    
    @Override
    public void nacitajUseky(InputStream inputStream, LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky) throws Exception {
        Scanner scanner = new Scanner(new InputStreamReader(inputStream));
        nacitajUsekyZoScannera(scanner, useky);
    }
    
    private void nacitajUsekyZoScannera(Scanner scanner, LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky) {
        while (scanner.hasNextLine()) {
            String riadok = scanner.nextLine().trim();
            if (riadok.isEmpty() || riadok.startsWith("#")) {
                continue; // preskočiť prázdne riadky a komentáre
            }
            
            String[] stlpce = riadok.split("\\s+");
            if (stlpce.length < 3) {
                continue; // neplatný riadok
            }
            
            int u = Integer.parseInt(stlpce[0]);
            int v = Integer.parseInt(stlpce[1]);
            int c = Integer.parseInt(stlpce[2]);

            useky.put(new Dvojica<>(u, v), c);
            useky.put(new Dvojica<>(v, u), c);
            useky.put(new Dvojica<>(u, u), 0);
            useky.put(new Dvojica<>(v, v), 0);
        }
        scanner.close();
    }
    
    @Override
    public void nacitajSpoje(File suborSpoje, LinkedHashMap<Integer, Spoj> spoje,
                             LinkedHashMap<Integer, Linka> linky) throws Exception {
        Scanner scanner = new Scanner(suborSpoje);
        nacitajSpojeZoScannera(scanner, spoje, linky);
    }
    
    @Override
    public void nacitajSpoje(InputStream inputStream, LinkedHashMap<Integer, Spoj> spoje,
                             LinkedHashMap<Integer, Linka> linky) throws Exception {
        Scanner scanner = new Scanner(new InputStreamReader(inputStream));
        nacitajSpojeZoScannera(scanner, spoje, linky);
    }
    
    private void nacitajSpojeZoScannera(Scanner scanner, LinkedHashMap<Integer, Spoj> spoje,
                                        LinkedHashMap<Integer, Linka> linky) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        
        while (scanner.hasNextLine()) {
            String riadok = scanner.nextLine().trim();
            if (riadok.isEmpty() || riadok.startsWith("#")) {
                continue; // preskočiť prázdne riadky a komentáre
            }
            
            String[] stlpce = riadok.split("\\s+");
            if (stlpce.length < 9) {
                continue; // neplatný riadok
            }

            int idLinky = Integer.parseInt(stlpce[0]);
            int idSpoja = Integer.parseInt(stlpce[1]);

            // vytvor unikátny identifikátor spoja
            int id = Model.K * idLinky + idSpoja;

            // začiatočná zastávka
            int miestoOdchodu = Integer.parseInt(stlpce[2]);
            LocalTime casOdchodu = LocalTime.parse(stlpce[3], formatter);

            // koncová zastávka
            int miestoPrichodu = Integer.parseInt(stlpce[4]);
            LocalTime casPrichodu = LocalTime.parse(stlpce[5], formatter);

            // dlzka a obsadenosť
            double dlzka = Double.parseDouble(stlpce[7].replace(',', '.'));
            int obsadenostSpoja = Integer.parseInt(stlpce[8]);

            // získaj alebo vytvor linku
            Linka linka;
            if (linky.containsKey(idLinky)) {
                linka = linky.get(idLinky);
            } else {
                linka = new Linka(idLinky);
                linky.put(idLinky, linka);
            }

            // vytvor spoj a pridaj ho do štruktúr
            Spoj spoj = new Spoj(id, idLinky, idSpoja, miestoOdchodu, casOdchodu, 
                               miestoPrichodu, casPrichodu, dlzka, obsadenostSpoja);
            spoje.put(id, spoj);
            linka.pridajSpoj(spoj);
        }
        scanner.close();
    }
    
    @Override
    public String getFormatName() {
        return "Plain Text";
    }
    
    @Override
    public String getFileExtension() {
        return "txt";
    }
}
