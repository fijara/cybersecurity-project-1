package sec.project.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sec.project.domain.Signup;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author janne
 */
public class SignupRepositoryImpl implements CustomRepository {
    
    
    @PersistenceContext
    private EntityManager em;
  
    @Override
    public List<Signup> findByInjectableQuery(String emailAddress) {
        //
        // Can be attacked with value: inject' OR 1=1 OR 'X'='X
        String sql = "SELECT * FROM SIGNUP WHERE EMAIL = '" + emailAddress + "' ORDER BY id";
        Query query = em.createNativeQuery(sql, Signup.class);
        List<Signup> retValue = query.getResultList();
        return retValue;
    }
}
