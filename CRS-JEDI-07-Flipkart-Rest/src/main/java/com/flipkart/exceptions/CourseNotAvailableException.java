package com.flipkart.exceptions;

public class CourseNotAvailableException extends Exception {
    private int courseId;

    public CourseNotAvailableException(int courseId) {
        this.courseId = courseId;
    }

    @Override
    public String getMessage() {
        return "Course with course Id: " + courseId + " cannot be selected.";
    }
}