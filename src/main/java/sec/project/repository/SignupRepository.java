package sec.project.repository;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sec.project.domain.Signup;

public interface SignupRepository extends JpaRepository<Signup, Long>, CustomRepository {
    
    @Query(value = "SELECT * FROM SIGNUP WHERE EMAIL LIKE ?1", nativeQuery = true)
    List<Signup> findByEmailAddress(String emailAddress);
    
}
