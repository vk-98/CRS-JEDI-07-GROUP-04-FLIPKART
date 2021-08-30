package com.flipkart.exceptions;

/**
 * @author JEDI-07
 * StudentNotRegisteredException
 */
public class PaymentIncompleteException extends Exception {
    private String studentName;

    /**
     * Parameterized Constructor
     *
     * @param studentName: name of the student
     */
    public PaymentIncompleteException(String studentName) {
        this.studentName = studentName;
    }

    /**
     * getter function for studentName
     *
     * @return returns StudentName
     */
    public String getStudentName() {
        return studentName;
    }

    @Override
    public String getMessage() {
        return "Payment incomplete for " + studentName;
    }
}
