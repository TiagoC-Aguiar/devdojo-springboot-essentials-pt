package br.com.devdojo;

import br.com.devdojo.model.Student;
import br.com.devdojo.repository.StudentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
//@ExtendWith(SpringExtension.class)
@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

//    @Rule
//    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldPersistData() {
        Student student = new Student("William", "william@devdojo.com.br");
        this.studentRepository.save(student);
        assertThat(student.getId()).isNotNull();
        assertThat(student.getName()).isEqualTo("William");
        assertThat(student.getEmail()).isEqualTo("william@devdojo.com.br");
    }

    @Test
    public void shouldRemoveData() {
        Student student = new Student("William", "william@devdojo.com.br");
        this.studentRepository.save(student);
        studentRepository.delete(student);
        assertThat(studentRepository.findById(student.getId())).isEmpty();
    }

    @Test
    public void shouldChangeAndPersistData() {
        Student student = new Student("William", "william@devdojo.com.br");
        this.studentRepository.save(student);
        student.setName("Willianm22");
        student.setEmail("email@email.com.br");
        this.studentRepository.save(student);
        assertThat(student.getName()).isEqualTo("Willianm22");
        assertThat(student.getEmail()).isEqualTo("email@email.com.br");
    }

    @Test
    public void shouldFindStudentByName() {
        Student student = new Student("Lucas", "lucas@email.com.br");
        Student student2 = new Student("lucas", "lucas2@email.com.br");
        this.studentRepository.save(student);
        this.studentRepository.save(student2);
        List<Student> studentList = studentRepository.findByNameIgnoreCaseContaining("lucas");
        assertThat(studentList.size()).isEqualTo(2);
    }

}
