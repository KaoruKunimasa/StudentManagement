package raisetech.StudentManagement.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

@MybatisTest
class StudentRepositoryTest {

  @Autowired
  StudentRepository sut;

  @Test
  void 受講生の全件検索が行えること() {
    List<Student> actual = sut.search();
    assertThat(actual.size()).isEqualTo(6);
  }

  @Test
  void 受講生の検索が行えること() {
    Student actual = sut.searchStudent("1");

    assertThat(actual.getName()).isEqualTo("中村嶺亜");
    assertThat(actual.getKanaName()).isEqualTo("ナカムラレイア");
    assertThat(actual.getNickname()).isEqualTo("れいあ");
    assertThat(actual.getEmail()).isEqualTo("reia@example.com");
    assertThat(actual.getArea()).isEqualTo("東京都");
    assertThat(actual.getAge()).isEqualTo(27);
    assertThat(actual.getSex()).isEqualTo("男性");
    assertThat(actual.getRemark()).isEqualTo(null);
    assertThat(actual.isDeleted()).isEqualTo(true);

  }

  @Test
  void 受講生のコース情報の全件検索が行えること() {
    List<StudentCourse> actual = sut.searchStudentCourselist();
    assertThat(actual.size()).isEqualTo(10);
  }

  @Test
  void 受講生IDに紐づく受講生コース情報が検索できること() {
    List<StudentCourse> actual = sut.searchStudentCourse("2");

    assertThat(actual.size()).isEqualTo(2);
    assertThat(actual.get(0).getStudentId()).isEqualTo("2");
    assertThat(actual.get(0).getCourseName()).isEqualTo("Javaフルコース");

//    フォーマッタを用意
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//    期待値をLocalDateTimeに変換して比較
    assertThat(actual.get(0).getCourseStartAt())
        .isEqualTo(LocalDateTime.parse("2025-02-01 09:00:00", formatter));
    assertThat(actual.get(0).getCourseEndAt())
        .isEqualTo(LocalDateTime.parse("2025-05-01 09:00:00", formatter));

  }


  @Test
  void 受講生の登録が行えること() {
    Student student = new Student();
    student.setName("稲葉通陽");
    student.setKanaName("イナバミチハル");
    student.setNickname("みっちー");
    student.setEmail("michiharu@example.com");
    student.setArea("神奈川県");
    student.setAge(20);
    student.setSex("男性");
    student.setRemark(null);
    student.setDeleted(false);

    sut.registerStudent(student);

    List<Student> actual = sut.search();
    assertThat(actual.size()).isEqualTo(7);

    Student actual2 = sut.searchStudent("7");
    assertThat(actual2.getName()).isEqualTo("稲葉通陽");
    assertThat(actual2.getKanaName()).isEqualTo("イナバミチハル");
    assertThat(actual2.getNickname()).isEqualTo("みっちー");
    assertThat(actual2.getEmail()).isEqualTo("michiharu@example.com");
    assertThat(actual2.getArea()).isEqualTo("神奈川県");
    assertThat(actual2.getAge()).isEqualTo(20);
    assertThat(actual2.getSex()).isEqualTo("男性");
    assertThat(actual2.getRemark()).isEqualTo(null);
    assertThat(actual2.isDeleted()).isEqualTo(false);


  }

  @Test
  void 受講生コース情報の登録が行えること() {
    StudentCourse studentCourse = new StudentCourse();
    LocalDateTime now = LocalDateTime.now();
    studentCourse.setStudentId("126");
    studentCourse.setCourseName("Javaフルコース");
    studentCourse.setCourseStartAt(now);
    studentCourse.setCourseEndAt(now.plusYears(1));

    sut.registerStudentCourse(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCourselist();
    assertThat(actual.size()).isEqualTo(11);

  }

  @Test
  void 受講生を更新できること() {
    Student student = new Student();
    student.setId("4");
    student.setName("今野大輝");
    student.setKanaName("コンノタイキ");
    student.setNickname("こにゃ");
    student.setEmail("konpi@example.com");
    student.setArea("神奈川県");
    student.setAge(26);
    student.setSex("男性");
    student.setRemark(null);
    student.setDeleted(true);

    sut.updateStudent(student);

    Student actual = sut.searchStudent("4");
    assertThat(actual.getName()).isEqualTo("今野大輝");
    assertThat(actual.getKanaName()).isEqualTo("コンノタイキ");
    assertThat(actual.getNickname()).isEqualTo("こにゃ");
    assertThat(actual.getEmail()).isEqualTo("konpi@example.com");
    assertThat(actual.getArea()).isEqualTo("神奈川県");
    assertThat(actual.getAge()).isEqualTo(26);
    assertThat(actual.getSex()).isEqualTo("男性");
    assertThat(actual.getRemark()).isEqualTo(null);
    assertThat(actual.isDeleted()).isEqualTo(true);

  }

  @Test
  void 受講生コース情報のコース名を更新できること() {

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId("7");
    studentCourse.setCourseName("WordPress副業コース");

    sut.updateStudentCourse(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCourse("2");

    assertThat(actual.size()).isEqualTo(2);
    assertThat(actual.get(1).getId()).isEqualTo("7");
    assertThat(actual.get(1).getStudentId()).isEqualTo("2");
    assertThat(actual.get(1).getCourseName()).isEqualTo("WordPress副業コース");

    sut.updateStudentCourse(studentCourse);

  }
}