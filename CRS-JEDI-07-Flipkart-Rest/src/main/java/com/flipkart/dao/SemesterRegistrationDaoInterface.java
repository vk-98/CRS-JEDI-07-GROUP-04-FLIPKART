package com.flipkart.dao;

import java.sql.SQLException;
import java.util.List;

import com.flipkart.bean.Course;
import com.flipkart.bean.Grade;
import com.flipkart.bean.OptedCourse;

/**
 * @author JEDI-07
 * SemesterRegistration for DAO Interface
 */
public interface SemesterRegistrationDaoInterface {

    /**
     * method for Student registration for Semester
     *
     * @return returns true if student successfully registers
     */
    public boolean registerForSemester();

    /**
     * method to get Semester Id
     *
     * @return returns the semesterId
     */
    public int getSemesterId();

    /**
     * method to get number of courses selected by student
     *
     * @param isPrimary Indicates if the course is primary or not
     * @return returns the number of courses selected by student
     */
    public int getCourseCount(int isPrimary);

    /**
     * method to check if a course is available for the semster
     *
     * @param courseId unique Id to represent a course
     * @return returns true if course is available for semester
     */

    boolean checkAvailability(int courseId);

    /**
     * Method to add course
     *
     * @param courseId   unique Id to represent a course
     * @param semesterId Id of the Current Semester
     * @param isPrimary  Indicates if the course is primary or not
     * @return returns true if the course is added successfully
     */
    boolean addCourse(int courseId, int semesterId, int isPrimary, int studentId);

    /**
     * Method to Drop Course
     *
     * @param courseId unique Id to represent a course
     * @return returns true if the course is dropped successfully
     */
    boolean dropCourse(int courseId, int studentId);

    /**
     * Method to Get List of Registered  courses of the Student for a semester
     *
     * @return List of Registered Courses
     */

    List<OptedCourse> getRegisteredCourses(int studentId);

    /**
     * Method to Get List of Selected  courses of the Student for a semester
     *
     * @return List of Selected Courses
     */
    List<OptedCourse> getSelectedCourses(int studentId);

    /**
     * Method to find if course is already registered
     *
     * @param courseId unique Id to represent a course
     * @return returns true if course is already registered or not
     */
    boolean isCourseAlreadyRegistered(int courseId, int studentId);

    /**
     * Method to get registration status of a student for a semester
     *
     * @param studentId unique Id for a student
     * @return returns true if the student is registered for the semester
     */
    boolean getRegistrationStatus(int studentId);

    /**
     * Method to Get Payment Status of student
     *
     * @return returns true if Payment is done successfully by the student
     */
    boolean getPaymentStatus(int studentId);

    /**
     * Method to allot course
     *
     * @param courseId unique Id to represent a course
     * @return returns true if course is allotted successfully
     */
    boolean allotCourse(int courseId, int studentId);

    /**
     * Method to Submit Registration
     *
     * @param courseFee Fee assigned to a course
     * @return returns true if registration submitted successfully
     */
    boolean submitRegistration(double courseFee, int studentId);

    /**
     * Method to Update Student count for a course
     *
     * @param courseId unique Id to represent a course
     * @return returns true if Student count is updated
     */
    boolean updateStudentCount(int courseId);

    /**
     * Method to get Pending fee
     *
     * @return returns the pending fee
     */
    double getPendingFee(int studentId);

    /**
     * Method to Pay fee
     *
     * @param amount Amount to be paid
     * @return returns true if payment is successful
     */
    boolean payFee(double amount, int studentId);

}
