package com.myTMS.demo.dao;

import com.myTMS.demo.dao.abstractclass.createdAt;
import com.myTMS.demo.dao.markerinterface.EmployeeAnswerGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Customer Service 를 제공하기 위한 포스트의 데이터를 담는 객체
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Post extends createdAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Setter(lombok.AccessLevel.NONE)
    private Long id;
    private Long userId;
    @NotEmpty
    @Size(max = 50)
    private String title;
    private String writer;
    private String responser;
    @NotEmpty
    @Size(max = 5000)
    private String content;
    @NotEmpty(groups = EmployeeAnswerGroup.class)
    @Size(max = 5000, groups = EmployeeAnswerGroup.class)
    private String answerContent;
    private boolean isAnswered; // Getter 를 사용하면 json 직렬화 시에 자동으로 is 를 제거해줌
    @Transient
    private boolean isMyPost;

    public Post(String title, String writer, String responser, String content, String answerContent, boolean isAnswered, Long userId) {
        this.title = title;
        this.writer = writer;
        this.responser = responser;
        this.content = content;
        this.answerContent = answerContent;
        this.isAnswered = isAnswered;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", writer='" + writer + '\'' +
                ", responser='" + responser + '\'' +
                ", content='" + content + '\'' +
                ", answerContent='" + answerContent + '\'' +
                ", isAnswered=" + isAnswered +
                ", isMyPost=" + isMyPost +
                '}';
    }
}
