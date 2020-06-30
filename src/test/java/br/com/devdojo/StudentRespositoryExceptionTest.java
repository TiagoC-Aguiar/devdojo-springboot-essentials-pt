package br.com.devdojo;

import br.com.devdojo.model.Student;
import br.com.devdojo.repository.StudentRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class StudentRespositoryExceptionTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void shouldThrowWhenNameIsEmpty() {
//        thrown.expect(ConstraintViolationException.class);
//        thrown.expectMessage("O campo nome do estudante é obrigatório");
//        Exception exception = Assert.assertThrows(NullPointerException.class, () -> {
//            studentRepository.save(new Student("", "email@email.com.br"));
//        });
        Exception exception = Assert.assertThrows(NullPointerException.class, () ->
                studentRepository.save(new Student("", "email@email.com")));
//        Assertions.assertTrue(exception.getMessage().contains("O campo name não pode ficar vazio"));
    }

    @Test
    public void shouldThrowWhenEmailIsEmpty() {
        Exception exception = Assert.assertThrows(NullPointerException.class, () ->
                studentRepository.save(new Student("Teste", " ")));
//        Assertions.assertTrue(exception.getMessage().contains("O campo name não pode ficar vazio"));
    }

    @Test
    public void shouldThrowWhenEmailIsNotValid() {
        Exception exception = Assert.assertThrows(NullPointerException.class, () ->
                studentRepository.save(new Student("Teste", "teste")));
    }



}
