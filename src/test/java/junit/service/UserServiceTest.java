package junit.service;

import junit.dto.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;


import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.InstanceOfAssertFactories.PERIOD;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

// @TestInstance(TestInstance.Lifecycle.PER_METHOD)  by default
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User OLEG = User.of(2, "Oleg", "123");

    private UserService userService;

    @BeforeAll
    void init() {
        System.out.println("Before all: " + this);
    }


    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this);
        userService = new UserService();
    }


    @Test
    void usersEmptyIfNoUserAdded() {
        System.out.println("test1: " + this);
        var users = userService.getAll();
        assertTrue(users.isEmpty());
    }

    @Test
    void userSizeIfUserAdded() {
        System.out.println("test2: " + this);
        userService.add(IVAN);
        userService.add(OLEG);
        var users = userService.getAll();
        assertThat(users).hasSize(2);
//        assertEquals(2, 2);
    }

    @Test
    void loginSuccessIfUserExist() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.getUserName(), IVAN.getPassword());
        assertThat(maybeUser).isPresent();
        maybeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));
//        assertTrue(maybeUser.isPresent());
//        maybeUser.ifPresent(user -> assertEquals(IVAN, user));

    }

//    @Test
//    void throwExceptionIfUsernameOrPasswordIsNull() {
//        try {
//            userService.login(null, "dummy");
//            fail("login should throw exception on null username");
//        } catch (IllegalArgumentException e) {
//            assertTrue(true);
//        }
//    }

    @Test
    void throwExceptionIfUsernameOrPasswordIsNull() {
        assertAll(
                () -> {
                    var exception = assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy"));
                    assertThat(exception.getMessage()).isEqualTo("username or password is null");
                },
                () -> assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null))
        );

    }


    @Test
    void loginFailIfPasswordIsNotCorrect() {
        userService.add(IVAN);
        Optional<User> dummy = userService.login(IVAN.getUserName(), "000");
        assertTrue(dummy.isEmpty());
    }

    @Test
    void usersConvertedToMapId() {
        userService.add(IVAN, OLEG);
        Map<Integer, User> users = userService.getAllConvertedById();

        assertAll(
                () -> assertThat(users).containsKeys(IVAN.getId(), OLEG.getId()),
                () -> assertThat(users).containsValues(IVAN, OLEG));

    }

    @Test
    void loginFailIfUserDoesNotExist() {
        userService.add(IVAN);
        Optional<User> dummy = userService.login("OLEG", IVAN.getPassword());
        assertTrue(dummy.isEmpty());
    }

    @AfterEach
    void deleteDataFromDataBase() {
        System.out.println("After each: " + this);
    }

    @AfterAll
    void closeConnectionPool() {
        System.out.println("After all: " + this);
    }
}
