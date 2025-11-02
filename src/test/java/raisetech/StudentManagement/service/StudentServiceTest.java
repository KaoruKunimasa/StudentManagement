package raisetech.StudentManagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.StudentManagement.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  private StudentService sut;


  @BeforeEach
  void before() {
    sut = new StudentService(repository, converter);

  }

  @Test
  void 受講生詳細の一覧検索_リポジトリとコンバーターの処理が適切に呼び出せていること() {
//    事前準備
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    when(repository.search()).thenReturn(studentList);
    when(repository.searchStudentCourselist()).thenReturn(studentCourseList);

//    実行
    sut.searchStudentList();

//    検証
    verify(repository, times(1)).search();
    verify(repository, times(1)).searchStudentCourselist();
    verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList);
  }

  @Test
  void 受講生詳細検索_IDに紐づく受講生情報とその受講生に紐づく受講生コース情報を取得できること() {
//    事前準備
    String id = "999";
    Student student = new Student();
    List<StudentCourse> studentCourse = new ArrayList<>();
    when(repository.searchStudent(id)).thenReturn(student);
    when(repository.searchStudentCourse(student.getId())).thenReturn(studentCourse);

    StudentDetail expected = new StudentDetail(student, studentCourse);

//    実行
    StudentDetail actual = sut.searchStudent(id);

//    検証
    verify(repository, times(1)).searchStudent(id);
    verify(repository, times(1)).searchStudentCourse(student.getId());
    assertEquals(expected.getStudent().getId(), actual.getStudent().getId());

  }

  @Test
  void 受講生詳細の登録_コース情報がある場合_受講生コースも登録されること() {
    // 事前準備
    StudentDetail studentDetail = new StudentDetail();

    // Studentをセット
    Student student = new Student();
    student.setId("1");
    student.setName("山田太郎");
    studentDetail.setStudent(student);

    // StudentCourseを1件作成してセット
    StudentCourse course = new StudentCourse();
    course.setId("C001");
    course.setCourseName("Java基礎講座");

    List<StudentCourse> courseList = new ArrayList<>();
    courseList.add(course);
    studentDetail.setStudentCourseList(courseList);

    // モック設定
    doNothing().when(repository).registerStudent(student);
    doNothing().when(repository).registerStudentCourse(course);

    // 実行
    StudentDetail result = sut.registerStudent(studentDetail);

    // 検証
    verify(repository, times(1)).registerStudent(student);
    verify(repository, times(1)).registerStudentCourse(course); // コース1件分呼ばれる
  }

  @Test
  void 受講生詳細の登録_初期化処理が行われること() {
//    事前準備
    String id = "999";
    Student student = new Student();
    student.setId(id);
    StudentCourse studentCourse = new StudentCourse();

//    実行
    sut.initStudentsCourse(studentCourse, student.getId());

//    検証
    assertEquals(id, studentCourse.getStudentId());
    assertEquals(LocalDateTime.now().getHour(),
        studentCourse.getCourseEndAt().getHour());
    assertEquals(LocalDateTime.now().plusYears(1).getYear(),
        studentCourse.getCourseEndAt().getYear());
  }


  @Test
  void 受講生詳細の更新_コース情報がある場合_受講生詳細情報が更新されること() {
    // 事前準備
    StudentDetail studentDetail = new StudentDetail();

    // Studentをセット
    Student student = new Student();
    student.setId("1");
    student.setName("大野智");
    studentDetail.setStudent(student);

    // StudentCourseを1件作成してセット
    StudentCourse course = new StudentCourse();
    course.setId("C001");
    course.setCourseName("Webマーケティングコース");

    List<StudentCourse> courseList = new ArrayList<>();
    courseList.add(course);
    studentDetail.setStudentCourseList(courseList);

    // モック設定
    doNothing().when(repository).updateStudent(student);
    doNothing().when(repository).updateStudentCourse(course);

    // 実行
    sut.updateStudent(studentDetail);

    // 検証
    verify(repository, times(1)).updateStudent(student);
    verify(repository, times(1)).updateStudentCourse(course); // コース1件分呼ばれる
  }


}