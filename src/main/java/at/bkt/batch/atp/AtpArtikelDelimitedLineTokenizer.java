package at.bkt.batch.atp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.IncorrectTokenCountException;

public class AtpArtikelDelimitedLineTokenizer extends DelimitedLineTokenizer {

	private static final Logger LOGGER = LoggerFactory.getLogger(AtpArtikelDelimitedLineTokenizer.class);
	
	public AtpArtikelDelimitedLineTokenizer() {
		setDelimiter(";");
		setNames(new String[] {"atpnr", "beschreibung", "espnr", "eartnr"});
		setIncludedFields(new int[] { 0, 3, 7, 9});
		setStrict(false);
	}
	
	

	
}