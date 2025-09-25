package edu.ccrm.service;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Semester;
import java.util.List;
import java.util.Optional;

public class CourseService {
    private final DataStore ds = DataStore.get();

    public Course createCourse(String code, String title, int credits, Semester sem){
        return ds.addCourse(code, title, credits, sem);
    }

    public Optional<Course> findByCode(String code){ return ds.findCourseByCode(code); }
    public List<Course> listAll(){ return ds.listCourses(); }
}
