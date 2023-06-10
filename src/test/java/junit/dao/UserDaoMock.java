package junit.dao;

import org.mockito.stubbing.Answer1;

import java.util.HashMap;
import java.util.Map;

public class UserDaoMock extends UserDao {

    private Map<Integer, Boolean> answer = new HashMap<>();
//    private Answer1<Integer, Boolean> answer1;

    @Override
    public boolean deleteByUserId(Integer id) {
        return answer.getOrDefault(id, false);
    }
}
