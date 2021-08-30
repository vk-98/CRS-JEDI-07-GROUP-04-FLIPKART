package com.flipkart.restController;

import com.flipkart.bean.Student;
import com.flipkart.bean.User;
import com.flipkart.business.*;
import com.flipkart.constants.Roles;
import org.apache.log4j.Logger;

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
    private static Logger logger = Logger.getLogger(UserRestAPI.class);


    @POST
    @Path("/login")
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
            return Response.status(200).entity("User Logged In Successfully").build();
        }
        return Response.status(200).entity("Invalid EmailId or Password, Try Again").build();
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(Student student) {
        Student st = studentInterface.register(student.getUserName(), student.getUserEmailId(), student.getUserPassword(), student.getPhoneNo());
        if (st == null) {
            return Response.status(200).entity("Something Went wrong try again later").build();
        }
        return Response
                .status(201)
                .entity(student.getUserName() + " you are successfully registered, please wait for Admin's Approval")
                .build();
    }

    @POST
    @Path("/logout")
    public Response logout() {
        boolean isLoggedOut = userInterface.logout();
        return Response.status(200).entity("User Logged out Successfully").build();
    }
}
