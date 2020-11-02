/**
 * 
 */
package com.capgemini.javaio.employeepayroll;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		String sql = "SELECT * FROM payroll_employee";	
		return this.getEmployeePayrollDataFromDB(sql);	
	}

	/**
	 * To write into Database
	 * @param name
	 * @param salary
	 * @param startDate
	 * @param gender
	 * @return number of rows modified
	 */
	public int writeEmployeePayrollToDB(String name, double salary, LocalDate startDate, char gender) {
		int rowAffected = 0;
		log.info("rows affected : "+rowAffected);
		String sql = String.format("INSERT INTO payroll_employee (name, salary, start, gender)"+ " VALUES('%s',%.2f,'%s','%s')", name, salary, Date.valueOf(startDate), gender);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			log.info("rows affected : "+rowAffected);
			if (rowAffected == 1) {
				ResultSet result = statement.getGeneratedKeys();
				if (result.next()) {
					EmployeePayrollService.newEmpPayrollDataObj.id = result.getInt(1);
					log.info("id : "+EmployeePayrollService.newEmpPayrollDataObj.id);
				}
			}
		} catch (SQLException | SecurityException | IOException e) {
			log.log(Level.SEVERE, "Failed : "+e);
		}
		return rowAffected;
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
	public List<EmployeePayrollData> getEmployeePayrollDataByName(String name) {
		String sql = String.format("SELECT * FROM payroll_employee WHERE name='%s';", name);
		return this.getEmployeePayrollDataFromDB(sql);
	}

	/**
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<EmployeePayrollData> readEmpPayrollDBInGivenDateRange(LocalDate startDate, LocalDate endDate) {
		String sql = String.format("SELECT * FROM payroll_employee where start between '%s' AND '%s';", Date.valueOf(startDate), Date.valueOf(endDate));
		return this.getEmployeePayrollDataFromDB(sql);
	}

	/**
	 * @return Map Key : Gender, Value is Average of Salaries
	 */
	public Map<String, Double> getAverageSalaryGroupByGender() {
		String sql = "SELECT gender, AVG(salary) as avg_salary FROM payroll_employee group by gender;";
		Map<String, Double> genderToAverageSalaryMap = new HashMap<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				String gender = result.getString("gender");
				double salary = result.getDouble("avg_salary");
				genderToAverageSalaryMap.put(gender, salary);
			}
		} catch (SQLException | SecurityException | IOException e) {
			log.log(Level.SEVERE, "Failed : "+e);
		}
		return genderToAverageSalaryMap;
	}
	
	/**
	 * Used Dry Principle to Consolidate Code to read data from database
	 * @param sql Query
	 * @return List of Employee Payroll Data Objects
	 */
	private List<EmployeePayrollData> getEmployeePayrollDataFromDB(String sql) {
		List<EmployeePayrollData> employeePayrollDataList = null;
		try {
			Connection connection = this.getConnection();
			employeePayrollDataPreparedStatement = connection.prepareStatement(sql);
			ResultSet result = employeePayrollDataPreparedStatement.executeQuery();
			employeePayrollDataList = this.getEmployeePayrollData(result);
		} catch (SQLException | SecurityException | IOException e) {
			log.log(Level.SEVERE, "Failed : "+e);
		}
		return employeePayrollDataList;
	}
	
	/** 
	 * Populating Database Records to EmployeePayrollData Class's Objects
	 * @param Result Set
	 * @return List of Employee Objects
	 * @throws SQLException 
	 */
	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet result) throws SQLException {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				double Salary = result.getDouble("salary");
				LocalDate startDate = result.getDate("start").toLocalDate();
				char gender = result.getString("gender").charAt(0);
				employeePayrollList.add(new EmployeePayrollData(id, name, Salary, startDate, gender));
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



