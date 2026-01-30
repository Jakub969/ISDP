package subory;

import mvp.Model;
import udaje.Dvojica;
import udaje.Linka;
import udaje.Spoj;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.InputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

/**
 * Načítavač údajov z XML formátu.
 * Štruktúra XML pre úseky:
 * <useky>
 *   <usek>
 *     <from>1</from>
 *     <to>2</to>
 *     <cost>14</cost>
 *   </usek>
 * </useky>
 * 
 * Štruktúra XML pre spoje:
 * <spoje>
 *   <spoj>
 *     <idLinky>1</idLinky>
 *     <idSpoja>1</idSpoja>
 *     <miestoOdchodu>102</miestoOdchodu>
 *     <casOdchodu>14:15</casOdchodu>
 *     <miestoPrichodu>10</miestoPrichodu>
 *     <casPrichodu>14:32</casPrichodu>
 *     <trvanie>0:17</trvanie>
 *     <dlzka>10.7</dlzka>
 *     <obsadenost>100</obsadenost>
 *   </spoj>
 * </spoje>
 */
public class XMLDataLoader implements IDataLoader {
    
    @Override
    public void nacitajUseky(File suborUseky, LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(suborUseky);
        nacitajUsekyZDokumentu(document, useky);
    }
    
    @Override
    public void nacitajUseky(InputStream inputStream, LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputStream);
        nacitajUsekyZDokumentu(document, useky);
    }
    
    private void nacitajUsekyZDokumentu(Document document, LinkedHashMap<Dvojica<Integer, Integer>, Integer> useky) {
        document.getDocumentElement().normalize();
        
        NodeList nodeList = document.getElementsByTagName("usek");
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                
                int u = Integer.parseInt(getElementTextContent(element, "from"));
                int v = Integer.parseInt(getElementTextContent(element, "to"));
                int c = Integer.parseInt(getElementTextContent(element, "cost"));

                useky.put(new Dvojica<>(u, v), c);
                useky.put(new Dvojica<>(v, u), c);
                useky.put(new Dvojica<>(u, u), 0);
                useky.put(new Dvojica<>(v, v), 0);
            }
        }
    }
    
    @Override
    public void nacitajSpoje(File suborSpoje, LinkedHashMap<Integer, Spoj> spoje,
                             LinkedHashMap<Integer, Linka> linky) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(suborSpoje);
        nacitajSpojeZDokumentu(document, spoje, linky);
    }
    
    @Override
    public void nacitajSpoje(InputStream inputStream, LinkedHashMap<Integer, Spoj> spoje,
                             LinkedHashMap<Integer, Linka> linky) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputStream);
        nacitajSpojeZDokumentu(document, spoje, linky);
    }
    
    private void nacitajSpojeZDokumentu(Document document, LinkedHashMap<Integer, Spoj> spoje,
                                       LinkedHashMap<Integer, Linka> linky) {
        document.getDocumentElement().normalize();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        
        NodeList nodeList = document.getElementsByTagName("spoj");
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                
                int idLinky = Integer.parseInt(getElementTextContent(element, "idLinky"));
                int idSpoja = Integer.parseInt(getElementTextContent(element, "idSpoja"));

                // vytvor unikátny identifikátor spoja
                int id = Model.K * idLinky + idSpoja;

                // začiatočná zastávka
                int miestoOdchodu = Integer.parseInt(getElementTextContent(element, "miestoOdchodu"));
                LocalTime casOdchodu = LocalTime.parse(getElementTextContent(element, "casOdchodu"), formatter);

                // koncová zastávka
                int miestoPrichodu = Integer.parseInt(getElementTextContent(element, "miestoPrichodu"));
                LocalTime casPrichodu = LocalTime.parse(getElementTextContent(element, "casPrichodu"), formatter);

                // dlzka a obsadenosť
                double dlzka = Double.parseDouble(getElementTextContent(element, "dlzka").replace(',', '.'));
                int obsadenostSpoja = Integer.parseInt(getElementTextContent(element, "obsadenost"));

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
    }
    
    private String getElementTextContent(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return "";
    }
    
    @Override
    public String getFormatName() {
        return "XML";
    }
    
    @Override
    public String getFileExtension() {
        return "xml";
    }
}
