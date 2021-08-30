package com.flipkart.business;

import com.flipkart.bean.Course;
import com.flipkart.bean.Professor;
import com.flipkart.bean.Student;
import com.flipkart.dao.ProfessorDaoInterface;
import com.flipkart.dao.ProfessorDaoOperation;
import com.flipkart.exceptions.*;
import org.apache.log4j.Logger;

import java.util.Formatter;
import java.util.List;

/**
 * @author JEDI-07
 * Implementation of professor interface
 */
public class ProfessorOperation implements ProfessorInterface {
    private static Logger logger = Logger.getLogger(ProfessorOperation.class);
    public static Professor professor = null;
    ProfessorDaoInterface professorDaoInterface = new ProfessorDaoOperation();

    /**
     * method to retrieve Professor Details
     */
    public void getProfessor() {
        professor = professorDaoInterface.getProfessorByUserId(UserOperation.user.getId());
    }

    /**
     * method to add Grade in the database
     *
     * @param studentId unique Id for a student
     * @param courseId  unique Id to represent a course
     * @param grade     Grade assigned to student for a course
     * @return returns true if the grade is added successfully by professor
     */
    @Override
    public boolean addGrade(int studentId, int courseId, double grade) throws CourseNotSelectedException, StudentNotEnrolledInCourseException, StudentAlreadyGradedException {

        try {
            boolean courseSelected = professorDaoInterface.isCourseSelected(professor.getProfessorId(), courseId);
            if (!courseSelected) {
                throw new CourseNotSelectedException(courseId);
            }
            boolean isStudentEnrolled = professorDaoInterface.isStudentEnrolled(studentId, courseId);
            if (!isStudentEnrolled) {
                throw new StudentNotEnrolledInCourseException(courseId, studentId);
            }
            boolean studentGraded = professorDaoInterface.isStudentAlreadyGraded(studentId, courseId);
            if (studentGraded) {
                throw new StudentAlreadyGradedException(studentId, courseId);
            }
            return professorDaoInterface.addGrade(studentId, courseId, grade);
        } catch (CourseNotSelectedException e) {
            logger.info("Error: " + e.getMessage());
            throw e;
        } catch (StudentNotEnrolledInCourseException e) {
            logger.info("Error: " + e.getMessage());
            throw e;
        } catch (StudentAlreadyGradedException e) {
            logger.info("Error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Method to view all enrolled students in a particular course
     *
     * @param courseId unique Id to represent a course
     * @return List of Students
     */
    @Override
    public List<Student> getEnrolledStudents(int courseId) throws CourseNotSelectedExcpetion {
        try {
            boolean courseSelected = professorDaoInterface.isCourseSelected(professor.getProfessorId(), courseId);
            if (!courseSelected) {
                throw new CourseNotSelectedExcpetion(courseId);
            }
            return professorDaoInterface.getEnrolledStudents(courseId);
        } catch (CourseNotSelectedExcpetion e) {
            logger.info("Error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * method to view all selected course
     *
     * @return list of selected courses
     */
    @Override
    public List<Course> getSelectedCourses() {
        return professorDaoInterface.getCoursesByProfessorId(professor.getProfessorId());
    }

    /**
     * method to select the course to teach
     *
     * @param courseId unique Id to represent a course
     * @return returns true if course is selected successfully
     */
    @Override
    public boolean selectCourse(int courseId) throws CourseNotAvailableException {
        try {
            boolean courseAvailable = professorDaoInterface.isCourseAvailable(courseId);

            if (!courseAvailable) {
                throw new CourseNotAvailableException(courseId);
            }
            boolean isCourseSelected = professorDaoInterface.selectCourse(professor.getProfessorId(), courseId);
            if (isCourseSelected) {
                return true;
            }
        } catch (CourseNotAvailableException e) {
            logger.info("Error: " + e.getMessage());
            throw e;
        }
        return false;
    }

    /**
     * method to deselect the course
     *
     * @param courseId unique Id to represent a course
     * @return returns true if deselection of course is successful
     */
    @Override
    public boolean deselectCourse(int courseId) throws CourseNotSelectedExcpetion {
        try {
            boolean isCourseSelected = professorDaoInterface.isCourseSelected(professor.getProfessorId(), courseId);
            if (!isCourseSelected) throw new CourseNotSelectedExcpetion(courseId);

            boolean isCourseDeselected = professorDaoInterface.deselectCourse(courseId);
            if (isCourseDeselected) {
                return true;
            }

        } catch (CourseNotSelectedExcpetion e) {
            logger.info("Error: " + e.getMessage());
            throw e;
        }
        return false;
    }

    /**
     * method to view all available courses
     *
     * @return list of courses.
     */
    @Override
    public List<Course> getAvailableCourses() {
        return professorDaoInterface.getAvailableCourses();
    }
}
