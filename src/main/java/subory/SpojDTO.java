package subory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mvp.Model;
import udaje.Linka;
import udaje.Spoj;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;

public class SpojDTO {
    public int idLinky;
    public int idSpoja;
    public int miestoOdchodu;
    public String casOdchodu;
    public int miestoPrichodu;
    public String casPrichodu;
    public double dlzka;
    public int obsadenost;


    public void nacitajSpojeJSON(
            File subor,
            LinkedHashMap<Integer, Spoj> pSpoje,
            LinkedHashMap<Integer, Linka> pLinky) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");

        List<SpojDTO> spoje = mapper.readValue(
                subor,
                new TypeReference<List<SpojDTO>>() {}
        );

        for (SpojDTO dto : spoje) {
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

