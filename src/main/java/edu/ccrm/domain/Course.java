package edu.ccrm.domain;

public class Course {
    private final String code;
    private final String title;
    private final int credits;
    private final String instructorId;
    private final Semester semester;

    private Course(Builder b){
        this.code = b.code;
        this.title = b.title;
        this.credits = b.credits;
        this.instructorId = b.instructorId;
        this.semester = b.semester;
    }

    public String getCode(){ return code; }
    public String getTitle(){ return title; }
    public int getCredits(){ return credits; }
    public String getInstructorId(){ return instructorId; }
    public Semester getSemester(){ return semester; }

    public static class Builder {
        private String code;
        private String title;
        private int credits = 3;
        private String instructorId;
        private Semester semester = Semester.FALL;

        public Builder code(String c){ this.code = c; return this; }
        public Builder title(String t){ this.title = t; return this; }
        public Builder credits(int c){ this.credits = c; return this; }
        public Builder instructorId(String id){ this.instructorId = id; return this; }
        public Builder semester(Semester s){ this.semester = s; return this; }

        public Course build(){
            if(code==null || title==null) throw new IllegalStateException("code/title required");
            return new Course(this);
        }
    }

    @Override
    public String toString(){
        return String.format("Course[%s - %s (%dcr)]", code, title, credits);
    }
}
