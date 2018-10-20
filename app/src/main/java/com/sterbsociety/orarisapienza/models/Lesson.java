package com.sterbsociety.orarisapienza.models;

public class Lesson {

    private Classroom classroom;
    private int courseId;
    private String courseName;
    private String day;
    private String endLesson;
    private String professor;
    private String startLesson;
    private String subjectName;
    private String year;
    private String channel;

    public Lesson(Classroom classroom, int courseId, String courseName, String day, String endLesson, String professor, String startLesson, String subjectName, String year, String channel) {
        this.classroom = classroom;
        this.courseId = courseId;
        this.courseName = courseName;
        this.day = day;
        this.endLesson = endLesson;
        this.professor = professor;
        this.startLesson = startLesson;
        this.subjectName = subjectName;
        this.year = year;
        this.channel = channel;
    }

    public Lesson(Classroom classroom, String courseId, String courseName, String day, String endLesson, String professor, String startLesson, String subjectName, String year, String channel) {
        this(classroom, Integer.parseInt(courseId), courseName, day, endLesson, professor, startLesson, subjectName, year, channel);
    }

    public Lesson() {
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getStartLesson() {
        return startLesson;
    }

    public void setStartLesson(String startLesson) {
        this.startLesson = startLesson;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getEndLesson() {
        return endLesson;
    }

    public void setEndLesson(String endLesson) {
        this.endLesson = endLesson;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getClassroomName() {
        if (classroom != null) {
            return classroom.getName();
        }
        return "Not available";
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classRoom) {
        this.classroom = classRoom;
    }

    public int getStartLessonHour() {
        try {
            return Integer.parseInt(startLesson.split(":")[0]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 24;
    }

    public String getChannel() {
        return channel;
    }

    public String getYear() {
        return year;
    }
}
