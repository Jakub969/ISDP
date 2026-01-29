package subory;

import mvp.Model;
import udaje.Dvojica;
import udaje.Linka;
import udaje.Spoj;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

/**
 * Načítavač údajov z CSV formátu.
 * Formát úsekov CSV: u,v,c (prvý riadok je hlavička)
 * Formát spojov CSV: idLinky,idSpoja,miestoOdchodu,casOdchodu,miestoPrichodu,casPrichodu,trvanie,dlzka,obsadenost
 */
public class CSVDataLoader implements IDataLoader {
    
    private static final String CSV_SEPARATOR = ",";
    
    @Override
    public void nacitajUseky(File suborUseky, LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(suborUseky))) {
            nacitajUsekyZReadera(reader, useky);
        }
    }
    
    @Override
    public void nacitajUseky(InputStream inputStream, LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            nacitajUsekyZReadera(reader, useky);
        }
    }
    
    private void nacitajUsekyZReadera(BufferedReader reader, LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky) throws IOException {
        String riadok;
        boolean prvaRiadka = true;
        
        while ((riadok = reader.readLine()) != null) {
            riadok = riadok.trim();
            
            if (riadok.isEmpty() || riadok.startsWith("#")) {
                continue;
            }
            
            // Preskočiť hlavičku
            if (prvaRiadka) {
                prvaRiadka = false;
                if (riadok.toLowerCase().contains("from") || riadok.toLowerCase().contains("to") ||
                    riadok.toLowerCase().contains("cost")) {
                    continue;
                }
            }
            
            String[] stlpce = riadok.split(CSV_SEPARATOR);
            if (stlpce.length < 3) {
                continue;
            }
            
            int u = Integer.parseInt(stlpce[0].trim());
            int v = Integer.parseInt(stlpce[1].trim());
            int c = Integer.parseInt(stlpce[2].trim());

            useky.put(new Dvojica<>(u, v), c);
            useky.put(new Dvojica<>(v, u), c);
            useky.put(new Dvojica<>(u, u), 0);
            useky.put(new Dvojica<>(v, v), 0);
        }
    }
    
    @Override
    public void nacitajSpoje(File suborSpoje, LinkedHashMap<Integer, Spoj> spoje,
                             LinkedHashMap<Integer, Linka> linky) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(suborSpoje))) {
            nacitajSpojeZReadera(reader, spoje, linky);
        }
    }
    
    @Override
    public void nacitajSpoje(InputStream inputStream, LinkedHashMap<Integer, Spoj> spoje,
                             LinkedHashMap<Integer, Linka> linky) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            nacitajSpojeZReadera(reader, spoje, linky);
        }
    }
    
    private void nacitajSpojeZReadera(BufferedReader reader, LinkedHashMap<Integer, Spoj> spoje,
                                     LinkedHashMap<Integer, Linka> linky) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        String riadok;
        boolean prvaRiadka = true;
        
        while ((riadok = reader.readLine()) != null) {
            riadok = riadok.trim();
            
            if (riadok.isEmpty() || riadok.startsWith("#")) {
                continue;
            }
            
            // Preskočiť hlavičku
            if (prvaRiadka) {
                prvaRiadka = false;
                if (riadok.toLowerCase().contains("line") || riadok.toLowerCase().contains("trip") || 
                    riadok.toLowerCase().contains("linka") || riadok.toLowerCase().contains("spoj")) {
                    continue;
                }
            }
            
            String[] stlpce = riadok.split(CSV_SEPARATOR);
            if (stlpce.length < 9) {
                continue;
            }

            int idLinky = Integer.parseInt(stlpce[0].trim());
            int idSpoja = Integer.parseInt(stlpce[1].trim());

            // vytvor unikátny identifikátor spoja
            int id = Model.K * idLinky + idSpoja;

            // začiatočná zastávka
            int miestoOdchodu = Integer.parseInt(stlpce[2].trim());
            LocalTime casOdchodu = LocalTime.parse(stlpce[3].trim(), formatter);

            // koncová zastávka
            int miestoPrichodu = Integer.parseInt(stlpce[4].trim());
            LocalTime casPrichodu = LocalTime.parse(stlpce[5].trim(), formatter);

            // dlzka a obsadenosť
            double dlzka = Double.parseDouble(stlpce[7].trim().replace(',', '.'));
            int obsadenostSpoja = Integer.parseInt(stlpce[8].trim());

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
    }
    
    @Override
    public String getFormatName() {
        return "CSV";
    }
    
    @Override
    public String getFileExtension() {
        return "csv";
    }
}
