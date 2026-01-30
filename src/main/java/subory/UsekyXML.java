package subory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import udaje.Dvojica;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsekyXML {

    @XmlElement(name = "row")
    public List<UsekDTO> useky;

    public void nacitajUsekyXML(
            File subor,
            LinkedHashMap<Dvojica<Integer, Integer>, Integer> pUseky
    ) throws Exception {

        JAXBContext ctx = JAXBContext.newInstance(UsekyXML.class);
        Unmarshaller um = ctx.createUnmarshaller();
        UsekyXML data = (UsekyXML) um.unmarshal(subor);

        for (UsekDTO u : data.useky) {
            pUseky.put(new Dvojica<>(u.u, u.v), u.c);
            pUseky.put(new Dvojica<>(u.v, u.u), u.c);
            pUseky.put(new Dvojica<>(u.u, u.u), 0);
            pUseky.put(new Dvojica<>(u.v, u.v), 0);
        }
    }

}