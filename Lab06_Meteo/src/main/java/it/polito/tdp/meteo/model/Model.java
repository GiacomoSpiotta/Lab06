package it.polito.tdp.meteo.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import it.polito.tdp.meteo.DAO.*;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private final static int COSTO_SPOSTAMENTO = 100;
	private MeteoDAO meteoDao;
	private List<String> localita;
	private List<Citta> citta;
	private List<Citta> bestCase;

	public Model() {
		meteoDao = new MeteoDAO();
		localita = meteoDao.getLocalita();
		citta = new LinkedList<Citta>();
		for(String s : localita) {
			Citta c = new Citta(s, null);
			citta.add(c);
		}
		bestCase = null;
	}

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
		
		int somma;
		int umiditaMedia;
		String valoriUmidita = "";
		
		for(Citta ci : citta) {
			somma = 0;
			umiditaMedia = 0;
			ci.setRilevamenti(meteoDao.getAllRilevamentiLocalitaMese(mese, ci.getNome()));
			for(Rilevamento r : ci.getRilevamenti()) {
				somma += r.getUmidita();
			}
			umiditaMedia = somma/ci.getRilevamenti().size();
			valoriUmidita += ci.getNome() + " " + umiditaMedia +"\n";
		}
		
		return valoriUmidita;
	}
	
	// of course you can change the String output with what you think works best
	public String trovaSequenza(int mese) {
		
		List<Citta> parziale = new ArrayList<Citta>();
		
		String sequenza = "";
		
		for(Citta ci : citta) {
			ci.setRilevamenti(meteoDao.getAllRilevamentiLocalitaMese(mese, ci.getNome()));
		}
		
		cercaSequenza(parziale, 0);
		
		for(Citta ci : bestCase) {
			sequenza += ci.getNome()+"\n";
		}
		
		return sequenza;
	}

	private void cercaSequenza(List<Citta> parziale, int livello) {
		// Condizione di terminazione
		if(livello == this.NUMERO_GIORNI_TOTALI) {
			//calcolarci il costo
			double costo = this.calcolaCosto(parziale);
			if(bestCase == null || costo < this.calcolaCosto(bestCase)) {
				bestCase = new ArrayList<Citta>(parziale);
			}
		}else {
			for(Citta c : citta) {
				if(this.isValida(parziale, c)) {
					parziale.add(c);
					cercaSequenza(parziale, livello+1);
					parziale.remove(parziale.size()-1);
				}
			}
		}
		
	}
	
	private double calcolaCosto(List<Citta> parziale) {
		double costoTotale = 0;
		
		for(int i = 0; i < NUMERO_GIORNI_TOTALI; i++) {
			//calcolo le umidita di ogni giorno
			Citta c = parziale.get(i);
			
			costoTotale += c.getRilevamenti().get(i).getUmidita();
		}
		
		for(int i = 2; i < NUMERO_GIORNI_TOTALI; i++) {
			if(!parziale.get(i).getNome().equals(parziale.get(i-1).getNome())) {
				costoTotale += COSTO_SPOSTAMENTO;
			}
		}
		
		return costoTotale;
	}
	
	private boolean isValida(List<Citta> parziale, Citta c) {
		
		c.setCounter(0);	
		for(Citta ci : parziale) {
			if(ci.getNome().equals(c.getNome())) {
				c.increaseCounter();
			}
		}
		
		// Max 6 gg per ogni citta
		if(c.getCounter() > 5) {
			return false;
		}
		
		if(parziale.size() == 0) {
			return true;
		}
		
		if(parziale.size() == 1 || parziale.size() == 2) {
			return parziale.get(parziale.size()-1).equals(c);
		}
		
		if (parziale.get(parziale.size()-1).equals(c)) {
			return true;
		}
		
		if(parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)) && parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-3))){
			return true;
		}
		
		return false;
	}
	

}
