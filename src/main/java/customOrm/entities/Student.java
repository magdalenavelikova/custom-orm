package customOrm.entities;

import ormFramework.annotations.Column;
import ormFramework.annotations.Entity;
import ormFramework.annotations.Id;

@Entity(name = "students")
public class Student {

  @Id()
     private int id;
    @Column(name = "student_name", columnDefinition = "VARCHAR(50) NOT NULL")
    private String name;

}
