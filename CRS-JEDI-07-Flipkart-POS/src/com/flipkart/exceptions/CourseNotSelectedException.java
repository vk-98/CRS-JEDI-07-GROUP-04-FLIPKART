package com.flipkart.exceptions;

/**
 * @author JEDI-07
 * CourseNotSelectedException
 */
public class CourseNotSelectedException extends Exception {
    private int courseId;

    public CourseNotSelectedException(int courseId) {
        this.courseId = courseId;
    }

    @Override
    public String getMessage() {
        return "Course with courseId " + courseId + " is not selected.";
    }
}

