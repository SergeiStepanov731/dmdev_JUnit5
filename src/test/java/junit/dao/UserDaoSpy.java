package junit.dao;

import java.util.HashMap;
import java.util.Map;

public class UserDaoSpy extends UserDao {

    private UserDao userDao;
    private Map<Integer, Boolean> answer = new HashMap<>();
//    private Answer1<Integer, Boolean> answer1;


    public UserDaoSpy(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean deleteByUserId(Integer id) {
        return answer.getOrDefault(id, userDao.deleteByUserId(id));
    }
}
