package junit.service;

import junit.dao.UserDao;
import junit.dto.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;


public class UserService {

    private final List<User> users = new ArrayList<>();
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getAll() {

        return users;
    }

    public boolean delete(Integer userId) {
        return userDao.deleteByUserId(userId);
    }


    public void add(User...users) {
        this.users.addAll(Arrays.asList(users));
    }

    public Optional<User> login(String userName, String password) {
        if (userName == null || password == null) {
            throw new IllegalArgumentException("username or password is null");
        }
        return users.stream().filter(user -> user.getUserName().equals(userName)).filter(user -> user.getPassword().equals(password)).findFirst();
    }

    public Map<Integer, User> getAllConvertedById() {
        return users.stream()
                .collect(toMap(User::getId, Function.identity()));
    }
}
