package com.capgemini.javaio.employeepayroll;

import java.time.LocalDate;

public class EmployeePayrollData {
	public int id;
	public String name;
	public double salary;
	public LocalDate startDate;
	public char gender;

	public EmployeePayrollData(Integer id, String name, Double salary) {
		this.id = id;
		this.name = name;
		this.salary = salary;
	}
	
	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate) {
		this(id, name, salary);
		this.startDate = startDate;
	}
	
	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate, char gender) {
		this(id, name, salary, startDate);
		this.gender = gender;
	}

	public EmployeePayrollData(String name, double salary, LocalDate date, char gender) {
		this.name = name;
		this.salary = salary;
		this.startDate = date;
		this.gender = gender;
	}

	@Override
	public String toString() {
		return "id= " + id + ", name= " + name + ", salary= " + salary+ ", Start Date= " + startDate+ ", Gender= " + gender;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		EmployeePayrollData that = (EmployeePayrollData) o;
		return id == that.id && Double.compare(that.salary, salary) == 0 && name.equals(that.name) && gender == that.gender;
	}
}
