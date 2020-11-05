package com.capgemini.javaio.employeepayroll;

import java.time.LocalDate;
import java.util.Objects;

public class EmployeePayrollData {
	public int id;
	public String name;
	public double salary;
	public LocalDate startDate;
	public char gender;
	public String [] departments;

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
	
	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate, char gender, String[] departments) {
		this(id, name, salary, startDate, gender);
		this.departments = departments;
	}

	public EmployeePayrollData(String name, double salary, LocalDate date, char gender, String[] departments ) {
		this.name = name;
		this.salary = salary;
		this.startDate = date;
		this.gender = gender;
		this.departments = departments;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, gender, salary, startDate);
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
