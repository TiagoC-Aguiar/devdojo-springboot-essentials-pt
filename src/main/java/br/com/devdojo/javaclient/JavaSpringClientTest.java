package br.com.devdojo.javaclient;

import br.com.devdojo.model.Student;

import java.util.List;

public class JavaSpringClientTest {
    public static void main(String[] args) {

        JavaClientDAO dao = new JavaClientDAO();
        Student studentPost = new Student();

        studentPost.setName("Amanda");
        studentPost.setEmail("mandinha@email.com.br");

        List<Student> students = dao.listAll();
        studentPost.setId(26l);

//        System.out.println(students);

        System.out.println(dao.findById(27l));
//        System.out.println(dao.listAll());
//        System.out.println(dao.save(studentPost));

//        dao.update(studentPost);
//        dao.delete(27l);

    }

}
