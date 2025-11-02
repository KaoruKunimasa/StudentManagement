package raisetech.StudentManagement.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.service.StudentService;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private StudentService service;

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void 受講生詳細の一覧検索が実行できて空のリストが帰ってくること() throws Exception {

//    事前準備
    when(service.searchStudentList()).thenReturn(List.of(new StudentDetail()));

//    検証
    mockMvc.perform(MockMvcRequestBuilders.get("/studentList"))
        .andExpect(status().isOk())
        .andExpect(content().json("[{\"student\":null,\"studentCourseList\":null}]"));

    verify(service, times(1)).searchStudentList();
  }

  @Test
  void 受講生詳細の受講生で適切な値を入力した時に入力チェックで異常が発生しないこと() {

//    事前準備
    Student student = new Student();
    student.setId("1");
    student.setName("中村嶺亜");
    student.setKanaName("ナカムラレイア");
    student.setNickname("れいあ");
    student.setEmail("reia@example.com");
    student.setArea("東京都");
    student.setSex("男性");

//    実行
    Set<ConstraintViolation<Student>> violations = validator.validate(student);

//    検証
    assertThat(violations.size()).isEqualTo(0); // assertThat(値).isEqualTo(期待値) の形にする

  }

  @Test
  void 受講生詳細の受講生でIDに数字以外を用いた時に入力チェックに掛かること() {

//    事前準備
    Student student = new Student();
    student.setId("テストです。");
    student.setName("中村嶺亜");
    student.setKanaName("ナカムラレイア");
    student.setNickname("れいあ");
    student.setEmail("reia@example.com");
    student.setArea("東京都");
    student.setSex("男性");

//    実行
    Set<ConstraintViolation<Student>> violations = validator.validate(student);

//    検証
    assertThat(violations.size()).isEqualTo(1); // assertThat(値).isEqualTo(期待値) の形にする
    assertThat(violations).extracting("message").containsOnly("数値のみ入力してください");

  }

  @Test
  void IDに紐づく受講生情報と受講生コース情報の検索ができて空のリストが帰ってくること()
      throws Exception {

//    事前準備
    String id = "999";
    when(service.searchStudent(id)).thenReturn(new StudentDetail());

//    検証
    mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}", id))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"student\":null,\"studentCourseList\":null}"));

    verify(service, times(1)).searchStudent(id);
  }

  @Test
  void IDが数値でない場合は400エラーが返ること() throws Exception {
//    事前準備
    String id = "abc";

//    検証
    mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}", id))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Invalid request parameter"));

//    serviceは呼ばれない
    verify(service, never()).searchStudent(any());
  }

  @Test
  void 受講生詳細の登録が実行できること() throws Exception {
    // 事前準備
    StudentDetail inputDetail = createTestStudentDetail();
    when(service.registerStudent(any(StudentDetail.class))).thenReturn(inputDetail);

    // 検証
    mockMvc.perform(MockMvcRequestBuilders.post("/registerStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(inputDetail)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.student.name").value("本髙克樹"));

    verify(service, times(1)).registerStudent(any(StudentDetail.class));
  }

  @Test
  void 受講生詳細の更新が実行できること() throws Exception {
    // 事前準備
    StudentDetail updateDetail = createTestStudentDetail();
    doNothing().when(service).updateStudent(any(StudentDetail.class));

    // 検証
    mockMvc.perform(MockMvcRequestBuilders.put("/updateStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDetail)))
        .andExpect(status().isOk())
        .andExpect(content().string("更新処理が成功しました。"));

    verify(service, times(1)).updateStudent(any(StudentDetail.class));
  }

  private StudentDetail createTestStudentDetail() {
    StudentDetail detail = new StudentDetail();
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
    detail.setStudent(student);

    StudentCourse studentCourse = new StudentCourse();
    List<StudentCourse> studentCourses = new ArrayList<>();
    LocalDateTime now = LocalDateTime.now();
    studentCourse.setId("3");
    studentCourse.setStudentId("126");
    studentCourse.setCourseName("Javaフルコース");
    studentCourse.setCourseStartAt(now);
    studentCourse.setCourseEndAt(now.plusYears(1));
    studentCourses.add(studentCourse);
    detail.setStudentCourseList(studentCourses);

    return detail;
  }

  @Test
  void 受講生詳細のAPIが実行できてステータスが400で返ってくること() throws Exception {
    mockMvc.perform(get("/studentListErr"))
        .andExpect(status().is4xxClientError())
        .andExpect(content().string(
            "現在このAPIは利用できません。URLは「studentListErr」ではなく、「studentList」を利用ください。"));
  }
}