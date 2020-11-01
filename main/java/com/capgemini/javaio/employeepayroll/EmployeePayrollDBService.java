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
	
	private static PreparedStatement employeePayrollDataPreparedStatement;
	private static EmployeePayrollDBService employeePayrollDBService;

	private EmployeePayrollDBService() {
	}

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null) {
			employeePayrollDBService = new EmployeePayrollDBService();
		}
		return employeePayrollDBService;
	}

	/**
	 * Reading Records from Database to program 
	 * @return List of Employee Payroll objects
	 */
	public List<EmployeePayrollData> readData() {
		String query = "SELECT * FROM payroll_employee";	
		List<EmployeePayrollData> empList = new ArrayList();
		try {
			Connection connection = this.getConnection();
			employeePayrollDataPreparedStatement = connection.prepareStatement(query);
			ResultSet result = employeePayrollDataPreparedStatement.executeQuery();
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
		String sql = String.format("update payroll_employee set salary=? where name=?;");
		try (Connection connection = this.getConnection();) {
			employeePayrollDataPreparedStatement = connection.prepareStatement(sql);
			employeePayrollDataPreparedStatement.setDouble(1, salary);
			employeePayrollDataPreparedStatement.setString(2, name);
			return employeePayrollDataPreparedStatement.executeUpdate();
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
		List<EmployeePayrollData> employeeParollListByName = null;
		if (this.employeePayrollDataPreparedStatement == null)
			this.prepareStatementForEmployeeDataByName();
		try {
			employeePayrollDataPreparedStatement.setString(1,name);
			ResultSet resultSet=employeePayrollDataPreparedStatement.executeQuery();
			employeeParollListByName= this.getEmployeePayrollData(resultSet);
		}catch (SQLException e) {
			log.log(Level.SEVERE, "Failed : "+e);
		}
		return employeeParollListByName;
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
			log.log(Level.SEVERE, "Failed : "+e);
		}
		return employeePayrollList;
	}
	
	private void prepareStatementForEmployeeDataByName() {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM employee_payroll WHERE name=?";
			employeePayrollDataPreparedStatement = connection.prepareStatement(sql);
		} catch (SQLException | SecurityException | IOException e) {
			log.log(Level.SEVERE, "Failed : "+e);
		}
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



