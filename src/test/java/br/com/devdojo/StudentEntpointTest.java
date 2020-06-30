package br.com.devdojo;

import br.com.devdojo.model.Student;
import br.com.devdojo.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class StudentEntpointTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @MockBean
    private StudentRepository studentRepository;

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class Config {

        @Bean
        public RestTemplateBuilder restTemplateBuilder() {
            return new RestTemplateBuilder().basicAuthentication("tiago", "1234");
        }
    }

    @Test
    public void listStudentsWhenUsernameAndPassIncorrect401() {
        System.out.println(port);
        restTemplate = restTemplate.withBasicAuth("1", "1");
        ResponseEntity<String> response = restTemplate.getForEntity("/v1/protected/students/", String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(401);
    }

    @Test
    public void getStudentByIdWhenUsernameAndPassIncorrect401() {
        System.out.println(port);
        restTemplate = restTemplate.withBasicAuth("1", "1");
        ResponseEntity<String> response = restTemplate.getForEntity("/v1/protected/students/11", String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(401);
    }

    // não funcionou
    @Test
    @WithMockUser(username="admin", password = "1234", roles = {"ADMIN"})
    public void listStudentsWhenUsernameAndPassCorrect200() throws Exception {
        List<Student> students = asList(new Student(11L, "Legolas", "legolas@lotr.com"),
                                        new Student(12L, "Aragorn", "aragorn@lotr.com"));

        Page<Student> pagedStudents = new PageImpl(students);

        when(studentRepository.findAll(isA(Pageable.class))).thenReturn(pagedStudents);
//        ResponseEntity<String> response = restTemplate.getForEntity("/v1/protected/students/", String.class);
//        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/v1/protected/students/"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(studentRepository).findAll(isA(Pageable.class));
    }

    // não funciona
    @Test
    public void getStudentByIdWhenUsernameAndPassCorrect200() {
        Student student = new Student(11L, "Legolas", "legolas@lotr.com");
        when(studentRepository.findById(student.getId())).thenReturn(java.util.Optional.of(student));
        ResponseEntity<String> response = restTemplate.getForEntity("/v1/protected/students/{id}", String.class, student.getId());
        System.out.println(response);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }


    @Test
    @WithMockUser(username = "xxx", password = "xxx", roles = {"USER"})
    public void whenListAllStudentsUsingCorrectRole_thenReturnStatusCode200 () throws Exception {
        List<Student> students = asList(new Student(1L, "Legolas", "legolas@lotr.com"),
                new Student(2L, "Aragorn", "aragorn@lotr.com"));

        Page<Student> pagedStudents = new PageImpl(students);

        when(studentRepository.findAll(isA(Pageable.class))).thenReturn(pagedStudents);

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/v1/protected/students/"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(studentRepository).findAll(isA(Pageable.class));
    }

    @Test
    @WithMockUser(username = "xxx", password = "xxx", roles = {"USER"})
    public void whenGetStudentByIdUsingCorrectRoleAndStudentDoesntExist_thenReturnStatusCode404 () throws Exception {
        Student student = new Student(3L, "Legolas", "legolas@lotr.com");

        when(studentRepository.findById(3L)).thenReturn(java.util.Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/v1/protected/students/{id}", 6))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(studentRepository).findById(6L);
    }

    @Test
    @WithMockUser(username = "xxx", password = "xxx", roles = {"USER"})
    public void whenFindStudentsByNameUsingCorrectRole_thenReturnStatusCode200 () throws Exception {
        List<Student> students = asList(new Student(1L, "Legolas", "legolas@lotr.com"),
                new Student(2L, "Aragorn", "aragorn@lotr.com"),
                new Student(3L, "legolas greenleaf", "legolas.gl@lotr.com"));

        when(studentRepository.findByNameIgnoreCaseContaining("legolas")).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/v1/protected/students?name=legolas"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(studentRepository).findByNameIgnoreCaseContaining("legolas");
    }

    @Test
    @WithMockUser(username = "xxx", password = "xxx", roles = {"ADMIN"})
    public void whenDeleteUsingCorrectRole_thenReturnStatusCode200 () throws Exception {

        Student student = new Student(3L, "Legolas", "legolas@lotr.com");

        when(studentRepository.findById(3L)).thenReturn(java.util.Optional.of(student));

        doNothing().when(studentRepository).deleteById(3L);

        mockMvc.perform(delete("http://localhost:8080/v1/admin/students/{id}", 3))
                .andExpect(status().isOk())
                .andDo(print());

        verify(studentRepository).deleteById(3L);
    }

    @Test
    @WithMockUser(username = "xxx", password = "xxx", roles = {"ADMIN"})
    public void whenDeleteHasRoleAdminAndStudentDontExist_thenReturnStatusCode404 () throws Exception {

        doNothing().when(studentRepository).deleteById(1L);

        mockMvc.perform(delete("http://localhost:8080/v1/admin/students/{id}", 1))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(studentRepository, atLeast(1)).findById(1L);
    }

    @Test
    @WithMockUser(username = "xxx", password = "xxx", roles = {"USER"})
    public void whenDeleteHasRoleUser_thenReturnStatusCode404 () throws Exception {
        doNothing().when(studentRepository).deleteById(1L);

        mockMvc.perform(delete("http://localhost:8080/v1/admin/students/{id}", 1))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "xxx", password = "xxx", roles = {"ADMIN"})
    public void whenSaveHasRoleAdminAndStudentIsNull_thenReturnStatusCode400 () throws Exception {

        Student student = new Student(3L, "", "legolas@lotr.com");

        when(studentRepository.save(student)).thenReturn(student);

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(student);


        mockMvc.perform(post("http://localhost:8080/v1/admin/students/").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }


    @Test
    @WithMockUser(username = "xx", password = "xx", roles = "USER")
    public void whenListAllStudentsUsingCorrectRole_thenReturnCorrectData () throws Exception {
        List<Student> students = asList(new Student(1L, "Legolas", "legolas@lotr.com"),
                new Student(2L, "Aragorn", "aragorn@lotr.com"));

        Page<Student> pagedStudents = new PageImpl(students);

        when(studentRepository.findAll(isA(Pageable.class))).thenReturn(pagedStudents);

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/v1/protected/students/"))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[0].name").value("Legolas"))
                .andExpect(jsonPath("$.content[0].email").value("legolas@lotr.com"))
                .andExpect(jsonPath("$.content[1].id").value("2"))
                .andExpect(jsonPath("$.content[1].name").value("Aragorn"))
                .andExpect(jsonPath("$.content[1].email").value("aragorn@lotr.com"));

        verify(studentRepository).findAll(isA(Pageable.class));
    }
}
