package com.flipkart.restController;

import com.flipkart.bean.Course;
import com.flipkart.bean.Grade;
import com.flipkart.bean.Student;
import com.flipkart.bean.User;
import com.flipkart.business.ProfessorInterface;
import com.flipkart.business.ProfessorOperation;
import com.flipkart.business.UserInterface;
import com.flipkart.business.UserOperation;
import com.flipkart.constants.Roles;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author JEDI-07
 * Professor Rest API
 */
@Path("/professor")
public class ProfessorRestAPI {
    UserInterface userInterface = new UserOperation();
    ProfessorInterface professorInterface = new ProfessorOperation();

    /**
     * Endpoint for fetching courses
     *
     * @param selected selected courses
     * @return list of courses
     */
    @GET
    @Path("/courses")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Course> getCourses(@QueryParam("selected") int selected) {
        if (UserOperation.user == null || !UserOperation.user.getRole().equals(Roles.Professor)) {
            return null;
        }
        if (selected == 1)
            return professorInterface.getSelectedCourses();
        return professorInterface.getAvailableCourses();
    }

    /**
     * Endpoint for selecting a course
     *
     * @param course course
     * @return isCourseAdded
     */
    @POST
    @Path("/course")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response selectCourse(@NotNull Course course) {
        if (UserOperation.user == null) {
            return Response
                    .status(401)
                    .entity("Login Required.")
                    .build();
        }
        if (!UserOperation.user.getRole().equals(Roles.Professor)) {
            return Response
                    .status(403)
                    .entity("Access Denied")
                    .build();
        }

        boolean isCourseSelected = professorInterface.selectCourse(course.getCourseId());
        if (isCourseSelected) {
            return Response
                    .status(201)
                    .entity("Course with course id: " + course.getCourseId() + " selected successfully.")
                    .build();
        }
        return Response
                .status(400)
                .entity("Course with course id: " + course.getCourseId() + " cannot be selected.")
                .build();
    }

    /**
     * Endpoint for dropping a course
     *
     * @param course course
     * @return isCourseDropped
     */
    @POST
    @Path("/dropcourse")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response dropCourse(@NotNull Course course) {
        if (UserOperation.user == null) {
            return Response
                    .status(401)
                    .entity("Login Required.")
                    .build();
        }
        if (!UserOperation.user.getRole().equals(Roles.Professor)) {
            return Response
                    .status(403)
                    .entity("Access Denied")
                    .build();
        }
        boolean isCourseDeselected = professorInterface.deselectCourse(course.getCourseId());
        if (isCourseDeselected) {
            return Response
                    .status(200)
                    .entity("Course with course id: " + course.getCourseId() + " dropped successfully.")
                    .build();
        }
        return Response
                .status(400)
                .entity("Course with course id: " + course.getCourseId() + " cannot be dropped.")
                .build();
    }

    /**
     * Endpoint for getting list of enrolled students
     *
     * @param courseId courseId
     * @return list of students
     */
    @GET
    @Path("/enrolledStudents")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Student> getEnrolledStudents(@QueryParam("courseId") int courseId) {
        if (UserOperation.user == null || !UserOperation.user.getRole().equals(Roles.Professor)) {
            return null;
        }
        return professorInterface.getEnrolledStudents(courseId);
    }

    /**
     * Endpoint for adding grade for a student
     *
     * @param grade grade
     * @return isGradeAdded
     */
    @POST
    @Path("/gradeStudent")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response gradeStudent(@NotNull Grade grade) {
        if (UserOperation.user == null) {
            return Response
                    .status(401)
                    .entity("Login Required.")
                    .build();
        }
        if (!UserOperation.user.getRole().equals(Roles.Professor)) {
            return Response
                    .status(403)
                    .entity("Access Denied")
                    .build();
        }

        boolean graded = professorInterface.addGrade(grade.getStudentId(), grade.getCourseId(), grade.getGpa());
        if (graded) {
            return Response
                    .status(201)
                    .entity("Grade for student with studentId " + grade.getStudentId() + " added successfully.")
                    .build();
        }
        return Response
                .status(400)
                .entity("Grade for student with studentId " + grade.getStudentId() + " cannot be added.")
                .build();
    }


    /**
     * Endpoint for updating password
     *
     * @param user User
     * @return isPasswordUpdated
     */
    @PUT
    @Path("/updatepassword")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePassword(@NotNull User user) {
        if (UserOperation.user == null) {
            return Response
                    .status(401)
                    .entity("Login Required.")
                    .build();
        }
        if (!UserOperation.user.getRole().equals(Roles.Professor)) {
            return Response
                    .status(403)
                    .entity("Access Denied")
                    .build();
        }

        boolean passwordUpdated = userInterface.updateUserPassword(user.getUserPassword());
        if (passwordUpdated) {
            return Response
                    .status(200)
                    .entity("Password Updated Successfully.")
                    .build();
        }
        return Response
                .status(400)
                .entity("Something went wrong.")
                .build();
    }
}
