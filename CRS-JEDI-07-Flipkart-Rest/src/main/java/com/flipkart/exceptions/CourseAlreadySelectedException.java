package com.flipkart.exceptions;

/**
 * @author JEDI-07
 * Implementation of admin dao interface
 */
public class CourseAlreadySelectedException extends Exception {
    private int courseId;

    public CourseAlreadySelectedException(int courseId) {
        this.courseId = courseId;
    }

    @Override
    public String getMessage() {
        return "Course Already selected " + courseId;
    }
}
