package edu.ccrm.service;

import edu.ccrm.domain.Student;
import java.util.List;
import java.util.Optional;

public class StudentService {
    private final DataStore ds = DataStore.get();

    public Student createStudent(String regNo, String name, String email){
        return ds.addStudent(regNo, name, email);
    }

    public Optional<Student> findById(String id){ return ds.findStudentById(id); }
    public List<Student> listAll(){ return ds.listStudents(); }
}
