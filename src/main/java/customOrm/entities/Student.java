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

    public Student(){}

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
