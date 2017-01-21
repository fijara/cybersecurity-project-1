package sec.project.repository;

import java.util.List;
import sec.project.domain.Signup;

/**
 *
 * @author janne
 */
public interface CustomRepository {
    
    List<Signup> findByInjectableQuery(String emailAddress);
}
