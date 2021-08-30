package com.flipkart.dao;

import com.flipkart.bean.Course;
import com.flipkart.bean.OptedCourse;
import com.flipkart.business.StudentOperation;
import com.flipkart.constants.Courses;
import com.flipkart.constants.SqlQueries;
import com.flipkart.utils.DBUtil;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JEDI-07
 * SmesterRegistration for DAO Operation
 */
public class SemesterRegistrationDaoOperation implements SemesterRegistrationDaoInterface {

    static Connection conn = DBUtil.getConnection();
    NotificationDaoInterface notificationDaoInterface = new NotificationDaoOperation();

    private static Logger logger = Logger.getLogger(SemesterRegistrationDaoOperation.class);

    /**
     * method for Student registration for Semester
     *
     * @return returns true if student successfully registers
     */
    @Override
    public boolean registerForSemester() {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.ADD_SEMESTER_REGISTRATION);
            ps.setInt(1, StudentOperation.student.getStudentId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * method to get Semester Id
     *
     * @return returns the semesterId
     */
    @Override
    public int getSemesterId() {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.GET_SEMESTER_ID);
            ps.setInt(1, StudentOperation.student.getStudentId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * method to get number of courses selected by student
     *
     * @param isPrimary Indicates if the course is primary or not
     * @return returns the number of courses selected by student
     */
    @Override
    public int getCourseCount(int isPrimary) {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.GET_COURSE_COUNT);
            ps.setInt(1, StudentOperation.student.getStudentId());
            ps.setInt(2, isPrimary);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * method to check if a course is available for the semster
     *
     * @param courseId unique Id to represent a course
     * @return returns true if course is available for semester
     */
    @Override
    public boolean checkAvailability(int courseId) {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.CHECK_COURSE_AVAILABILITY);
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("studentCount") < Courses.MAX_STUDENT_LIMIT;
            }
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Method to add course
     *
     * @param courseId   unique Id to represent a course
     * @param semesterId Id of the Current Semester
     * @param isPrimary  Indicates if the course is primary or not
     * @return returns true if the course is added successfully
     */
    @Override
    public boolean addCourse(int courseId, int semesterId, int isPrimary, int studentId) {

        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.ADD_COURSE_STUDENT);
            ps.setInt(1, courseId);
            ps.setInt(2, semesterId);
            ps.setInt(3, isPrimary);
            ps.setInt(4, studentId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return false;
    }


    /**
     * Method to Drop Course
     *
     * @param courseId unique Id to represent a course
     * @return returns true if the course is dropped successfully
     */
    @Override
    public boolean dropCourse(int courseId, int studentId) {
        try {

            PreparedStatement ps = conn.prepareStatement(SqlQueries.DROP_COURSE);
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);

            int rowAffected = ps.executeUpdate();
            return rowAffected == 1;
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Method to Get List of Registered  courses of the Student for a semester
     *
     * @return List of Registered Courses
     */
    @Override
    public List<OptedCourse> getRegisteredCourses(int studentId) {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.VIEW_REGISTERED_STUDENT_COURSES);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            List<OptedCourse> registeredCourseList = new ArrayList<OptedCourse>();

            while (rs.next()) {
                OptedCourse oc = new OptedCourse();
                oc.setCourseId(rs.getInt("id"));
                oc.setCourseName(rs.getString("courseName"));
                oc.setStudentCount(rs.getInt("studentCount"));
                oc.setCourseFee(rs.getDouble("courseFee"));
                oc.setIsPrimary(rs.getInt("isPrimary") == 1);

                registeredCourseList.add(oc);
            }
            return registeredCourseList;
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }

        return null;
    }

    /**
     * Method to Get List of Selected  courses of the Student for a semester
     *
     * @return List of Selected Courses
     */
    @Override
    public List<OptedCourse> getSelectedCourses(int studentId) {
        try {

            PreparedStatement ps = conn.prepareStatement(SqlQueries.VIEW_SELECTED_STUDENT_COURSES);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            List<OptedCourse> registeredCourseList = new ArrayList<OptedCourse>();

            while (rs.next()) {
                OptedCourse oc = new OptedCourse();
                oc.setCourseId(rs.getInt("id"));
                oc.setCourseName(rs.getString("courseName"));
                oc.setStudentCount(rs.getInt("studentCount"));
                oc.setCourseFee(rs.getDouble("courseFee"));
                oc.setIsPrimary(rs.getInt("isPrimary") == 1);

                registeredCourseList.add(oc);
            }
            return registeredCourseList;
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }

        return null;
    }


    /**
     * Method to find if course is already registered
     *
     * @param courseId unique Id to represent a course
     * @return returns true if course is already registered or not
     */
    @Override
    public boolean isCourseAlreadyRegistered(int courseId, int studentId) {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.CHECK_COURSE_STUDENT);
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) >= 1;
            }
        } catch (SQLException e) {
            logger.info(e.getMessage());
        }
        return false;
    }


    /**
     * Method to get registration status of a student for a semester
     *
     * @param studentId unique Id for a student
     * @return returns true if the student is registered for the semester
     */
    @Override
    public boolean getRegistrationStatus(int studentId) {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.GET_REGISTRATION_STATUS);
            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 1;
            }
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Method to Get Payment Status of student
     *
     * @return returns true if Payment is done successfully by the student
     */
    @Override
    public boolean getPaymentStatus(int studentId) {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.GET_PAYMENT_STATUS);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1) == 1;
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Method to allot course
     *
     * @param courseId unique Id to represent a course
     * @return returns true if course is allotted successfully
     */
    @Override
    public boolean allotCourse(int courseId, int studentId) {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.ALLOT_COURSE);
            ps.setInt(1, courseId);
            ps.setInt(2, studentId);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Method to Submit Registration
     *
     * @param courseFee Fee assigned to a course
     * @return returns true if registration submitted successfully
     */
    @Override
    public boolean submitRegistration(double courseFee, int studentId) {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.SUBMIT_REGISTRATION);
            ps.setDouble(1, courseFee);
            ps.setInt(2, studentId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Method to Update Student count for a course
     *
     * @param courseId unique Id to represent a course
     * @return returns true if Student count is updated
     */
    @Override
    public boolean updateStudentCount(int courseId) {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.UPDATE_STUDENT_COUNT);
            ps.setInt(1, courseId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Method to get Pending fee
     *
     * @return returns the pending fee
     */
    @Override
    public double getPendingFee(int studentId) {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.GET_PENDING_FEE);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("feeStatus") == 0 && rs.getInt("registrationStatus") == 1) {
                    return rs.getDouble("totalFees");
                }
            }
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Method to Pay fee
     *
     * @param amount Amount to be paid
     * @return returns true if payment is successful
     */

    @Override
    public boolean payFee(double amount, int studentId) {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.PAY_FEE);
            ps.setInt(1, studentId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return false;
    }
}
