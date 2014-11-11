package com.lg.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * 2008-12-6
 * 
 * @author <a href="mailto:liyongibm@hotmail.com">����</a>
 * 
 */
public final class JdbcUtils {

	public static String database = "daydayhealth";
	public static String host = "localhost";
	public static String url = "jdbc:mysql://"+host+":3306/"+database+"?useUnicode=true&characterEncoding=utf8";
	public static String user = "root";
	public static String password = "root";

	
	/*public static String database = "o2opos";
	public static String host = "192.168.1.127";
	public static String url = "jdbc:mysql://"+host+":3306/"+database+"?useUnicode=true&characterEncoding=utf8";
	public static String user = "root";
	public static String password = "123456";*/
	
	private JdbcUtils() {
		
	}

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}

	public static void free(ResultSet rs, Statement st, Connection conn) {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (conn != null)
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}
	}
}
