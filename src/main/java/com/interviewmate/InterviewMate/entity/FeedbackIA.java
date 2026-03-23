package com.interviewmate.InterviewMate.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity
@Table(name = "feedback_ia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", nullable = false, unique = true)
    private Answer answer;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String feedback; // Feedback generado por IA

    private Integer confidenceScore; // Confianza del feedback (0-100)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackType feedbackType; // POSITIVE, NEUTRAL, NEGATIVE, NEEDS_IMPROVEMENT

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public enum FeedbackType {
        POSITIVE, NEUTRAL, NEGATIVE, NEEDS_IMPROVEMENT
    }
}

