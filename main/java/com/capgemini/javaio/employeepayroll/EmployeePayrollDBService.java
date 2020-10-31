/**
 * 
 */
package com.capgemini.javaio.employeepayroll;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mohana Kavya
 *
 */
public class EmployeePayrollDBService {
	private static Logger log = Logger.getLogger(EmployeePayrollDBService.class.getName());

	/**
	 * @return
	 */
	public List<EmployeePayrollData> readData() {
		String query = "SELECT * FROM payroll_employee";	
		List<EmployeePayrollData> empList = new ArrayList();
		try {
			Connection connection = this.getConnection();
			PreparedStatement prepStatement = connection.prepareStatement(query);
			ResultSet result = prepStatement.executeQuery();
			while(result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				double salary = result.getDouble("salary");
				LocalDate startDate = result.getDate("start").toLocalDate();
				empList.add(new EmployeePayrollData(id, name, salary, startDate));
			}
		} catch (SQLException | SecurityException | IOException e) {
			log.log(Level.SEVERE, "Failed : "+e);
		}
		
		return empList;
	}

	/**
	 * @return Connection
	 * @throws IOException 
	 * @throws SecurityException 
	 * @throws SQLException 
	 */
	private Connection getConnection() throws SecurityException, IOException, SQLException {

		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service";
		String user = "root";
		String password = "L3al!lhope"; 
		// Create Connection object and establish connection with database	
		Handler fileHandler = null;
		Connection connection = null;
		fileHandler = new FileHandler("C:\\Users\\Mohana Kavya\\eclipse-workspace\\JavaIO_EmployeePayRoll\\src\\test\\resources\\employee payroll.log");
		fileHandler.setLevel(Level.ALL);
		log.addHandler(fileHandler);
		log.log(Level.INFO, "Connecting to database :"+jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, user, password);
		log.log(Level.INFO, "Connection Succesfull : "+connection);
	return connection;
	}
		
}



