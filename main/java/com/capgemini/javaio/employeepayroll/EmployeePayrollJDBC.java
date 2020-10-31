/**
 * 
 */
package com.capgemini.javaio.employeepayroll;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mysql.cj.jdbc.Driver;

/**
 * @author Mohana Kavya
 *
 */
public class EmployeePayrollJDBC {
	private static Logger log = Logger.getLogger(EmployeePayrollJDBC.class.getName());
	
	public static void main(String[] args) {
		
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service";
		String user = "root";
		String password = "L3al!lhope"; 
		
		// Loading Driver Class
		Handler fileHandler = null;
		try {
			fileHandler = new FileHandler("C:\\Users\\Mohana Kavya\\eclipse-workspace\\JavaIO_EmployeePayRoll\\src\\test\\resources\\employee payroll.log");
			fileHandler.setLevel(Level.ALL);
			log.addHandler(fileHandler);
			Class.forName("com.mysql.jdbc.Driver");
			log.log(Level.INFO, "Driver Loaded");
		} catch (ClassNotFoundException | SecurityException | IOException e) {
			log.log(Level.SEVERE, "Failed to Load the Driver", e);
		}

	listDrivers();
	
	// Create Connection object and establish connection with database	
	try {
		log.log(Level.INFO, "Connecting to database :"+jdbcURL);
		Connection connection = DriverManager.getConnection(jdbcURL, user, password);
		log.log(Level.INFO, "Connection Succesfull : "+connection);
	} catch (SQLException e) {
		log.log(Level.SEVERE, "Connection failed "+e);
	}
	}

	/**
	 * List all Drivers Available
	 */
	private static void listDrivers() {
		Iterator<java.sql.Driver> iterator = DriverManager.getDrivers().asIterator();
		while(iterator.hasNext()) {
			Driver driver = (Driver) iterator.next();
			System.out.println(driver.getClass().getName());
		}
	}
	
}
