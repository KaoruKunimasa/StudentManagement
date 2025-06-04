package raisetech.StudentManagement.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentsCourses;
import raisetech.StudentManagement.repository.StudentRepository;

@Service
public class StudentService {

  private StudentRepository repository;

  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  public List<Student> searchStudentList() {
    List<Student> students = repository.search();

    // フィルタリング結果を格納するリスト
    List<Student> filteredList = new ArrayList<>();

    // Loopで1件ずつチェック
    for (Student student : students) {
      if (student.getAge() >= 26) {
        filteredList.add(student);
      }
    }

    return filteredList;

  }

  public List<StudentsCourses> searchStudentsCourseList() {
    List<StudentsCourses> studentsCourses = repository.searchStudentsCourses();

    // フィルタリング結果を格納するリスト
    List<StudentsCourses> filteredList = new ArrayList<>();

    // Loopで1件ずつチェック
    for (StudentsCourses courses : studentsCourses) {
      if (courses.getCourseName().equals("Javaフルコース")) {
        filteredList.add(courses);
      }
    }

    return filteredList;

  }

}
