package com.capgemini.javaio.employeepayroll;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EmployeePayrollService {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}
		private List<EmployeePayrollData> employeePayrollList;
		private EmployeePayrollDBService employeePayrollDBService;
		private Map<String, Double> genderToAverageSalaryMap;
		public static EmployeePayrollData newEmpPayrollDataObj;

		public EmployeePayrollService() {
			employeePayrollDBService=EmployeePayrollDBService.getInstance();
		}

		public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
			this();
			this.employeePayrollList = employeePayrollList;
		}

		public static void main(String[] args) {
			ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<>();
			EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
			Scanner consoleInputReader = new Scanner(System.in);
			employeePayrollService.readEmployeePayrollData(consoleInputReader);
			employeePayrollService.writeEmployeePayrollData(IOService.CONSOLE_IO);
		}
		private void readEmployeePayrollData(Scanner consoleInputReader) {
			System.out.println("Enter the employee ID : ");
			int id = consoleInputReader.nextInt();
			System.out.println("Enter the employee name : ");
			String name = consoleInputReader.next();
			System.out.println("Enter the employee's salary : ");
			double salary = consoleInputReader.nextDouble();

			employeePayrollList.add(new EmployeePayrollData(id, name, salary));
		}

		public void writeEmployeePayrollData(IOService ioService) {
			if (ioService.equals(IOService.CONSOLE_IO))
				System.out.println("Writing Employee payroll data on Console: " + employeePayrollList);
			else if (ioService.equals(IOService.FILE_IO))
				new EmployeePayrollFileIOService().writeData(employeePayrollList);
			else if(ioService.equals(IOService.DB_IO))
				this.addEmpPayrollToDB();
		}

		public void printData(IOService ioService) {
			if(ioService.equals(IOService.FILE_IO))
				new EmployeePayrollFileIOService().printData();
		}

		public long countEntries(IOService ioService) {
			if(ioService.equals(IOService.FILE_IO))
				return new EmployeePayrollFileIOService().countEntries();
			return 0;
		}

		/**
		 * @param IOService type
		 * @return List of Employee Payroll Data
		 */
		public List<EmployeePayrollData> readPayrollData(IOService ioService) {
			if (ioService.equals(IOService.FILE_IO))
				this.employeePayrollList = new EmployeePayrollFileIOService().readData();
			else if(ioService.equals(IOService.DB_IO))
				this.employeePayrollList = employeePayrollDBService.readData();
			return employeePayrollList;
		}

		/**
		 * Update Record in Database and Employee Payroll object
		 * @param string Employee name
		 * @param double salary
		 * @throws PayrollSystemException 
		 */
		public void updateEmployeeSalary(String name, double salary) {
			try {
				int[] numOfRowsModified = employeePayrollDBService.updateEmployeeData(name, salary);
				if (numOfRowsModified == null) 
					throw new PayrollSystemException("no rows updated", PayrollSystemException.ExceptionType.UPDATE_DATABASE_EXCEPTION);
				EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
				if (employeePayrollData != null)
					employeePayrollData.salary = salary;
			} catch(PayrollSystemException e) {
				System.out.println(e.getMessage());
			}
		}

		/**
		 * Fetch Particular Employee Payroll object by Employee Name
		 * @param Employee name
		 * @return Employee Payroll Data Object
		 */
		private EmployeePayrollData getEmployeePayrollData(String name) {
			return this.employeePayrollList.stream().filter(emp -> emp.name.equals(name)).findFirst().orElse(null);
		}

		/**
		 * Check the data sync between Employee Payroll objects and Database records
		 * @param name
		 * @return boolean value
		 */
		public boolean checkEmployeePayrollInSyncWithDB(String name) {
			List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollDataByName(name);
			return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
		}

		/**
		 * Fetching Data Based on Given Date Range
		 * @param startDate
		 * @param endDate
		 * @return List of Employee Payroll Data
		 * @throws PayrollSystemException 
		 */
		public List<EmployeePayrollData> readEmployeePayrollDataForDateRange(LocalDate startDate, LocalDate endDate) {
			try {
			List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.readEmpPayrollDBInGivenDateRange(startDate, endDate);
			if(employeePayrollDataList!=null)
				return employeePayrollDataList;
			else
				throw new PayrollSystemException("no rows selected for the given date range", PayrollSystemException.ExceptionType.RETRIEVE_DATA_FOR_DATERANGE_EXCEPTION);
			} catch(PayrollSystemException e) {
				System.out.println(e.getMessage());
			}
			return null;
		}

		/**
		 * @param IOService Type
		 * @return Map Key : Gender, Value : Avg Salary 
		 */
		public Map<String, Double> getAvgSalary(IOService ioService) {
			try {
				if (ioService.equals(IOService.DB_IO))
					this.genderToAverageSalaryMap = employeePayrollDBService.getAverageSalaryGroupByGender();
				if (genderToAverageSalaryMap.isEmpty()) {
					throw new PayrollSystemException("no data retrieved",
							PayrollSystemException.ExceptionType.MANIPULATE_AND_RETRIEVE_EXCEPTION);
				}
			} catch (PayrollSystemException e) {
				System.out.println(e.getMessage());
			}
			return genderToAverageSalaryMap;
		}
		
		/**
		 * Insert new Record into Database, If Successful should add new Employee Payroll data into List
		 */
		private void addEmpPayrollToDB() {
			try {
				int rowsModified = employeePayrollDBService.writeEmployeePayrollToDB(newEmpPayrollDataObj.name, newEmpPayrollDataObj.salary,
						newEmpPayrollDataObj.startDate, newEmpPayrollDataObj.gender, newEmpPayrollDataObj.departments);
				if(rowsModified==1)
					this.employeePayrollList.add(newEmpPayrollDataObj);
				else
					throw new PayrollSystemException("Failed to Insert new rows", PayrollSystemException.ExceptionType.INSERT_INTO_DB_EXCEPTION);				
			} catch (PayrollSystemException e) {
				System.out.println(e.getMessage());
			}	
		}
	
}

