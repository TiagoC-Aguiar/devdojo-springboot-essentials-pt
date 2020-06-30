package br.com.devdojo.model;

import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Entity
public class Student extends AbstractEntity {

    @NotEmpty(message = "O campo email não pode ficar em branco")
    @Email
    private String email;

    public Student(String name, String email) {
        super(name);
        this.email = email;
    }

    public Student(Long id, String name, String email) {
        super(name);
        this.id = id;
        this.email = email;
    }

    public Student() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + super.getId() + "' " +
                "nome='" + super.getName() + "' " +
                "email='" + email + '\'' +
                "}";
    }
}
