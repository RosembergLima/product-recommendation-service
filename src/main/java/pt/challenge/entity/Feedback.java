package pt.challenge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

/**
 * Entity representing user feedback on a product recommendation.
 * <p>
 * This entity is persisted in the database and includes auditing fields
 * to track when the feedback was created.
 * </p>
 */
@Entity
@Table(name = "feedback")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {

  /**
   * Primary key of the feedback record.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /**
   * The ID of the user who provided the feedback.
   */
  @Column(name = "user_id", nullable = false)
  private String userId;

  /**
   * The ID of the product for which feedback was given.
   */
  @Column(name = "product_id", nullable = false)
  private String productId;

  /**
   * The type of feedback (e.g., LIKED, DISLIKED).
   */
  @Column(nullable = false)
  private String feedback;

  /**
   * Optional additional comment provided by the user.
   */
  private String comment;

  /**
   * Timestamp when the feedback was created. Automatically populated by JPA auditing.
   */
  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

}
