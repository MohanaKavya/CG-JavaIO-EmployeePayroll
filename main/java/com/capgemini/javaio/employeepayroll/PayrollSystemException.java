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
		UPDATE_DATABASE_EXCEPTION, RETRIEVE_DATA_FOR_DATERANGE_EXCEPTION, MANIPULATE_AND_RETRIEVE_EXCEPTION
	}

	ExceptionType type;

	public PayrollSystemException(String message, ExceptionType type) {
		super(message);
		this.type = type;
	}
}
