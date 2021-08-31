package com.flipkart.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RESTResponseException extends WebApplicationException {
    public RESTResponseException(String message, int statusCode) {
        super(Response.status(statusCode).entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
