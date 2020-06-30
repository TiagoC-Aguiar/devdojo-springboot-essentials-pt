package br.com.devdojo.endpoint;

import br.com.devdojo.error.ResourceNotFoundException;
import br.com.devdojo.model.Student;
import br.com.devdojo.repository.StudentRepository;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("v1")
public class StudentEndpoint {

    private StudentRepository dao;

    @Autowired
    public StudentEndpoint(StudentRepository dao) {
        this.dao = dao;
    }

    @GetMapping("protected/students")
    @ApiOperation(value = "Return a list with all students", response = Student[].class)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "Authorization", value = "Bearer token",
//            required = true, dataType = "string", paramType = "header")
//    })
    public ResponseEntity<?> listStudents(@RequestParam(required = false) String name, Pageable pageable) {
        System.out.println(dao.findAll());
        if(name != null) {
            return findStudentsByName(name);
        }
        return new ResponseEntity<>(dao.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("protected/students/{id}")
    public ResponseEntity<?> findStudentById(@PathVariable Long id, Authentication authentication) {
        System.out.println(authentication);
        studentExists(id);
        Student student = dao.findById(id).get();
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    private ResponseEntity<?> findStudentsByName(String name) {
        return new ResponseEntity<>(dao.findByNameIgnoreCaseContaining(name), HttpStatus.OK);
    }

    @PostMapping("admin/students")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> save(@RequestBody @Valid Student student) {
        return new ResponseEntity<>(dao.save(student), HttpStatus.CREATED);
    }

    @PutMapping("admin/students")
    public ResponseEntity<?> update(@RequestBody Student student) {
        studentExists(student.getId());
        return new ResponseEntity<>(dao.save(student), HttpStatus.OK);
    }

    @DeleteMapping("admin/students/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        studentExists(id);
        dao.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void studentExists(Long id) {
        try {
            dao.findById(id).get();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Estudante nao encontrado para o ID: " + id);
        }
    }
}
