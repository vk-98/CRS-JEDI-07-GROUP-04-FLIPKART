package com.flipkart.business;

import com.flipkart.bean.Grade;
import com.flipkart.bean.GradeCard;
import com.flipkart.bean.Student;
import com.flipkart.dao.*;
import com.flipkart.exceptions.StudentNotRegisteredException;
import org.apache.log4j.Logger;

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
     * @param studentName
     * @param studentEmailId
     * @param studentPassword
     * @param studentPhoneNo
     * @return
     */
    @Override
    public Student register(String studentName, String studentEmailId, String studentPassword, String studentPhoneNo) {
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
            logger.info(logger.getClass());
            logger.error(ex.getStudentName() + " is not registered.");
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
        boolean isRegistered = semesterRegistrationDaoInterface.getRegistrationStatus();
        if (!isRegistered) {
            logger.info("Student is not registered for the semester");
            return null;
        }

        boolean paymentStatus = semesterRegistrationDaoInterface.getPaymentStatus();
        if (paymentStatus) {
            return studentDaoInterface.getGrades(StudentOperation.student.getStudentId());
        }
        return null;
    }

    /**
     * method getting gradecard
     *
     * @return list of grades
     */
    @Override
    public GradeCard getGradeCard() {
        boolean isRegistered = semesterRegistrationDaoInterface.getRegistrationStatus();
        if (!isRegistered) {
            logger.info("Student is not registered for the semester");
            return null;
        }

        boolean paymentStatus = semesterRegistrationDaoInterface.getPaymentStatus();
        if (paymentStatus) {
            GradeCard gradeCard = new GradeCard();
            List<Grade> grades = studentDaoInterface.getGrades(StudentOperation.student.getStudentId());
            gradeCard.setGrades(grades);
            double gradeSum = 0;
            for (Grade grade : grades) {
                gradeSum += grade.getGpa();
            }
            gradeCard.setCgpa(gradeSum / grades.size());
            return gradeCard;
        }
        return null;
    }


    /**
     * method for getting student by emailid
     */
    @Override
    public void getStudentByEmailId() {
        student = studentDaoInterface.getStudentByEmailId(UserOperation.user.getUserEmailId());
    }
}