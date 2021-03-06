package com.flipkart.dao;

import com.flipkart.bean.Course;
import com.flipkart.bean.Professor;
import com.flipkart.bean.Student;
import com.flipkart.constants.Roles;
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
 * Implementation of admin dao interface
 */
public class AdminDaoOperation implements AdminDaoInterface {
    private static Logger logger = Logger.getLogger(AdminDaoOperation.class);
    Connection conn = DBUtil.getConnection();
    UserDaoInterface userDaoInterface = new UserDaoOperation();

    /**
     * method for adding course into database
     *
     * @param courseName        Name of the course
     * @param courseDescription A brief description of course
     * @param courseFee         Fee assigned to a course
     * @return returns true if the course is added successfully
     */
    @Override
    public boolean addCourse(String courseName, String courseDescription, double courseFee) {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.ADD_COURSE);
            ps.setString(1, courseName);
            ps.setString(2, courseDescription);
            ps.setDouble(3, courseFee);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * method for removing course from the database
     *
     * @param courseId unique Id to represent a course
     * @return returns true if the course is removed successfully
     */
    @Override
    public boolean removeCourse(int courseId) {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.REMOVE_COURSE);
            ps.setInt(1, courseId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * method for getting all Pending admission requests
     *
     * @return List of students with pending request
     */
    @Override
    public List<Student> getPendingAdmissions() {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.LIST_APPROVAL_REQUESTS);
            ResultSet rs = ps.executeQuery();
            List<Student> admissions = new ArrayList<Student>();
            while (rs.next()) {
                Student s = new Student();
                s.setStudentId(rs.getInt("id"));
                s.setUserName(rs.getString("name"));
                s.setUserEmailId(rs.getString("email"));
                admissions.add(s);
            }
            return admissions;
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * method to approve a student by student id
     *
     * @param studentId unique Id for a student
     * @return returns true if student is approved successfully
     */
    @Override
    public boolean approveStudent(int studentId) {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.APPROVE_ADDMISSION_REQUEST);
            ps.setInt(1, studentId);
            int rowAffected = ps.executeUpdate();
            return rowAffected == 1;
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * method for adding professor into the database
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
        try {
            boolean IsUserCreated = userDaoInterface.createUser(name, emailId, password, Roles.Professor, phoneNo);
            if (IsUserCreated) {
                int id = userDaoInterface.getUserIdByEmail(emailId);
                try {
                    PreparedStatement ps = conn.prepareStatement(SqlQueries.ADD_PROFESSOR);
                    ps.setInt(1, id);
                    ps.setString(2, department);
                    ps.setString(3, designation);
                    return ps.executeUpdate() == 1;
                } catch (SQLException e) {
                    logger.info("Error: " + e.getMessage());
                }
            }
        }catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
            throw e;
        }
        return false;
    }

    /**
     * method for Viewing all the courses in the database
     *
     * @return List of courses
     */
    @Override
    public List<Course> viewCourses() {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.LIST_COURSES);
            ResultSet rs = ps.executeQuery();
            List<Course> courses = new ArrayList<Course>();
            while (rs.next()) {
                Course c = new Course();
                c.setCourseId(rs.getInt("id"));
                c.setCourseName(rs.getString("courseName"));
                c.setCourseDescription(rs.getString("courseDescription"));
                c.setCourseFee(rs.getDouble("courseFee"));
                c.setStudentCount(rs.getInt("studentCount"));
                c.setProfessorId(rs.getInt("professorId"));
                courses.add(c);
            }
            return courses;
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return null;
    }

    /**
     * method for getting all the professors
     *
     * @return List of Professors
     */
    @Override
    public List<Professor> getProfessors() {
        try {
            PreparedStatement ps = conn.prepareStatement(SqlQueries.LIST_PROFESSORS);
            ResultSet rs = ps.executeQuery();
            List<Professor> professors = new ArrayList<Professor>();
            while (rs.next()) {
                Professor p = new Professor();
                p.setProfessorId(rs.getInt("id"));
                p.setUserName(rs.getString("name"));
                p.setUserEmailId(rs.getString("email"));
                p.setDepartment(rs.getString("department"));
                p.setDesignation(rs.getString("designation"));
                professors.add(p);
            }
            return professors;
        } catch (SQLException e) {
            logger.info("Error: " + e.getMessage());
        }
        return null;
    }
}
