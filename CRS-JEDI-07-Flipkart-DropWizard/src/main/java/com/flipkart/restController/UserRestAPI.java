package com.flipkart.restController;

import com.flipkart.bean.Student;
import com.flipkart.bean.User;
import com.flipkart.business.*;
import com.flipkart.constants.Roles;
import com.flipkart.exceptions.RESTResponseException;
import org.apache.log4j.Logger;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author JEDI-07
 * User Rest API
 */
@Path("/user")
public class UserRestAPI {
    UserInterface userInterface = new UserOperation();
    StudentInterface studentInterface = new StudentOperation();
    ProfessorInterface professorInterface = new ProfessorOperation();
    private static Logger logger = Logger.getLogger(UserRestAPI.class);


    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(User user) {
        boolean isValidated = userInterface.validateUser(user.getUserEmailId(), user.getUserPassword());
        if (isValidated) {
            if (Roles.Student.equals(UserOperation.user.getRole())) {
                studentInterface.getStudentByEmailId();
                if (!StudentOperation.student.isApproved()) {
                    return Response.status(200).entity("Your admission request is still pending..., login later").build();
                }
            }
            if (Roles.Professor.equals(UserOperation.user.getRole())) {
                professorInterface.getProfessor();
            }
            return Response.status(200).entity("User Logged In Successfully").build();
        }
        return Response.status(200).entity("Invalid EmailId or Password, Try Again").build();
    }

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(Student student) {
        try {
            Student st = studentInterface.register(student.getUserName(), student.getUserEmailId(), student.getUserPassword(), student.getPhoneNo());
            if (st == null) {
                return Response.status(200).entity("Something Went wrong try again later").build();
            }
            return Response
                    .status(201)
                    .entity(student.getUserName() + " you are successfully registered, please wait for Admin's Approval")
                    .build();
        } catch (Exception e) {
            throw new RESTResponseException("Error: " + e.getMessage(), 400);
        }

    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response logout() {
        boolean isLoggedOut = userInterface.logout();
        return Response.status(200).entity("User Logged out Successfully").build();
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
            logger.info("Error: User not authenticated.");
            return Response
                    .status(401)
                    .entity("Login Required.")
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
