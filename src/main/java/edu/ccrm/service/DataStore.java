package edu.ccrm.service;

import edu.ccrm.domain.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.UUID;

public class DataStore {
    private static final DataStore INSTANCE = new DataStore();

    private final Map<String, Student> students = new ConcurrentHashMap<>();
    private final Map<String, Course> courses = new ConcurrentHashMap<>();
    private final List<Enrollment> enrollments = Collections.synchronizedList(new ArrayList<>());

    private DataStore(){}

    public static DataStore get(){ return INSTANCE; }

    // Student operations
    public Student addStudent(String regNo, String name, String email){
        String id = UUID.randomUUID().toString();
        Student s = new Student(id, regNo, name, email);
        students.put(id, s);
        return s;
    }
    public Optional<Student> findStudentById(String id){ return Optional.ofNullable(students.get(id)); }
    public List<Student> listStudents(){ return new ArrayList<>(students.values()); }

    // Course
    public Course addCourse(String code, String title, int credits, Semester sem){
        Course c = new Course.Builder().code(code).title(title).credits(credits).semester(sem).build();
        courses.put(code, c);
        return c;
    }
    public Optional<Course> findCourseByCode(String code){ return Optional.ofNullable(courses.get(code)); }
    public List<Course> listCourses(){ return new ArrayList<>(courses.values()); }

    // Enrollment
    public Enrollment enroll(String studentId, String courseCode, Semester sem) throws Exception {
        synchronized(enrollments){
            boolean exists = enrollments.stream().anyMatch(e -> e.getStudentId().equals(studentId) && e.getCourseCode().equals(courseCode));
            if(exists) throw new Exception("Duplicate enrollment");
            Enrollment e = new Enrollment(studentId, courseCode, sem);
            enrollments.add(e);
            // update student record
            Student s = students.get(studentId);
            if(s!=null) s.enroll(courseCode);
            return e;
        }
    }

    public List<Enrollment> listEnrollmentsForStudent(String studentId){
        synchronized(enrollments){
            return enrollments.stream().filter(e -> e.getStudentId().equals(studentId)).collect(Collectors.toList());
        }
    }

    public List<Enrollment> listAllEnrollments(){
        synchronized(enrollments){
            return new ArrayList<>(enrollments);
        }
    }
}
