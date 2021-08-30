package com.flipkart.business;

import com.flipkart.bean.OptedCourse;
import com.flipkart.constants.Courses;
import com.flipkart.dao.SemesterRegistrationDaoInterface;
import com.flipkart.dao.SemesterRegistrationDaoOperation;
import com.flipkart.exceptions.*;
import org.apache.log4j.Logger;

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
    public boolean addCourse(int courseId, int isPrimary) {

        try {
            boolean isRegistered = semesterRegistrationDaoInterface.getRegistrationStatus();

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
            logger.info(e.getMessage());
        } catch (MaxCoursesAlreadySelectedException e) {
            logger.info(e.getMessage());
        } catch (CourseAlreadyRegisteredException e) {
            logger.info(e.getMessage());
        } catch (SeatNotAvailableException e) {
            logger.info(e.getMessage());
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
    public boolean dropCourse(int courseId) {
        try {
            boolean isRegistered = semesterRegistrationDaoInterface.getRegistrationStatus();
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
            logger.info(e.getMessage());
        } catch (CourseNotRegisteredByStudentException e) {
            logger.info(e.getMessage());
        }
        return false;
    }


    /**
     * method for getting registered courses
     *
     * @return list of registered courses
     */
    @Override
    public List<OptedCourse> getRegisteredCourses() {
        return semesterRegistrationDaoInterface.getRegisteredCourses(StudentOperation.student.getStudentId());
    }

    /**
     * method for getting selected courses
     *
     * @return list of selected courses
     */
    @Override
    public List<OptedCourse> getSelectedCourses() {
        return semesterRegistrationDaoInterface.getSelectedCourses(StudentOperation.student.getStudentId());
    }


    /**
     * method for submitting course choices
     *
     * @return returns true if course choices are submitted successfully
     */
    @Override
    public boolean submitCourseChoices() {

        boolean isRegistered = semesterRegistrationDaoInterface.getRegistrationStatus();

        if (isRegistered) {
            logger.info("Student already registered for the semester");
            return false;
        }

        List<OptedCourse> courses = semesterRegistrationDaoInterface.getSelectedCourses(StudentOperation.student.getStudentId());
        List<OptedCourse> primaryCourse = courses.stream().filter(course -> course.isPrimary()).collect(Collectors.toList());
        List<OptedCourse> secondaryCourse = courses.stream().filter(course -> !course.isPrimary()).collect(Collectors.toList());

        if (courses == null || courses.size() == 0) {
            logger.info("No courses Selected for registeration.");
            return false;
        }

        logger.info(
                "You have selected "
                        + primaryCourse.size()
                        + " primary courses and "
                        + secondaryCourse.size()
                        + "secondary courses."
        );
        if (courses.size() < Courses.MAX_PRIMARY_COURSES + Courses.MAX_SECONDARY_COURSES) {
            logger.info("Please Select " + Courses.MAX_PRIMARY_COURSES + " Primary and " + Courses.MAX_SECONDARY_COURSES + " Seconday Courses.");
            return false;
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
            if (courseCount < Courses.MAX_STUDENT_LIMIT) {
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
