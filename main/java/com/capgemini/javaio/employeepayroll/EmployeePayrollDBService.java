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
import java.sql.Statement;
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
	 * Reading Records from Database to program 
	 * @return List of Employee Payroll objects
	 */
	public List<EmployeePayrollData> readData() {
		String query = "SELECT * FROM payroll_employee";	
		List<EmployeePayrollData> empList = new ArrayList();
		try {
			Connection connection = this.getConnection();
			PreparedStatement prepStatement = connection.prepareStatement(query);
			ResultSet result = prepStatement.executeQuery();
			empList = this.getEmployeePayrollData(result);
		} catch (SQLException | SecurityException | IOException e) {
			log.log(Level.SEVERE, "Failed : "+e);
		}
		
		return empList;
	}
	
	/**
	 * Update Salary in Database by Employee Name
	 * @param name
	 * @param salary
	 * @return number of rows modified
	 */
	public int updateEmployeeData(String name, double salary) {
		String sql = String.format("update payroll_employee set salary=%.2f where name='%s';", salary, name);
		try (Connection connection = this.getConnection();) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException | SecurityException | IOException e) {
			log.log(Level.SEVERE, "Failed : "+e);
		}
		return 0;
	}

	/**
	 * Get Records from Database for a Particular Name 
	 * @param name
	 * @return List of EmployePayrollData objects
	 */
	public List<EmployeePayrollData> getEmployeePayrollData(String name) {
		String sql = String.format("SELECT * FROM payroll_employee WHERE name='%s';", name);
		try (Connection connection = this.getConnection();) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			List<EmployeePayrollData> empListByName = this.getEmployeePayrollData(result);
			return empListByName;
		} catch (SQLException | SecurityException | IOException e) {
			log.log(Level.SEVERE, "Failed : "+e);
		}
		return null;
	}
	
	/** 
	 * Populating Database Records to EmployeePayrollData Class's Objects
	 * @param Result Set
	 * @return List of Employee Objects
	 */
	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet result) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try {
			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				double Salary = result.getDouble("salary");
				LocalDate startDate = result.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, Salary, startDate));
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	/**
	 * Establishing Connection between program and Database via Connection Object
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



