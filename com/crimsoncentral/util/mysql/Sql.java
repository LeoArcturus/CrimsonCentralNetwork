package com.crimsoncentral.util.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.crimsoncentral.util.CrimsonMap;

public class Sql {

	public static Connection connection = null;

	public static void establishConnection() {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://198.245.51.96/db_34360", "db_34360", "9770276e52");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized static void closeConnection() {
		try {
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setInt(String table, String selection, String where, int i) {
		try {
			PreparedStatement sql = connection
					.prepareStatement("UPDATE `" + table + "` SET" + selection + "=? WHERE " + where + "=?;");
			sql.setInt(1, i);
			sql.setString(2, where + "");
			sql.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static int getInt(String table, String selection, String where, String what) {
		int i = 0;

		try {
			PreparedStatement sql = connection
					.prepareStatement("SELECT " + selection + " FROM `" + table + "` WHERE " + where + "=?;");
			sql.setString(1, what + "");
			ResultSet result = sql.executeQuery();
			result.next();
			i = result.getInt(selection);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return i;
	}
	
	public static void setDouble(String table, String selection, String where, double i) {
		try {
			PreparedStatement sql = connection
					.prepareStatement("UPDATE `" + table + "` SET" + selection + "=? WHERE " + where + "=?;");
			sql.setDouble(1, i);
			sql.setString(2, where + "");
			sql.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static double getDouble(String table, String selection, String where, String what) {
		double i = 0.0;

		try {
			PreparedStatement sql = connection
					.prepareStatement("SELECT " + selection + " FROM `" + table + "` WHERE " + where + "=?;");
			sql.setString(1, what + "");
			ResultSet result = sql.executeQuery();
			result.next();
			i = result.getDouble(selection);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return i;
	}

	public static void setString(String table, String setation, String where, String setation_equals,
			String where_equals) {
		try {
			PreparedStatement sql = connection
					.prepareStatement("UPDATE `" + table + "` SET " + setation + "=? WHERE " + where + "=?;");
			sql.setString(1, setation_equals);
			sql.setString(2, where_equals);
			sql.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String getString(String table, String selection, String where, String what) {
		String s = "";

		try {
			PreparedStatement sql = connection
					.prepareStatement("SELECT " + selection + " FROM `" + table + "` WHERE " + where + "=?;");
			sql.setString(1, what + "");
			ResultSet result = sql.executeQuery();
			result.next();
			s = result.getString(selection);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return s;
	}

	public static boolean checkTableForRow(String table, String selection, String where, String where_equals) {

		boolean exists = false;
		try {
			PreparedStatement sql = connection
					.prepareStatement("SELECT " + selection + " FROM `" + table + "` WHERE " + where + "=?;");
			sql.setString(1, where_equals + "");

			ResultSet result = sql.executeQuery();
			boolean containsPlayer = result.next();

			sql.close();
			result.close();
			exists = containsPlayer;

		} catch (SQLException e) {
			e.printStackTrace();
			exists = false;
		}

		return exists;

	}

	public static void createRow(String table, CrimsonMap entires) {

		String column_names = "";
		String inserts = "";

		for (int i = 1; i <= entires.size(); ++i) {

			String comma = ", ";
			if (i == entires.size()) {

				comma = "";
			}

			column_names = column_names + "`" + entires.order_keys.get(i).toString() + "`" + comma;
			inserts = inserts + "'" + entires.order_values.get(i).toString() + "'" + comma;

		}

		try {

			PreparedStatement sql = connection.prepareStatement("INSERT INTO `db_34360`.`" + table.toString() + "` ("
					+ column_names.toString() + ") VALUES (" + inserts.toString() + ");");
			sql.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
