/**
 * 
 */
package com.capgemini.javaio.employeepayroll;

/**
 * @author Mohana Kavya
 *
 */
public class PayrollSystemException extends Exception {
	enum ExceptionType {
		UPDATE_DATABASE_EXCEPTION
	}

	ExceptionType type;

	public PayrollSystemException(String message, ExceptionType type) {
		super(message);
		this.type = type;
	}
}
