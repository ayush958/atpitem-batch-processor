package at.bkt.batch.atp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import at.bkt.batch.model.AtpArtikelDTO;

public class AtpArtikelItemProcessor implements ItemProcessor<AtpArtikelDTO, AtpArtikelDTO> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AtpArtikelItemProcessor.class);

	@Override
    public AtpArtikelDTO process(final AtpArtikelDTO atpArtikel) throws Exception {
    	
		String espnr = atpArtikel.getEspnr().trim();
		String eartnr = atpArtikel.getEartnr().trim();
		String beschreibung = atpArtikel.getBeschreibung().trim();
		
		if (eartnr.isEmpty() || espnr.equalsIgnoreCase("0000") || espnr.isEmpty()) {
			return null;
		} else {
		
        	if (LOGGER.isDebugEnabled()) {
        		LOGGER.debug("Processing: {}",atpArtikel);
        	}
        	
        	
        	// Datenfehler wenn in Beschreibung ein ';' steht.
        	if (beschreibung.contains(";")) {
        		String[] splitBeschreibung = beschreibung.split(";");
        		atpArtikel.setBeschreibung(splitBeschreibung[0]);
        	}
        	
            return atpArtikel;
		}	    		
	        	
    }

}
