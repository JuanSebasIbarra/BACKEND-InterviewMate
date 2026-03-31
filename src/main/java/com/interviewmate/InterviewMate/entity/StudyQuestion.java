package com.interviewmate.InterviewMate.entity;

import com.interviewmate.InterviewMate.enums.StudyDifficulty;
import com.interviewmate.InterviewMate.enums.StudyQuestionType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "study_question")
public class StudyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "study_session_id", nullable = false)
    private StudySession studySession;

    private int orderIndex;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Enumerated(EnumType.STRING)
    private StudyDifficulty difficulty;

    @Enumerated(EnumType.STRING)
    private StudyQuestionType type;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public StudySession getStudySession() { return studySession; }
    public void setStudySession(StudySession studySession) { this.studySession = studySession; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public StudyDifficulty getDifficulty() { return difficulty; }
    public void setDifficulty(StudyDifficulty difficulty) { this.difficulty = difficulty; }

    public StudyQuestionType getType() { return type; }
    public void setType(StudyQuestionType type) { this.type = type; }
}

