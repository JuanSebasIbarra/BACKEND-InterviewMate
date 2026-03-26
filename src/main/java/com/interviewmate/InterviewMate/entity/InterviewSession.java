package com.interviewmate.InterviewMate.entity;

import com.interviewmate.InterviewMate.enums.SessionStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "interview_session")
public class InterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private InterviewTemplate template;

    private int attemptNumber;

    @Enumerated(EnumType.STRING)
    private SessionStatus status = SessionStatus.PENDING;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public InterviewTemplate getTemplate() { return template; }
    public void setTemplate(InterviewTemplate template) { this.template = template; }

    public int getAttemptNumber() { return attemptNumber; }
    public void setAttemptNumber(int attemptNumber) { this.attemptNumber = attemptNumber; }

    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
