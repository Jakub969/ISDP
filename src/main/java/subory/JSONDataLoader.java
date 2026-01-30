package subory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mvp.Model;
import udaje.Dvojica;
import udaje.Linka;
import udaje.Spoj;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

/**
 * Načítavač údajov z JSON formátu.
 * Štruktúra JSON pre úseky:
 * {
 *   "useky": [
 *     {"from": 1, "to": 2, "cost": 14},
 *     ...
 *   ]
 * }
 * 
 * Štruktúra JSON pre spoje:
 * {
 *   "spoje": [
 *     {
 *       "idLinky": 1,
 *       "idSpoja": 1,
 *       "miestoOdchodu": 102,
 *       "casOdchodu": "14:15",
 *       "miestoPrichodu": 10,
 *       "casPrichodu": "14:32",
 *       "trvanie": "0:17",
 *       "dlzka": 10.7,
 *       "obsadenost": 100
 *     },
 *     ...
 *   ]
 * }
 */
public class JSONDataLoader implements IDataLoader {
    
    public JSONDataLoader() {
    }
    
    @Override
    public void nacitajUseky(File suborUseky, LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky) throws Exception {
        try (FileReader reader = new FileReader(suborUseky)) {
            nacitajUsekyZReadera(reader, useky);
        }
    }
    
    @Override
    public void nacitajUseky(InputStream inputStream, LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky) throws Exception {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            nacitajUsekyZReadera(reader, useky);
        }
    }
    
    private void nacitajUsekyZReadera(Reader reader, LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky) throws IOException {
        JsonElement jsonElement = JsonParser.parseReader(reader);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray usekyArray = jsonObject.getAsJsonArray("useky");
        
        for (JsonElement element : usekyArray) {
            JsonObject usek = element.getAsJsonObject();
            
            int u = usek.get("from").getAsInt();
            int v = usek.get("to").getAsInt();
            int c = usek.get("cost").getAsInt();

            useky.put(new Dvojica<>(u, v), c);
            useky.put(new Dvojica<>(v, u), c);
            useky.put(new Dvojica<>(u, u), 0);
            useky.put(new Dvojica<>(v, v), 0);
        }
    }
    
    @Override
    public void nacitajSpoje(File suborSpoje, LinkedHashMap<Integer, Spoj> spoje,
                             LinkedHashMap<Integer, Linka> linky) throws Exception {
        try (FileReader reader = new FileReader(suborSpoje)) {
            nacitajSpojeZReadera(reader, spoje, linky);
        }
    }
    
    @Override
    public void nacitajSpoje(InputStream inputStream, LinkedHashMap<Integer, Spoj> spoje,
                             LinkedHashMap<Integer, Linka> linky) throws Exception {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            nacitajSpojeZReadera(reader, spoje, linky);
        }
    }
    
    private void nacitajSpojeZReadera(Reader reader, LinkedHashMap<Integer, Spoj> spoje,
                                     LinkedHashMap<Integer, Linka> linky) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        
        JsonElement jsonElement = JsonParser.parseReader(reader);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray spojeArray = jsonObject.getAsJsonArray("spoje");
        
        for (JsonElement element : spojeArray) {
            JsonObject spojObj = element.getAsJsonObject();
            
            int idLinky = spojObj.get("idLinky").getAsInt();
            int idSpoja = spojObj.get("idSpoja").getAsInt();

            // vytvor unikátny identifikátor spoja
            int id = Model.K * idLinky + idSpoja;

            // začiatočná zastávka
            int miestoOdchodu = spojObj.get("miestoOdchodu").getAsInt();
            LocalTime casOdchodu = LocalTime.parse(spojObj.get("casOdchodu").getAsString(), formatter);

            // koncová zastávka
            int miestoPrichodu = spojObj.get("miestoPrichodu").getAsInt();
            LocalTime casPrichodu = LocalTime.parse(spojObj.get("casPrichodu").getAsString(), formatter);

            // dlzka a obsadenosť
            double dlzka = spojObj.get("dlzka").getAsDouble();
            int obsadenostSpoja = spojObj.get("obsadenost").getAsInt();

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
        return "JSON";
    }
    
    @Override
    public String getFileExtension() {
        return "json";
    }
}
