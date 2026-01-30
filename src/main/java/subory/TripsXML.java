package subory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import mvp.Model;
import udaje.Linka;
import udaje.Spoj;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
public class TripsXML {
    @XmlElement(name = "row")
    public List<SpojDTO> trips;

    public void nacitajSpojeXML(
            File subor,
            LinkedHashMap<Integer, Spoj> pSpoje,
            LinkedHashMap<Integer, Linka> pLinky) throws Exception {

        JAXBContext context = JAXBContext.newInstance(TripsXML.class);
        Unmarshaller um = context.createUnmarshaller();
        TripsXML data = (TripsXML) um.unmarshal(subor);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");

        for (SpojDTO dto : data.trips) {
            int id = Model.K * dto.idLinky + dto.idSpoja;

            Linka linka = pLinky.computeIfAbsent(dto.idLinky, Linka::new);

            Spoj spoj = new Spoj(
                    id,
                    dto.idLinky,
                    dto.idSpoja,
                    dto.miestoOdchodu,
                    LocalTime.parse(dto.casOdchodu, formatter),
                    dto.miestoPrichodu,
                    LocalTime.parse(dto.casPrichodu, formatter),
                    dto.dlzka,
                    dto.obsadenost
            );

            pSpoje.put(id, spoj);
            linka.pridajSpoj(spoj);
        }
    }

}

