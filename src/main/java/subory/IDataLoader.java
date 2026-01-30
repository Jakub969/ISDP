package subory;

import udaje.Dvojica;
import udaje.Linka;
import udaje.Spoj;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;

/**
 * Rozhranie pre načítavanie údajov z rôznych formátov.
 * Implementuje Strategy Pattern pre flexibilné načítavanie rôznych typov súborov.
 */
public interface IDataLoader {
    
    /**
     * Načíta úseky zo súboru.
     * @param suborUseky súbor s údajmi o úsekoch
     * @param useky mapa, do ktorej sa načítajú úseky
     * @throws Exception v prípade chyby pri načítavaní
     */
    void nacitajUseky(File suborUseky, LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky) throws Exception;
    
    /**
     * Načíta úseky zo vstupného prúdu.
     * @param inputStream vstupný prúd s údajmi
     * @param useky mapa, do ktorej sa načítajú úseky
     * @throws Exception v prípade chyby pri načítavaní
     */
    void nacitajUseky(InputStream inputStream, LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky) throws Exception;
    
    /**
     * Načíta spoje zo súboru.
     * @param suborSpoje súbor s údajmi o spojoch
     * @param spoje mapa spojov
     * @param linky mapa liniek
     * @throws Exception v prípade chyby pri načítavaní
     */
    void nacitajSpoje(File suborSpoje, LinkedHashMap<Integer, Spoj> spoje,
                      LinkedHashMap<Integer, Linka> linky) throws Exception;
    
    /**
     * Načíta spoje zo vstupného prúdu.
     * @param inputStream vstupný prúd s údajmi
     * @param spoje mapa spojov
     * @param linky mapa liniek
     * @throws Exception v prípade chyby pri načítavaní
     */
    void nacitajSpoje(InputStream inputStream, LinkedHashMap<Integer, Spoj> spoje,
                      LinkedHashMap<Integer, Linka> linky) throws Exception;
    
    /**
     * Vráti názov formátu, ktorý tento loader podporuje.
     * @return názov formátu (napr. "Plain Text", "CSV", "XML", "JSON")
     */
    String getFormatName();
    
    /**
     * Vráti príponu súborov, ktoré tento loader podporuje.
     * @return prípona súboru (napr. "txt", "csv", "xml", "json")
     */
    String getFileExtension();
}
