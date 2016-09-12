package at.bkt.batch.model;

import org.springframework.core.style.ToStringCreator;

public class AtpArtikelDTO {

	private int id;

	private String atpnr;

	private String beschreibung;

	private String espnr;

	private String eartnr;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAtpnr() {
		return atpnr.trim();
	}

	public void setAtpnr(String atpnr) {
		this.atpnr = atpnr;
	}

	public String getBeschreibung() {
		return beschreibung.trim();
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getEspnr() {
		return espnr;
	}

	public void setEspnr(String espnr) {
		this.espnr = espnr;
	}

	public String getEartnr() {
		return eartnr.trim();
	}

	public void setEartnr(String eartnr) {
		this.eartnr = eartnr;
	}

	@Override
	public String toString() {
		ToStringCreator builder = new ToStringCreator(this);
		builder.append("id", id);
		builder.append("atpnr", atpnr);
		builder.append("beschreibung", beschreibung);
		builder.append("espnr", espnr);
		builder.append("eartnr", eartnr);
		return builder.toString();
	}

}
