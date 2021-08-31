package com.flipkart.business;

import com.flipkart.bean.Grade;
import com.flipkart.bean.GradeCard;
import com.flipkart.bean.Student;
import com.flipkart.dao.*;
import com.flipkart.exceptions.PaymentIncompleteException;
import com.flipkart.exceptions.StudentNotRegisteredException;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

/**
 * @author JEDI-07
 * Implementation of Student Interface
 */
public class StudentOperation implements StudentInterface {
    private static Logger logger = Logger.getLogger(StudentOperation.class);
    public static Student student = null;
    StudentDaoInterface studentDaoInterface = new StudentDaoOperation();
    SemesterRegistrationDaoInterface semesterRegistrationDaoInterface = new SemesterRegistrationDaoOperation();
    SemesterRegistrationInterface semesterRegistrationInterface = new SemesterRegistrationOperation();

    /**
     * method for registering a student
     *
     * @param studentName     name of the Student
     * @param studentEmailId  emailId of the Student
     * @param studentPassword password for the Student
     * @param studentPhoneNo  Phone No of the Student
     * @return returns registered Student object
     */
    @Override
    public Student register(String studentName, String studentEmailId, String studentPassword, String studentPhoneNo) throws StudentNotRegisteredException, SQLException {
        Student student = null;
        try {
            StudentDaoInterface studentDao = new StudentDaoOperation();
            boolean added = studentDao.addStudent(studentName, studentEmailId, studentPassword, studentPhoneNo);
            if (!added) {
                throw new StudentNotRegisteredException(studentName);
            } else {
                student = new Student(studentName, studentEmailId, studentPassword, studentPhoneNo);
            }
        } catch (StudentNotRegisteredException ex) {
            logger.error(ex.getStudentName() + " is not registered.");
            throw ex;
        } catch (SQLException ex) {
            throw ex;
        }
        return student;
    }

    /**
     * method getting all the grades
     *
     * @return list of grades
     */
    @Override
    public List<Grade> getGrades() {
        try {
            boolean isRegistered = semesterRegistrationDaoInterface.getRegistrationStatus(StudentOperation.student.getStudentId());
            if (!isRegistered) {
                throw new StudentNotRegisteredException(StudentOperation.student.getUserName());
            }

            boolean paymentStatus = semesterRegistrationDaoInterface.getPaymentStatus(StudentOperation.student.getStudentId());
            if (!paymentStatus) throw new PaymentIncompleteException(StudentOperation.student.getUserName());

            return studentDaoInterface.getGrades(StudentOperation.student.getStudentId());

        } catch (StudentNotRegisteredException e) {
            logger.info(e.getMessage());
        } catch (PaymentIncompleteException e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    /**
     * method getting gradecard
     *
     * @return list of grades
     */
    @Override
    public GradeCard getGradeCard() throws StudentNotRegisteredException, PaymentIncompleteException, SQLException {
        try {
            boolean isRegistered = semesterRegistrationDaoInterface.getRegistrationStatus(StudentOperation.student.getStudentId());
            if (!isRegistered) {
                throw new StudentNotRegisteredException(StudentOperation.student.getUserName());
            }

            boolean paymentStatus = semesterRegistrationDaoInterface.getPaymentStatus(StudentOperation.student.getStudentId());
            if (!paymentStatus) {
                throw new PaymentIncompleteException(StudentOperation.student.getUserName());
            }

            GradeCard gradeCard = new GradeCard();
            List<Grade> grades = studentDaoInterface.getGrades(StudentOperation.student.getStudentId());
            gradeCard.setGrades(grades);
            double gradeSum = 0;
            for (Grade grade : grades) {
                gradeSum += grade.getGpa();
            }
            gradeCard.setCgpa(gradeSum / grades.size());
            return gradeCard;

        } catch (StudentNotRegisteredException e) {
            logger.info("Error: " + e.getMessage());
            throw e;
        } catch (PaymentIncompleteException e) {
            logger.info("Error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * method for getting student by emailId
     */
    @Override
    public void getStudentByEmailId() {
        student = studentDaoInterface.getStudentByEmailId(UserOperation.user.getUserEmailId());
    }
}