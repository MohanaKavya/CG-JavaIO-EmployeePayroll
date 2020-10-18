/**
 * 
 */
package com.capgemini.javaio.employeepayroll;
import org.junit.Assert;
import java.util.Arrays;
import org.junit.Test;

import com.capgemini.javaio.employeepayroll.EmployeePayrollService.IOService;
/**
 * @author Mohana Kavya
 *
 */
public class EmployeePayrollServiceTest {
	@Test
	public void gievn3EmployeesShouldMatchEmployeeEntries() {
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
	}
}
