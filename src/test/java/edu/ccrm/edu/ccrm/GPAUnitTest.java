package edu.ccrm;

import edu.ccrm.domain.*;
import edu.ccrm.service.DataStore;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GPAUnitTest {
    @Test
    public void testGPAComputation() throws Exception {
        DataStore ds = DataStore.get();
        // clean store by creating new instance isn't possible; rely on existing state but use unique ids
        var s = ds.addStudent("TST-1","Test One","t1@example.com");
        ds.addCourse("TST100","Testing 100",3, Semester.FALL);
        ds.addCourse("TST200","Testing 200",4, Semester.FALL);
        ds.enroll(s.getId(), "TST100", Semester.FALL);
        ds.enroll(s.getId(), "TST200", Semester.FALL);
        ds.assignGrade(s.getId(), "TST100", Grade.A);
        ds.assignGrade(s.getId(), "TST200", Grade.B);
        double gpa = ds.computeGPA(s.getId());
        // GPA = (A=9*3 + B=8*4) / (3+4) = (27+32)/7 = 59/7 = 8.428...
        assertTrue(gpa > 8.42 && gpa < 8.43);
    }
}
