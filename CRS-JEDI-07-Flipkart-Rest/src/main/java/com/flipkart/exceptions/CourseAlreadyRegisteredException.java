package com.flipkart.exceptions;

public class CourseAlreadyRegisteredException extends Exception {
    private int courseId;

    public CourseAlreadyRegisteredException(int courseId) {
        this.courseId = courseId;
    }

    @Override
    public String getMessage() {
        return "Student is already registered for this course id " + courseId;
    }
}