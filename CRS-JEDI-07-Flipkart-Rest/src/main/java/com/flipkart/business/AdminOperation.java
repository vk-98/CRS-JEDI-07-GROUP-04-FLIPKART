package com.flipkart.business;

import com.flipkart.bean.Professor;
import com.flipkart.bean.Student;
import com.flipkart.dao.AdminDaoInterface;
import com.flipkart.dao.AdminDaoOperation;
import com.flipkart.dao.StudentDaoInterface;
import com.flipkart.dao.StudentDaoOperation;
import com.flipkart.exceptions.CourseNotFoundException;
import com.flipkart.exceptions.StudentAlreadyApprovedException;
import com.flipkart.exceptions.StudentNotFoundException;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

/**
 * @author JEDI-07
 * Implementation of Admin Interface
 */
public class AdminOperation implements AdminInterface {
    private static Logger logger = Logger.getLogger(AdminOperation.class);
    AdminDaoInterface adminDaoInterface = new AdminDaoOperation();
    StudentDaoInterface studentDaoInterface = new StudentDaoOperation();

    /**
     * method for adding course into the catalogue
     *
     * @param courseName        Name of the course
     * @param courseDescription A brief description of course
     * @param courseFee         Fee assigned to a course
     * @return returns true if the course is successfully added
     */
    @Override
    public boolean addCourse(String courseName, String courseDescription, double courseFee) {
        return adminDaoInterface.addCourse(courseName, courseDescription, courseFee);
    }

    /**
     * method for removing course
     *
     * @param courseId unique Id to represent a course
     * @return returns true if the course is successfully removed
     */
    @Override
    public boolean removeCourse(int courseId) throws CourseNotFoundException {
        try {
            boolean courseRemoved = adminDaoInterface.removeCourse(courseId);
            if (courseRemoved) {
                throw new CourseNotFoundException(courseId);
            }
            return true;
        } catch (CourseNotFoundException e) {
            logger.info(e.getMessage());
            throw e;
        }
    }

    /**
     * method for approving students admission request.
     *
     * @param studentId unique Id for a student
     * @return returns true if the student's request is approved successfully
     */
    @Override
    public boolean approveStudentRequest(int studentId) throws StudentNotFoundException, StudentAlreadyApprovedException {
        try {
            Student student = studentDaoInterface.getStudentByStudentId(studentId);
            if (student == null) {
                throw new StudentNotFoundException(studentId);
            }
            if (student.isApproved()) {
                throw new StudentAlreadyApprovedException(studentId);
            }
            return adminDaoInterface.approveStudent(studentId);
        } catch (StudentNotFoundException e) {
            logger.info(e.getMessage());
            throw e;
        } catch (StudentAlreadyApprovedException e) {
            logger.info(e.getMessage());
            throw e;
        }
    }

    /**
     * method for adding professor
     *
     * @param name        name of the Professor
     * @param emailId     emailId of the Professor
     * @param password    password for the Professor
     * @param phoneNo     Phone Number of the Professor
     * @param department  Department of the Professor
     * @param designation Designation of the Professor
     * @return returns true if Professor is added successfully
     */
    @Override
    public boolean addProfessor(String name, String emailId, String password, String phoneNo, String department, String designation) throws SQLException {
        try{
            return adminDaoInterface.addProfessor(name, emailId, password, phoneNo, department, designation);
        }catch (SQLException e) {
            throw e;
        }
    }

    /**
     * method for getting all admission requests
     *
     * @return List of Students who made Admission Request
     */
    @Override
    public List<Student> getAdmissionRequests() {
        return adminDaoInterface.getPendingAdmissions();
    }

    /**
     * method for getting all the professors
     *
     * @return List of Professors
     */
    @Override
    public List<Professor> getProfessors() {
        return adminDaoInterface.getProfessors();
    }
}