package com.flipkart.restController;


import com.flipkart.bean.*;
import com.flipkart.business.*;
import com.flipkart.constants.Roles;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author JEDI-07
 * Student Rest API
 */
@Path("/student")
public class StudentRestAPI {

    CourseInterface courseInterface = new CourseOperation();
    StudentInterface studentInterface = new StudentOperation();
    NotificationInterface notificationInterface = new NotificationOperation();
    UserInterface userInterface = new UserOperation();
    SemesterRegistrationInterface semesterRegistrationInterface = new SemesterRegistrationOperation();

    /**
     * Endpoint for fetching all the courses
     *
     * @return list of courses
     */
    @GET
    @Path("/courses")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Course> getCourses() {
        if (UserOperation.user == null || !UserOperation.user.getRole().equals(Roles.Student)) {
            return null;
        }
        return courseInterface.getCourses();
    }

    /**
     * Endpoint for getting the gradecard
     *
     * @return GradeCard
     */
    @GET
    @Path("/gradecard")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GradeCard getGradeCard() {
        if (UserOperation.user == null || !UserOperation.user.getRole().equals(Roles.Student)) {
            return null;
        }
        return studentInterface.getGradeCard();
    }


    /**
     * Endpoint for fetching all the notifications
     *
     * @return list of notifications
     */
    @GET
    @Path("/notifications")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Notification> showNotifications() {
        if (UserOperation.user == null || !UserOperation.user.getRole().equals(Roles.Student)) {
            return null;
        }
        return notificationInterface.getNotifications();
    }

    /**
     * Endpoint for paying any fee required
     *
     * @return isPayementDone
     */
    @GET
    @Path("/payfee")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response payfee() {
        if (UserOperation.user == null) {
            return Response
                    .status(401)
                    .entity("Login Required.")
                    .build();
        }
        if (!UserOperation.user.getRole().equals(Roles.Student) || !StudentOperation.student.isApproved()) {
            return Response
                    .status(403)
                    .entity("Access Denied")
                    .build();
        }

        double feePending = semesterRegistrationInterface.getPendingFee();
        if (feePending != 0) {
            boolean feePayed = semesterRegistrationInterface.payFee(feePending);
            if (feePayed) {
                return Response.status(200).entity("Fee payment Successful.").build();
            }
            return Response.status(200).entity("Fee payment Failed").build();
        }
        return Response.status(200).entity("No Pending Fee payment").build();
    }


    /**
     * Endpoing for adding course
     *
     * @param optedCourse optedCourse
     * @return isCourseAdded
     */
    @POST
    @Path("/semester/addcourse")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addcourse(OptedCourse optedCourse) {
        if (UserOperation.user == null) {
            return Response
                    .status(401)
                    .entity("Login Required.")
                    .build();
        }
        if (!UserOperation.user.getRole().equals(Roles.Student) || !StudentOperation.student.isApproved()) {
            return Response
                    .status(403)
                    .entity("Access Denied")
                    .build();
        }

        boolean isCourseAdded = semesterRegistrationInterface.addCourse(optedCourse.getCourseId(), optedCourse.getIsPrimary() ? 1 : 0);
        if (isCourseAdded) {
            return Response.status(201).entity("Course with course id: " + optedCourse.getCourseId() + " added successfully").build();
        }
        return Response.status(200).entity("Course with course id: " + optedCourse.getCourseId() + " cannot be added").build();
    }


    /**
     * Endpoint for dropping course
     *
     * @param optedCourse optedcourse
     * @return isCourseDropped
     */
    @POST
    @Path("/semester/dropcourse")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response dropcourse(OptedCourse optedCourse) {
        if (UserOperation.user == null) {
            return Response
                    .status(401)
                    .entity("Login Required.")
                    .build();
        }
        if (!UserOperation.user.getRole().equals(Roles.Student) || !StudentOperation.student.isApproved()) {
            return Response
                    .status(403)
                    .entity("Access Denied")
                    .build();
        }

        boolean isCourseDropped = semesterRegistrationInterface.dropCourse(optedCourse.getCourseId());
        if (isCourseDropped) {
            return Response
                    .status(201)
                    .entity("Course with course id: " + optedCourse.getCourseId() + " dropped successfully")
                    .build();
        }
        return Response
                .status(200)
                .entity("Course with course id: " + optedCourse.getCourseId() + " cannot be dropped")
                .build();
    }

    /**
     * Endpoing for fetching all the selected courses
     *
     * @param selected selectec course only
     * @return list of optedcourse
     */
    @GET
    @Path("/semester/courses")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<OptedCourse> getCourses(@QueryParam("selected") int selected) {
        if (UserOperation.user == null || !UserOperation.user.getRole().equals(Roles.Student)) {
            return null;
        }
        if (selected == 1) {
            return semesterRegistrationInterface.getSelectedCourses();
        }
        return semesterRegistrationInterface.getRegisteredCourses();
    }

    /**
     * Enpoint for submitting the selected choices
     *
     * @return isSemesterSubmitted
     */
    @GET
    @Path("/semester/submit")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response submit() {
        if (UserOperation.user == null) {
            return Response
                    .status(401)
                    .entity("Login Required.")
                    .build();
        }
        if (!UserOperation.user.getRole().equals(Roles.Student) || !StudentOperation.student.isApproved()) {
            return Response
                    .status(403)
                    .entity("Access Denied")
                    .build();
        }

        boolean isSubmitted = semesterRegistrationInterface.submitCourseChoices();
        if (isSubmitted) {
            return Response
                    .status(200)
                    .entity("Course choices for semester submitted successfully.")
                    .build();
        }
        return Response
                .status(200)
                .entity("Something went wrong.")
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
        if (!UserOperation.user.getRole().equals(Roles.Student) || !StudentOperation.student.isApproved()) {
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
