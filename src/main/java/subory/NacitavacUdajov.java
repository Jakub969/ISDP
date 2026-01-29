package subory;

import udaje.*;

import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

/**
 * Hlavná trieda pre načítavanie údajov s podporou rôznych formátov.
 * Používa Strategy Pattern pre flexibilné načítavanie z rôznych zdrojov.
 */
public class NacitavacUdajov
{
    private IDataLoader dataLoader;
    private DataFormat currentFormat;
    
    /**
     * Konštruktor s implicitným Plain Text formátom.
     */
    public NacitavacUdajov() {
        this(DataFormat.PLAIN_TEXT);
    }
    
    /**
     * Konštruktor s určením formátu.
     * @param format formát dát, ktorý sa má používať
     */
    public NacitavacUdajov(DataFormat format) {
        setDataFormat(format);
    }
    
    /**
     * Nastaví formát dát a vytvorí príslušný loader.
     * @param format formát dát
     */
    public void setDataFormat(DataFormat format) {
        this.currentFormat = format;
        this.dataLoader = DataLoaderFactory.createLoader(format);
    }
    
    /**
     * Získa aktuálny formát dát.
     * @return aktuálny DataFormat
     */
    public DataFormat getCurrentFormat() {
        return this.currentFormat;
    }
    
    /**
     * Nastaví loader priamo (umožňuje vlastnú implementáciu).
     * @param loader vlastná implementácia IDataLoader
     */
    public void setDataLoader(IDataLoader loader) {
        this.dataLoader = loader;
        this.currentFormat = null; // custom loader
    }
    
    /**
     * Načíta úseky z resource súboru.
     * @param pNazovSuboruUseky názov súboru v resources
     * @param pUseky mapa pre načítané úseky
     * @throws Exception pri chybe načítavania
     */
    public void nacitajUseky(String pNazovSuboruUseky, LinkedHashMap<Dvojica<Integer, Integer>, Integer> pUseky) throws Exception
    {
        // Automaticky detekuj formát z prípony súboru
        DataFormat detectedFormat = DataFormat.fromFilename(pNazovSuboruUseky);
        if (detectedFormat != currentFormat) {
            setDataFormat(detectedFormat);
        }
        
        InputStream inputStream = getClass().getResourceAsStream("/" + pNazovSuboruUseky);
        if (inputStream == null) {
            throw new Exception("Súbor " + pNazovSuboruUseky + " sa nenašiel v resources");
        }
        dataLoader.nacitajUseky(inputStream, pUseky);
    }

    /**
     * Načíta úseky zo súboru.
     * @param pSuborUseky súbor s úsekmi
     * @param pUseky mapa pre načítané úseky
     * @throws Exception pri chybe načítavania
     */
    public void nacitajUseky(File pSuborUseky, LinkedHashMap<Dvojica<Integer, Integer>, Integer> pUseky) throws Exception
    {
        // Automaticky detekuj formát z prípony súboru
        DataFormat detectedFormat = DataFormat.fromFilename(pSuborUseky.getName());
        if (detectedFormat != currentFormat) {
            setDataFormat(detectedFormat);
        }
        
        dataLoader.nacitajUseky(pSuborUseky, pUseky);
    }

    /**
     * Načíta spoje z resource súboru.
     * @param pNazovSuboruSpoje názov súboru v resources
     * @param pSpoje mapa spojov
     * @param pLinky mapa liniek
     * @throws Exception pri chybe načítavania
     */
    public void nacitajSpoje(String pNazovSuboruSpoje, LinkedHashMap<Integer, Spoj> pSpoje,
                             LinkedHashMap<Integer, Linka> pLinky) throws Exception
    {
        // Automaticky detekuj formát z prípony súboru
        DataFormat detectedFormat = DataFormat.fromFilename(pNazovSuboruSpoje);
        if (detectedFormat != currentFormat) {
            setDataFormat(detectedFormat);
        }
        
        InputStream inputStream = getClass().getResourceAsStream("/" + pNazovSuboruSpoje);
        if (inputStream == null) {
            throw new Exception("Súbor " + pNazovSuboruSpoje + " sa nenašiel v resources");
        }
        dataLoader.nacitajSpoje(inputStream, pSpoje, pLinky);
    }

    /**
     * Načíta spoje zo súboru.
     * @param pSuborSpoje súbor so spojmi
     * @param pSpoje mapa spojov
     * @param pLinky mapa liniek
     * @throws Exception pri chybe načítavania
     */
    public void nacitajSpoje(File pSuborSpoje, LinkedHashMap<Integer, Spoj> pSpoje,
                             LinkedHashMap<Integer, Linka> pLinky) throws Exception
    {
        // Automaticky detekuj formát z prípony súboru
        DataFormat detectedFormat = DataFormat.fromFilename(pSuborSpoje.getName());
        if (detectedFormat != currentFormat) {
            setDataFormat(detectedFormat);
        }
        
        dataLoader.nacitajSpoje(pSuborSpoje, pSpoje, pLinky);
    }

    /**
     * @deprecated Ponechaná pre spätú kompatibilitu. Použite priamo metódy nacitajUseky/nacitajSpoje.
     */
    @Deprecated
    private File getSubor(String fileName)
    {
        String decodedWay = URLDecoder.decode(getClass().getResource("/" + fileName).getPath(), StandardCharsets.UTF_8);
        return new File(decodedWay);
    }
}
