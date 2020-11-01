package com.capgemini.javaio.employeepayroll;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeePayrollService {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}
		private List<EmployeePayrollData> employeePayrollList;

		public EmployeePayrollService() {
		}

		public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
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
				this.employeePayrollList = new EmployeePayrollDBService().readData();
			return employeePayrollList;
		}

		/**
		 * Update Record in Database and Employee Payroll object
		 * @param string Employee name
		 * @param double salary
		 * @throws PayrollSystemException 
		 */
		public void updateEmployeeSalary(String name, double salary) throws PayrollSystemException {
			int numOfRowsModified = new EmployeePayrollDBService().updateEmployeeData(name, salary);
			if (numOfRowsModified == 0) {
				throw new PayrollSystemException("no rows updated", PayrollSystemException.ExceptionType.UPDATE_DATABASE_EXCEPTION);
			}
			EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
			if (employeePayrollData != null)
				employeePayrollData.salary = salary;
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
			List<EmployeePayrollData> employeePayrollDataList = new EmployeePayrollDBService().getEmployeePayrollData(name);
			return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
		}


}
