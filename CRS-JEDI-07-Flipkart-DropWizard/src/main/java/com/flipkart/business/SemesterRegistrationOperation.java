package com.flipkart.business;

import com.flipkart.bean.OptedCourse;
import com.flipkart.constants.Courses;
import com.flipkart.dao.SemesterRegistrationDaoInterface;
import com.flipkart.dao.SemesterRegistrationDaoOperation;
import com.flipkart.exceptions.*;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author JEDI-07
 * Implementation of Semester Registration Interface
 */
public class SemesterRegistrationOperation implements SemesterRegistrationInterface {

    SemesterRegistrationDaoInterface semesterRegistrationDaoInterface = new SemesterRegistrationDaoOperation();
    NotificationOperation notificationOperation = new NotificationOperation();

    private static Logger logger = Logger.getLogger(SemesterRegistrationOperation.class);


    /**
     * method for adding course for the logged in user
     *
     * @param courseId  unique Id to represent a course
     * @param isPrimary isPrimary Indicates if the course is primary or not
     * @return returns true if the course is added successfully
     */
    @Override
    public boolean addCourse(int courseId, int isPrimary) throws StudentAlreadyRegisteredForSemesterException, MaxCoursesAlreadySelectedException, CourseAlreadyRegisteredException, SeatNotAvailableException, SQLException {

        try {
            boolean isRegistered = semesterRegistrationDaoInterface.getRegistrationStatus(StudentOperation.student.getStudentId());

            if (isRegistered) {
                throw new StudentAlreadyRegisteredForSemesterException();
            }

            if (isPrimary == 1) {
                int primaryCourseCount = semesterRegistrationDaoInterface.getCourseCount(1);
                if (primaryCourseCount >= Courses.MAX_PRIMARY_COURSES) {
                    throw new MaxCoursesAlreadySelectedException(Courses.MAX_PRIMARY_COURSES, "primary");
                }
            } else {
                int secondaryCourseCount = semesterRegistrationDaoInterface.getCourseCount(0);
                if (secondaryCourseCount >= Courses.MAX_SECONDARY_COURSES) {
                    throw new MaxCoursesAlreadySelectedException(Courses.MAX_SECONDARY_COURSES, "secondary");
                }
            }
            boolean isCourseAlreadyRegistered = semesterRegistrationDaoInterface.isCourseAlreadyRegistered(courseId, StudentOperation.student.getStudentId());

            if (isCourseAlreadyRegistered) {
                throw new CourseAlreadyRegisteredException(courseId);
            }

            boolean isAvailable = semesterRegistrationDaoInterface.checkAvailability(courseId);

            if (!isAvailable) {
                throw new SeatNotAvailableException(courseId);
            }

            int semesterId = semesterRegistrationDaoInterface.getSemesterId();

            if (semesterId == 0) {
                boolean registered = semesterRegistrationDaoInterface.registerForSemester();
                if (registered) {
                    semesterId = semesterRegistrationDaoInterface.getSemesterId();
                }
            }

            boolean courseAdded = semesterRegistrationDaoInterface.addCourse(courseId, semesterId, isPrimary, StudentOperation.student.getStudentId());

            if (courseAdded) {
                logger.info("Successfully added course");
                return true;
            }
        } catch (StudentAlreadyRegisteredForSemesterException e) {
            logger.info("Error: " + e.getMessage());
            throw e;
        } catch (MaxCoursesAlreadySelectedException e) {
            logger.info("Error: " + e.getMessage());
            throw e;
        } catch (CourseAlreadyRegisteredException e) {
            logger.info("Error: " + e.getMessage());
            throw e;
        } catch (SeatNotAvailableException e) {
            logger.info("Error: " + e.getMessage());
            throw e;
        }

        return false;
    }


    /**
     * method for dropping course
     *
     * @param courseId unique Id to represent a course
     * @return returns true if the course is dropped successfully
     */
    @Override
    public boolean dropCourse(int courseId) throws StudentAlreadyRegisteredForSemesterException, CourseNotRegisteredByStudentException, SQLException {
        try {
            boolean isRegistered = semesterRegistrationDaoInterface.getRegistrationStatus(StudentOperation.student.getStudentId());
            if (isRegistered) {
                throw new StudentAlreadyRegisteredForSemesterException();
            }
            boolean isCourseRegistered = semesterRegistrationDaoInterface.isCourseAlreadyRegistered(courseId, StudentOperation.student.getStudentId());
            if (!isCourseRegistered) throw new CourseNotRegisteredByStudentException(courseId);

            boolean courseDropped = semesterRegistrationDaoInterface.dropCourse(courseId, StudentOperation.student.getStudentId());
            if (courseDropped) {
                return true;
            }
        } catch (StudentAlreadyRegisteredForSemesterException e) {
            logger.info("Error: " + e.getMessage());
            throw e;
        } catch (CourseNotRegisteredByStudentException e) {
            logger.info("Error: " + e.getMessage());
            throw e;
        }
        return false;
    }


    /**
     * method for getting registered courses
     *
     * @return list of registered courses
     */
    @Override
    public List<OptedCourse> getRegisteredCourses() throws NoRegisteredCourseException {
        try {
            List<OptedCourse> courses = semesterRegistrationDaoInterface.getRegisteredCourses(StudentOperation.student.getStudentId());
            if (courses == null || courses.size() == 0) {
                throw new NoRegisteredCourseException();
            }
            return courses;
        } catch (NoRegisteredCourseException ex) {
            logger.info("Error: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * method for getting selected courses
     *
     * @return list of selected courses
     */
    @Override
    public List<OptedCourse> getSelectedCourses() throws NoRegisteredCourseException {
        try {
            List<OptedCourse> courses = semesterRegistrationDaoInterface.getSelectedCourses(StudentOperation.student.getStudentId());
            if (courses == null || courses.size() == 0) {
                throw new NoRegisteredCourseException();
            }
            return courses;
        } catch (NoRegisteredCourseException ex) {
            logger.info("Error: " + ex.getMessage());
            throw ex;
        }
    }


    /**
     * method for submitting course choices
     *
     * @return returns true if course choices are submitted successfully
     */
    @Override
    public boolean submitCourseChoices() throws RequiredCoursesSelectedException, NoRegisteredCourseException, StudentAlreadyRegisteredForSemesterException, SQLException {
        try {
            boolean isRegistered = semesterRegistrationDaoInterface.getRegistrationStatus(StudentOperation.student.getStudentId());

            if (isRegistered) {
                throw new StudentAlreadyRegisteredForSemesterException();
            }

            List<OptedCourse> courses = semesterRegistrationDaoInterface.getSelectedCourses(StudentOperation.student.getStudentId());
            List<OptedCourse> primaryCourse = courses.stream().filter(course -> course.getIsPrimary()).collect(Collectors.toList());
            List<OptedCourse> secondaryCourse = courses.stream().filter(course -> !course.getIsPrimary()).collect(Collectors.toList());

            if (courses == null || courses.size() == 0) {
                throw new NoRegisteredCourseException();
            }

            logger.info(
                    "You have selected "
                            + primaryCourse.size()
                            + " primary courses and "
                            + secondaryCourse.size()
                            + "secondary courses."
            );
            if (courses.size() < 6) {
                throw new RequiredCoursesSelectedException();
            }

            int courseCount = 0;
            double courseFee = 0;

            for (OptedCourse course : primaryCourse) {
                boolean isAvailable = semesterRegistrationDaoInterface.checkAvailability(course.getCourseId());
                if (isAvailable) {
                    boolean alloted = semesterRegistrationDaoInterface.allotCourse(course.getCourseId(), StudentOperation.student.getStudentId());
                    if (alloted) {
                        semesterRegistrationDaoInterface.updateStudentCount(course.getCourseId());
                        courseCount += 1;
                        courseFee += course.getCourseFee();
                    }
                }
            }

            for (OptedCourse course : secondaryCourse) {
                if (courseCount < 4) {
                    boolean isAvailable = semesterRegistrationDaoInterface.checkAvailability(course.getCourseId());
                    if (isAvailable) {
                        boolean alloted = semesterRegistrationDaoInterface.allotCourse(course.getCourseId(), StudentOperation.student.getStudentId());
                        if (alloted) {
                            semesterRegistrationDaoInterface.updateStudentCount(course.getCourseId());
                            courseCount += 1;
                            courseFee += course.getCourseFee();

                        }
                    }
                }
            }

            boolean submitRegistration = semesterRegistrationDaoInterface.submitRegistration(courseFee, StudentOperation.student.getStudentId());

            String notificationContent = "You have Successfully Registered for the Semester. Please Pay fee $" + courseFee + " ASAP";
            notificationOperation.sendNotification(notificationContent);

            return submitRegistration;
        } catch (StudentAlreadyRegisteredForSemesterException | NoRegisteredCourseException | RequiredCoursesSelectedException e) {
            logger.info("Error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * method for getting the pending fee.
     *
     * @return pending Fee
     */
    @Override
    public double getPendingFee() {
        return semesterRegistrationDaoInterface.getPendingFee(StudentOperation.student.getStudentId());
    }

    /**
     * method for paying fee
     *
     * @param amount Amount to be paid
     * @return returns true if payment is successful
     */
    @Override
    public boolean payFee(double amount) {
        boolean feePayment = semesterRegistrationDaoInterface.payFee(amount, StudentOperation.student.getStudentId());
        if (feePayment) {
            String notificationContent = "Fee Payment Complete Welcome to the CRS.";
            notificationOperation.sendNotification(notificationContent);
        }
        return feePayment;
    }
}
