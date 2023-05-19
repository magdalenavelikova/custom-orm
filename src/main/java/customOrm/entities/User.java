package customOrm.entities;

import ormFramework.annotations.Column;
import ormFramework.annotations.Entity;
import ormFramework.annotations.Id;

import java.time.LocalDate;

@Entity(name = "users")
public class User {

    @Id()
    private long id;

    @Column(name = "user_name", columnDefinition = "VARCHAR(50) NOT NULL")
    private String username;

    @Column(name = "age" ,columnDefinition ="int NOT NULL")
    private int age;
    @Column(name = "registration_date",columnDefinition ="DATE NOT NULL")
    private LocalDate registration;

    public User() {

    }

    public User(String username, int age) {
        this.username = username;
        this.age = age;
        this.registration = LocalDate.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public LocalDate getRegistration() {
        return registration;
    }

    public void setRegistration(LocalDate registration) {
        this.registration = registration;
    }

    @Override
    public String toString() {
        return (this.getId() + " " + this.getUsername()+" "+this.getAge()+" "+ this.getRegistration());
    }
}
