package com.flipkart.restController;

import com.flipkart.bean.Course;
import com.flipkart.bean.Professor;
import com.flipkart.bean.Student;
import com.flipkart.business.*;
import com.flipkart.constants.Roles;
import com.flipkart.exceptions.RESTResponseException;
import org.apache.log4j.Logger;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


/**
 * @author JEDI-07
 * Admin Rest API
 */
@Path("/admin")
public class AdminRestAPI {
    Logger logger = Logger.getLogger(AdminRestAPI.class);
    UserInterface userInterface = new UserOperation();
    AdminInterface adminInterface = new AdminOperation();
    CourseInterface courseInterface = new CourseOperation();

    /**
     * Endpoint for getting all the available courses
     *
     * @return list of courses
     */
    @GET
    @Path("/courses")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Course> getCourses() {
        if (UserOperation.user == null || !UserOperation.user.getRole().equals(Roles.Admin)) {
            logger.info("Error: User not authenticated.");
            return null;
        }
        return courseInterface.getCourses();
    }

    /**
     * Endpoint for adding a new course
     *
     * @param course course
     * @return isCourseAdded
     */
    @POST
    @Path("/course")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCourse(@NotNull Course course) {
        if (UserOperation.user == null) {
            logger.info("Error: User not authenticated.");
            return Response
                    .status(401)
                    .entity("Login Required.")
                    .build();
        }
        if (!UserOperation.user.getRole().equals(Roles.Admin)) {
            logger.info("Error: Access Denied");
            return Response
                    .status(403)
                    .entity("Access Denied")
                    .build();
        }

        boolean isCourseAdded = adminInterface.addCourse(
                course.getCourseName(),
                course.getCourseDescription(),
                course.getCourseFee()
        );
        if (isCourseAdded) {
            return Response
                    .status(200)
                    .entity("Course Added Successfully.")
                    .build();
        }
        return Response
                .status(400)
                .entity("Course not added.")
                .build();
    }

    /**
     * Endpoint for adding a new Professor
     *
     * @param professor professor
     * @return isProfessorAdded
     */
    @POST
    @Path("/professor")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addProfessor(@NotNull Professor professor) {
        if (UserOperation.user == null) {
            logger.info("Error: User not authenticated.");
            return Response
                    .status(401)
                    .entity("Login Required.")
                    .build();
        }
        if (!UserOperation.user.getRole().equals(Roles.Admin)) {
            logger.info("Error: Access Denied");
            return Response
                    .status(403)
                    .entity("Access Denied")
                    .build();
        }
        try {
            boolean isProfessorAdded = adminInterface.addProfessor(
                    professor.getUserName(),
                    professor.getUserEmailId(),
                    professor.getUserPassword(),
                    professor.getPhoneNo(),
                    professor.getDepartment(),
                    professor.getDesignation()
            );
            if (isProfessorAdded) {
                return Response
                        .status(201)
                        .entity("Professor Added Successfully.")
                        .build();
            }
            return Response
                    .status(204)
                    .entity("Professor cannot be added.")
                    .build();
        } catch (Exception e) {
            throw new RESTResponseException("Error: " + e.getMessage(), 400);
        }
    }

    /**
     * Endpoint for deleting a particular course
     *
     * @param course course
     * @return isCourseDeleted
     */
    @DELETE
    @Path("/course")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeCourse(@NotNull Course course) {
        if (UserOperation.user == null) {
            logger.info("Error: User not authenticated.");
            return Response
                    .status(401)
                    .entity("Login Required.")
                    .build();
        }
        if (!UserOperation.user.getRole().equals(Roles.Admin)) {
            logger.info("Error: Access Denied");
            return Response
                    .status(403)
                    .entity("Access Denied")
                    .build();
        }
        try {
            boolean isCourseRemoved = adminInterface.removeCourse(course.getCourseId());
            if (isCourseRemoved) {
                return Response
                        .status(204)
                        .entity("Course Removed Successfully.")
                        .build();
            }
            return Response
                    .status(400)
                    .entity("Course cannot be removed.")
                    .build();
        } catch (Exception e) {
            throw new RESTResponseException(e.getMessage(), 400);
        }
    }

    /**
     * Endpoint for fetching all the professors
     *
     * @return list of professors
     */
    @GET
    @Path("/professors")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Professor> getProfessors() {
        if (UserOperation.user == null || !UserOperation.user.getRole().equals(Roles.Admin)) {
            logger.info("Error: User not authenticated.");
            return null;
        }
        return adminInterface.getProfessors();
    }

    /**
     * Endpoint for fetching admission requests
     *
     * @return list of students
     */
    @GET
    @Path("/admissions")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Student> getAdmissionRequests() {
        if (UserOperation.user == null || !UserOperation.user.getRole().equals(Roles.Admin)) {
            logger.info("Error: User not authenticated.");
            return null;
        }
        return adminInterface.getAdmissionRequests();
    }

    /**
     * Endpoint for approving admission request
     *
     * @param student student
     * @return isRequestApproved
     */
    @POST
    @Path("/approve")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response approveAdmissionRequest(@NotNull Student student) {
        if (UserOperation.user == null) {
            logger.info("Error: User not authenticated.");
            return Response
                    .status(401)
                    .entity("Login Required.")
                    .build();
        }
        if (!UserOperation.user.getRole().equals(Roles.Admin)) {
            logger.info("Error: Access Denied");
            return Response
                    .status(403)
                    .entity("Access Denied")
                    .build();
        }
        try {
            boolean isApproved = adminInterface.approveStudentRequest(student.getStudentId());
            if (isApproved) {
                return Response
                        .status(200)
                        .entity("Admission Request for student with student ID: " + student.getStudentId() + " approved successfully.")
                        .build();
            }
            return Response
                    .status(400)
                    .entity("Admission Request for student with student ID: " + student.getStudentId() + " cannot be approved.")
                    .build();
        } catch (Exception e) {
            throw new RESTResponseException(e.getMessage(), 400);
        }

    }
}
