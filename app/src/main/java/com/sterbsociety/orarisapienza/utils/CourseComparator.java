package com.sterbsociety.orarisapienza.utils;

import com.sterbsociety.orarisapienza.model.Course;

import java.util.Comparator;

/**
 * A collection of {@link Comparator}s for {@link Course} objects.
 *
 * @author ISchwarz
 */
public final class CourseComparator {

    private CourseComparator() {
        //no instance
    }

    public static Comparator<Course> getCourseTimeComparator() {
        return new CourseTimeComparator();
    }

    public static Comparator<Course> getCourseNameComparator() {
        return new CourseNameComparator();
    }

    public static Comparator<Course> getCourseClassComparator() {
        return new CourseClassComparator();
    }

    private static class CourseTimeComparator implements Comparator<Course> {

        @Override
        public int compare(final Course course1, final Course course2) {
            return course1.getStartLessonHour() - course2.getStartLessonHour();
        }
    }

    private static class CourseNameComparator implements Comparator<Course> {

        @Override
        public int compare(Course course1, Course course2) {
            return course1.getCourseName().compareTo(course2.getCourseName());
        }
    }

    private static class CourseClassComparator implements Comparator<Course> {

        @Override
        public int compare(Course course1, Course course2) {
            return course1.getClassRoom().compareTo(course2.getClassRoom());
        }
    }

}