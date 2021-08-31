package com.flipkart.business;

import com.flipkart.bean.Grade;
import com.flipkart.bean.GradeCard;
import com.flipkart.bean.Student;
import com.flipkart.exceptions.PaymentIncompleteException;
import com.flipkart.exceptions.StudentNotRegisteredException;

import java.sql.SQLException;
import java.util.List;

/**
 * @author JEDI-07
 * Student Interface
 */
public interface StudentInterface {
    /**
     * method for registering a student
     *
     * @param studentName     name of the Student
     * @param studentEmailId  emailId of the Student
     * @param studentPassword password for the Student
     * @param studentPhoneNo  Phone No of the Student
     * @return returns registered Student object
     */
    Student register(String studentName, String studentEmailId, String studentPassword, String studentPhoneNo) throws StudentNotRegisteredException, SQLException;

    /**
     * method getting all the grades
     *
     * @return list of grades
     */
    List<Grade> getGrades();

    /**
     * method for getting student by emailId
     */
    void getStudentByEmailId();

    /**
     * method for getting gradecard
     * @return
     */
    GradeCard getGradeCard() throws StudentNotRegisteredException, PaymentIncompleteException, SQLException;
}
