package pt.challenge.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.challenge.entity.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {

}
