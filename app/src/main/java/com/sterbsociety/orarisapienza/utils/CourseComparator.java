package com.sterbsociety.orarisapienza.utils;

import com.sterbsociety.orarisapienza.models.Lesson;

import java.util.Comparator;

/**
 * A collection of {@link Comparator}s for {@link Lesson} objects.
 *
 * @author ISchwarz
 */
public final class CourseComparator {

    private CourseComparator() {
        //no instance
    }

    public static Comparator<Lesson> getCourseTimeComparator() {
        return new CourseTimeComparator();
    }

    public static Comparator<Lesson> getCourseNameComparator() {
        return new CourseNameComparator();
    }

    public static Comparator<Lesson> getCourseClassComparator() {
        return new CourseClassComparator();
    }

    private static class CourseTimeComparator implements Comparator<Lesson> {

        @Override
        public int compare(final Lesson lesson1, final Lesson lesson2) {
            return lesson1.getStartLessonHour() - lesson2.getStartLessonHour();
        }
    }

    private static class CourseNameComparator implements Comparator<Lesson> {

        @Override
        public int compare(Lesson lesson1, Lesson lesson2) {
            return lesson1.getCourseName().compareTo(lesson2.getCourseName());
        }
    }

    private static class CourseClassComparator implements Comparator<Lesson> {

        @Override
        public int compare(Lesson lesson1, Lesson lesson2) {
            return lesson1.getClassRoom().compareTo(lesson2.getClassRoom());
        }
    }

}