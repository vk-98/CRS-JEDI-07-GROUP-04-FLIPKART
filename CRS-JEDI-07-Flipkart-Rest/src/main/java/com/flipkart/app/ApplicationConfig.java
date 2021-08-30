package com.flipkart.app;

import com.flipkart.restController.AdminRestAPI;
import com.flipkart.restController.ProfessorRestAPI;
import com.flipkart.restController.StudentRestAPI;
import com.flipkart.restController.UserRestAPI;
import org.glassfish.jersey.server.ResourceConfig;

public class ApplicationConfig extends ResourceConfig {
    public ApplicationConfig() {

        register(StudentRestAPI.class);
        register(UserRestAPI.class);
        register(ProfessorRestAPI.class);
        register(AdminRestAPI.class);

    }
}
