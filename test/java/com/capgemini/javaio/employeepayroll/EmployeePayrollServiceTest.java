
package com.capgemini.javaio.employeepayroll;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import com.capgemini.javaio.employeepayroll.EmployeePayrollService.IOService;
/**
 * @author Mohana Kavya
 *
 */
public class EmployeePayrollServiceTest {
	private static Logger log = Logger.getLogger(EmployeePayrollServiceTest.class.getName());
	@Test
	public void given3EmployeesShouldMatchEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmployee = { new EmployeePayrollData(1, "Monica", 100000.0),
				new EmployeePayrollData(2, "Joey", 200000.0), new EmployeePayrollData(3, "Ross", 300000.0) };
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmployee));
		employeePayrollService.writeEmployeePayrollData(IOService.FILE_IO);
		employeePayrollService.printData(IOService.FILE_IO);
		long entries = employeePayrollService.countEntries(IOService.FILE_IO);
		System.out.println(entries);
		Assert.assertEquals(3, entries);
	}
	@Test
	public void writePayrollOnFile() {
		EmployeePayrollData[] arrayOfEmployee = { new EmployeePayrollData(1, "Monica", 100000.0),
				new EmployeePayrollData(2, "Joey", 200000.0), new EmployeePayrollData(3, "Ross", 300000.0) };
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmployee));
		employeePayrollService.writeEmployeePayrollData(IOService.FILE_IO);
		long entries = employeePayrollService.countEntries(IOService.FILE_IO);
		Assert.assertEquals(3, entries);
	}
	
	/*
	 * //UC2
	 * 
	 * @Test public void
	 * givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
	 * log.log(Level.INFO, "JDBC Test UC2"); EmployeePayrollService
	 * employeePayrollService = new EmployeePayrollService();
	 * List<EmployeePayrollData> employeePayrollData =
	 * employeePayrollService.readPayrollData(IOService.DB_IO);
	 * Assert.assertEquals(6, employeePayrollData.size()); }
	 * 
	 * //UC3 and UC4
	 * 
	 * @Test public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDB()
	 * throws PayrollSystemException { log.log(Level.INFO, "JDBC Test UC3 and UC4");
	 * EmployeePayrollService employeePayrollService = new EmployeePayrollService();
	 * employeePayrollService.readPayrollData(IOService.DB_IO);
	 * employeePayrollService.updateEmployeeSalary("kakashi",7000000.0); boolean
	 * result = employeePayrollService.checkEmployeePayrollInSyncWithDB("kakashi");
	 * Assert.assertTrue(result); }
	 * 
	 * //UC5
	 * 
	 * @Test public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount()
	 * throws PayrollSystemException { log.log(Level.INFO, "JDBC Test UC5");
	 * EmployeePayrollService employeePayrollService = new EmployeePayrollService();
	 * employeePayrollService.readPayrollData(IOService.DB_IO); LocalDate startDate
	 * = LocalDate.of(2019, 01, 01); LocalDate endDate = LocalDate.now();
	 * List<EmployeePayrollData> employeePayrollData1 =
	 * employeePayrollService.readEmployeePayrollDataForDateRange(startDate,
	 * endDate); Assert.assertEquals(3, employeePayrollData1.size()); }
	 * 
	 * //UC6
	 * 
	 * @Test public void
	 * findSumAverageMinMaxCount_ofEmployees_ShouldMatchEmployeeCount() throws
	 * PayrollSystemException { log.log(Level.INFO, "JDBC Test UC6");
	 * EmployeePayrollService employeePayrollService = new EmployeePayrollService();
	 * employeePayrollService.readPayrollData(IOService.DB_IO); Map<String, Double>
	 * genderToAverageSalaryMap =
	 * employeePayrollService.getAvgSalary(IOService.DB_IO); Double avgSalaryMale =
	 * 6000000.0; Assert.assertEquals(avgSalaryMale,
	 * genderToAverageSalaryMap.get("M")); Double avgSalaryFemale = 5000000.0;
	 * Assert.assertEquals(avgSalaryFemale, genderToAverageSalaryMap.get("F")); }
	 * 
	 * //UC7 & UC8
	 * 
	 * @Test public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() {
	 * log.log(Level.INFO, "JDBC Test UC7,8,9"); EmployeePayrollService
	 * employeePayrollService = new EmployeePayrollService();
	 * employeePayrollService.readPayrollData(IOService.DB_IO); String [] dept =
	 * {"Arts"}; EmployeePayrollService.newEmpPayrollDataObj = new
	 * EmployeePayrollData("Tsunade",5000000.0,LocalDate.of(2016, 05, 05),'F',
	 * dept); employeePayrollService.writeEmployeePayrollData(IOService.DB_IO);
	 * boolean
	 * result=employeePayrollService.checkEmployeePayrollInSyncWithDB("Tsunade");
	 * Assert.assertTrue(result); }
	 * 
	 * //UC12
	 * 
	 * @Test public void givenEmployeeName_WhenDeleted_ShouldBeInSyncWithDB() {
	 * log.log(Level.INFO, "JDBC Test UC12"); EmployeePayrollService
	 * employeePayrollService = new EmployeePayrollService();
	 * employeePayrollService.readPayrollData(IOService.DB_IO);
	 * employeePayrollService.deleteEmployeePayRollFromPayRollTableAndList("Minato")
	 * ; EmployeePayrollData employee =
	 * employeePayrollService.employeePayrollList.stream() .filter(emp ->
	 * emp.name.equals("Minato")).findFirst().orElse(null); assertEquals(null,
	 * employee); }
	 */
	
	//UC1 Threads
	@Test
	public void given6Employee_WhenAddedToDB_ShouldMatchEmployeeEnteries() {
		 log.log(Level.INFO, "JDBC Test UC1,2 Threads");
		EmployeePayrollData[] arrayOfEmps = {
				new EmployeePayrollData(0, "Harvey Specter", 3000000.0, LocalDate.now(), 'M'),
				new EmployeePayrollData(0, "Rachel Zane", 2000000.0, LocalDate.now(), 'F'),
				new EmployeePayrollData(0, "Louis Litt", 2000000.0, LocalDate.now(), 'M'),
				new EmployeePayrollData(0, "Mike Ross", 2000000.0, LocalDate.now(), 'M') };
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readPayrollData(IOService.DB_IO);
		Instant start = Instant.now();
		employeePayrollService.addEmployeesToPayroll(Arrays.asList(arrayOfEmps));
		Instant end = Instant.now();
		log.info("Duration without thread: " + Duration.between(start, end));
		Instant threadStart = Instant.now();
		employeePayrollService.addEmployeeToPayrollWithThreads(Arrays.asList(arrayOfEmps));
		Instant threadEnd = Instant.now();
		log.info("Duartion with Thread : "+Duration.between(threadStart, threadEnd));
		Assert.assertEquals(13, employeePayrollService.countEntries(IOService.DB_IO));
	}
	
}
