package raisetech.StudentManagement.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;

@ExtendWith(MockitoExtension.class)
class StudentConverterTest {

  private StudentConverter sut;

  @BeforeEach
  void setUp() {
    sut = new StudentConverter();
  }

  @Test
  void 受講生のリストと受講生コース情報のリストを渡して受講生詳細のリストが作成できること() {
//    準備

    Student student = createTestStudent();
    List<Student> studentList = List.of(student);
    StudentCourse studentCourse = createTestStudentCourse();
    List<StudentCourse> studentCourseList = List.of(studentCourse);

//    実行
    List<StudentDetail> actualList = sut.convertStudentDetails(studentList,
        studentCourseList);

//    検証
    StudentDetail actual = actualList.get(0);
    assertThat(actual.getStudent()).isEqualTo(student);
    assertThat(actual.getStudentCourseList()).hasSize(1);
    assertThat(actual.getStudentCourseList()).isEqualTo(studentCourseList);


  }

  @Test
  void 受講生のリストと受講生コース情報のリストを渡した時に紐づかない受講生コース情報は除外されること() {
//    準備
    Student student = createTestStudent();
    List<Student> studentList = List.of(student);
    StudentCourse studentCourse = createTestStudentCourse2();
    List<StudentCourse> studentCourseList = List.of(studentCourse);

//    実行
    List<StudentDetail> actualList = sut.convertStudentDetails(studentList,
        studentCourseList);

//    検証
    StudentDetail actual = actualList.get(0);
    assertThat(actual.getStudent()).isEqualTo(student);
    assertThat(actual.getStudentCourseList()).hasSize(0);
    assertThat(actual.getStudentCourseList()).isEmpty();

  }

  private Student createTestStudent() {

    Student student = new Student();
    student.setId("126");
    student.setName("本髙克樹");
    student.setKanaName("モトダカカツキ");
    student.setNickname("かっちゃん");
    student.setEmail("katuki@example.com");
    student.setArea("東京都");
    student.setAge(26);
    student.setSex("男性");
    student.setRemark(null);
    student.setDeleted(false);

    return student;

  }

  private StudentCourse createTestStudentCourse() {

    StudentCourse studentCourse = new StudentCourse();
    LocalDateTime now = LocalDateTime.now();
    studentCourse.setId("3");
    studentCourse.setStudentId("126");
    studentCourse.setCourseName("Javaフルコース");
    studentCourse.setCourseStartAt(now);
    studentCourse.setCourseEndAt(now.plusYears(1));

    return studentCourse;
  }

  private StudentCourse createTestStudentCourse2() {

    StudentCourse studentCourse = new StudentCourse();
    LocalDateTime now = LocalDateTime.now();
    studentCourse.setId("3");
    studentCourse.setStudentId("127");
    studentCourse.setCourseName("デザインコース");
    studentCourse.setCourseStartAt(now);
    studentCourse.setCourseEndAt(now.plusYears(1));

    return studentCourse;
  }


}