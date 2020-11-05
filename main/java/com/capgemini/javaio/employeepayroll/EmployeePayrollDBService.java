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
	private int connectionCounter = 0;

	private EmployeePayrollDBService() {
	}

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null) {
			employeePayrollDBService = new EmployeePayrollDBService();
			Handler fileHandler = null;
			try {
			fileHandler = new FileHandler("C:\\Users\\Mohana Kavya\\eclipse-workspace\\JavaIO_EmployeePayRoll\\src\\test\\resources\\payroll DB Service.log");
			fileHandler.setLevel(Level.ALL);
			log.addHandler(fileHandler);
			} catch (SecurityException | IOException e) {
				log.log(Level.SEVERE, "Failed : "+e);
			}
		}
		return employeePayrollDBService;
	}

	/**
	 * Reading Records from Database to program 
	 * @return List of Employee Payroll objects
	 */
	public List<EmployeePayrollData> readData() {
		String sql = " select e.id, e.name, e.salary, e.start, e.gender, d.department"
				+ " from payroll_employee e"
				+ " inner join emp_department d on e.id = d.id where e.is_active = 1;";	
		return this.getEmployeePayrollDataFromDB(sql);	
	}

	/**
	 * @param name
	 * @param salary
	 * @param startDate
	 * @param gender
	 * @return
	 */
	public int writeEmployeePayrollToDenormalisedDB(String name, double salary, LocalDate startDate, char gender) {
		int id = -1;
		int rowAffected = 0;
		EmployeePayrollData emp_obj = null;
		String sql = String.format(
				"INSERT INTO payroll_employee(name,gender,salary,start) VALUES ('%s','%s','%s','%s')", name, gender,
				salary, Date.valueOf(startDate));
		try (Connection connection = this.getConnection();) {
			PreparedStatement preparedstatement = connection.prepareStatement(sql);
			rowAffected = preparedstatement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = preparedstatement.getGeneratedKeys();
				if (resultSet.next())
					 EmployeePayrollService.newEmpPayrollDataObj.id = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rowAffected;
	}


	/**
	 * To write into Database
	 * @param name
	 * @param salary
	 * @param startDate
	 * @param gender
	 * @return number of rows modified
	 */
	public int writeEmployeePayrollToDB(String name, double salary, LocalDate startDate, char gender, String[] departments) {
		int rowAffected = 0;
		int empId = 0;
		Connection connection = null;
		String sql = String.format("INSERT INTO payroll_employee (name, salary, start, gender)"+ " VALUES('%s',%.2f,'%s','%s')", name, salary, Date.valueOf(startDate), gender);
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SecurityException | SQLException e2) {
			log.log(Level.SEVERE, "Failed : "+e2);
		}
		try  {
			Statement statement = connection.createStatement();
			rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet result = statement.getGeneratedKeys();
				if (result.next()) {
					empId = result.getInt(1);
				}
				try {
					double deductions = salary * 0.2;
					double taxablePay = salary - deductions;
					double tax = taxablePay * 0.1;
					double netPay = salary - tax;
					String sql1 = String.format(
							"INSERT INTO payroll_details(id,basic_pay,deductions,taxable_pay,tax ,net_pay)VALUES (%s,%s,%s,%s,%s,%s)",
							empId, salary, deductions, taxablePay, tax, netPay);
					rowAffected = statement.executeUpdate(sql1);
					if (rowAffected == 1) 
						EmployeePayrollService.newEmpPayrollDataObj.id = empId;
				}  catch (SQLException ex) {
					connection.rollback();
					log.log(Level.SEVERE, "Failed : "+ex);
				}
			}
		} catch (SQLException | SecurityException e) {
			log.log(Level.SEVERE, "Failed : "+e);
			try {
				connection.rollback();
			} catch (SQLException e1) {
				log.log(Level.SEVERE, "Failed : "+e1);
			}
		} 
		try {
			for(String dept : departments) {
			String sql1 = String.format("insert into emp_department (id,department) values ('%s', '%s')", empId, dept);
			Statement statement = connection.createStatement();
			rowAffected = statement.executeUpdate(sql1);
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Failed : "+e);
			try {
				connection.rollback();
			} catch (SQLException e1) {
				log.log(Level.SEVERE, "Failed : "+e1);
			}
		}
		
		try{
			try {
				connection.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
			finally {
			if(connection!=null)
				try {
					connection.close();
				} catch (SQLException e) {
					log.log(Level.SEVERE, "Failed : "+e);
				}
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
		} catch (SQLException | SecurityException e) {
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
		String sql = String.format("select e.id, e.name, e.salary, e.start, e.gender, d.department"
				+" from payroll_employee e inner join"
				+" emp_department d on e.id = d.id WHERE name='%s';", name);
		return this.getEmployeePayrollDataFromDB(sql);
	}

	/**
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<EmployeePayrollData> readEmpPayrollDBInGivenDateRange(LocalDate startDate, LocalDate endDate) {
		String sql = String.format("select e.id, e.name, e.salary, e.start, e.gender, d.department"
				+ "	from payroll_employee e inner join"
				+ "	emp_department d on e.id = d.id where start between '%s' AND '%s' and e.is_active = 1;", Date.valueOf(startDate), Date.valueOf(endDate));
		return this.getEmployeePayrollDataFromDB(sql);
	}

	/**
	 * @return Map Key : Gender, Value is Average of Salaries
	 */
	public Map<String, Double> getAverageSalaryGroupByGender() {
		String sql = "SELECT gender, AVG(salary) as avg_salary FROM payroll_employee where e.is_active = 1 group by gender;";
		Map<String, Double> genderToAverageSalaryMap = new HashMap<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				String gender = result.getString("gender");
				double salary = result.getDouble("avg_salary");
				genderToAverageSalaryMap.put(gender, salary);
			}
		} catch (SQLException | SecurityException e) {
			log.log(Level.SEVERE, "Failed : "+e);
		}
		return genderToAverageSalaryMap;
	}
	
	public int deleteEmployeePayRollFromPayRollTable(String name) {
		int employee_id = -1;
		int rowsAffected = 0 ;
		try (Connection con = this.getConnection()) {
			employeePayrollDataPreparedStatement = con.prepareStatement("select id from payroll_employee where name=?");
			employeePayrollDataPreparedStatement.setString(1, name);
			ResultSet resultSet = employeePayrollDataPreparedStatement.executeQuery();
			while (resultSet.next())
				employee_id = resultSet.getInt("id");
			employeePayrollDataPreparedStatement = con.prepareStatement("delete from payroll_details where id=?");
			employeePayrollDataPreparedStatement.setInt(1, employee_id);
			rowsAffected = employeePayrollDataPreparedStatement.executeUpdate();
			if (rowsAffected > 0) {
				employeePayrollDataPreparedStatement = con.prepareStatement("update payroll_employee set is_active=? where name=?");
				employeePayrollDataPreparedStatement.setBoolean(1, false);
				employeePayrollDataPreparedStatement.setString(2, name);
				employeePayrollDataPreparedStatement.executeUpdate();
			}
		} catch (SQLException | SecurityException e) {
			log.log(Level.SEVERE, "Failed : "+e);
		}
		return rowsAffected;
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
		} catch (SQLException | SecurityException e) {
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
		int id_prev = 0;
		List<EmployeePayrollData> employeePayrollList = new ArrayList();
		List<String> department_list = new ArrayList<>();
			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				double Salary = result.getDouble("salary");
				LocalDate startDate = result.getDate("start").toLocalDate();
				char gender = result.getString("gender").charAt(0);
				String department = result.getString("department");
				if(id!=id_prev) {
					department_list = new ArrayList<>();
					department_list.add(department);
					employeePayrollList.add(new EmployeePayrollData(id, name, Salary, startDate, gender, department_list.toArray(new String[0])));
					id_prev = id;
				}
				else {
					int index = employeePayrollList.indexOf(new EmployeePayrollData(id, name, Salary, startDate, gender, department_list.toArray(new String[0])));
					department_list.add(department);	
					employeePayrollList.set(index, new EmployeePayrollData(id, name, Salary, startDate, gender, department_list.toArray(new String[0])));
				}
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
	private synchronized Connection getConnection() throws SQLException {
		connectionCounter++;
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service";
		String user = "root";
		String password = "L3al!lhope"; 
		Connection connection = null;
		log.info("Processing Thread : " + Thread.currentThread().getName() +" ID : " + connectionCounter + ", Connecting to database : " + jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, user, password);
		log.info("Processing Thread : " + Thread.currentThread().getName() + " ID : " + connectionCounter
				+ " Connection is successful! " + connection);
	return connection;
	}

}



