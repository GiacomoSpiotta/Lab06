package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.model.Rilevamento;

public class MeteoDAO {
	
	public List<String> getLocalita(){
		final String sql = "SELECT DISTINCT Localita "
				+ "FROM situazione";
		
		List<String> localita = new ArrayList<String>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				String s = rs.getString("Localita");
				localita.add(s);
			}
			
			rs.close();
			st.close();
			conn.close();
			return localita;
		}catch(SQLException e) {
			throw new RuntimeException("Errore nella query", e);
		}
	}
	
	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		
		final String sql = "SELECT Localita, Data, Umidita "
				+ "FROM situazione "
				+ "WHERE YEAR(Data) = 2013 AND MONTH(Data) = ? AND Localita = ?"
				+ "ORDER BY data ASC" ;
		
		List<Rilevamento> RilevamentiLocalitaMese = new ArrayList<Rilevamento>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, mese);
			st.setString(2, localita);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				RilevamentiLocalitaMese.add(r);
			}
			
			rs.close();
			st.close();
			conn.close();
			
			return RilevamentiLocalitaMese;
		} catch (SQLException e) {
			throw new RuntimeException("Errore nella query", e);
		}
	}


}
