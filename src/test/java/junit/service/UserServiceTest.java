package junit.service;

import junit.TestBase;
import junit.dao.UserDao;
import junit.dto.User;
import junit.extension.ConditionalExtension;
import junit.extension.PostProcessingExtension;
import junit.extension.ThrowableExtension;
import junit.extension.UserServiceParamResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

// @TestInstance(TestInstance.Lifecycle.PER_METHOD)  by default
@Tag("fast")
@Tag("user")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@TestMethodOrder(MethodOrderer.Random.class)
@ExtendWith({
        UserServiceParamResolver.class,
        PostProcessingExtension.class,
        ConditionalExtension.class,
//        ThrowableExtension.class
//        GlobalExtension.class
})
public class UserServiceTest extends TestBase {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User OLEG = User.of(2, "Oleg", "123");

    private UserDao userDao;
    private UserService userService;

    public UserServiceTest(TestInfo testInfo) {
        System.out.println();
    }

    @BeforeAll
    void init() {
        System.out.println("Before all: " + this);
    }


    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this);
//        this.userDao = Mockito.mock(UserDao.class);
        this.userDao = Mockito.spy(new UserDao());
        this.userService = new UserService(userDao);
    }

    @Test
    void shouldDeleteExistedUser() {
        userService.add(IVAN);
        Mockito.doReturn(true).when(userDao).deleteByUserId(IVAN.getId()); // stub
//        Mockito.doReturn(true).when(userDao).deleteByUserId(Mockito.any()); // dummy
//        Mockito.when(userDao.deleteByUserId(IVAN.getId())).thenReturn(true).thenReturn(false);
        boolean deleteResult = userService.delete(IVAN.getId());
        System.out.println(userService.delete(IVAN.getId()));
        System.out.println(userService.delete(IVAN.getId()));


        ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(userDao, Mockito.times(3)).deleteByUserId(integerArgumentCaptor.capture());

        assertThat(integerArgumentCaptor.getValue()).isEqualTo(IVAN.getId());
        assertThat(deleteResult).isTrue();

    }


    @Test
//    @Order(1)
//    @DisplayName("123")
    void usersEmptyIfNoUserAdded(UserService userService) throws IOException {
        if (true) {
            throw new RuntimeException();
        }
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
    void usersConvertedToMapId() {
        userService.add(IVAN, OLEG);
        Map<Integer, User> users = userService.getAllConvertedById();

        assertAll(
                () -> assertThat(users).containsKeys(IVAN.getId(), OLEG.getId()),
                () -> assertThat(users).containsValues(IVAN, OLEG));

    }


    @AfterEach
    void deleteDataFromDataBase() {
        System.out.println("After each: " + this);
    }

    @AfterAll
    void closeConnectionPool() {
        System.out.println("After all: " + this);
    }

    @Nested
    @DisplayName("nested test class about login")
    @Tag("login")
    class LoginTest {


        @Test
        @Disabled("vot tak vot")
        void loginSuccessIfUserExist() {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login(IVAN.getUserName(), IVAN.getPassword());
            assertThat(maybeUser).isPresent();
            maybeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));
//        assertTrue(maybeUser.isPresent());
//        maybeUser.ifPresent(user -> assertEquals(IVAN, user));

        }

//        @Test
//        void checkLoginFunctionalityPerformance() {
//            Optional<User> result = assertTimeout(Duration.ofMillis(200L), () -> {
//                Thread.sleep(300);
//                return userService.login("dummy", IVAN.getPassword());
//            });
//
//
//        }

        @Test
//        @Timeout(value = 200, unit = TimeUnit.MICROSECONDS)
        void checkLoginFunctionalityPerformance() {
            System.out.println(Thread.currentThread().getName());
            Optional<User> result = assertTimeoutPreemptively(Duration.ofMillis(600L), () -> {
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(300);
                return userService.login("dummy", IVAN.getPassword());
            });


        }



//    @Test
//    @Tag("login")
//    void throwExceptionIfUsernameOrPasswordIsNull() {
//        try {
//            userService.login(null, "dummy");
//            fail("login should throw exception on null username");
//        } catch (IllegalArgumentException e) {
//            assertTrue(true);
//        }
//    }

        @Test
        @RepeatedTest(value = 5, name = RepeatedTest.LONG_DISPLAY_NAME)
        void throwExceptionIfUsernameOrPasswordIsNull() {
            assertAll(
                    () -> {
                        var exception = assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy"));
                        assertThat(exception.getMessage()).isEqualTo("username or password is null");
                    },
                    () -> {
                        var exception = assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null));
                        assertThat(exception.getMessage()).isEqualTo("username or password is null");
                    }
            );
        }


        @Test
        void loginFailIfPasswordIsNotCorrect() {
            userService.add(IVAN);
            Optional<User> dummy = userService.login(IVAN.getUserName(), "000");
            assertTrue(dummy.isEmpty());
        }

        @Test
        void loginFailIfUserDoesNotExist() {
            userService.add(IVAN);
            Optional<User> dummy = userService.login("OLEG", IVAN.getPassword());
            assertTrue(dummy.isEmpty());
        }


        @ParameterizedTest(name = "{arguments} test")
//        @ArgumentsSource()
//        @NullSource
//        @EmptySource
//        @NullAndEmptySource
//        @ValueSource(strings = {
//                "Ivan", "Oleg"
//        })
//        @EnumSource
        @MethodSource("junit.service.UserServiceTest#getArgumentsForLoginTest")
//        @CsvFileSource(resources = "/login-test-data.csv", delimiter = ',', numLinesToSkip = 1)
//        @CsvSource({
//                "Ivan, 123",
//                "Oleg, 123",
//        })
        @DisplayName("login param test")
        void loginParameterizedTest(String userName, String password, Optional<User> user) {
            userService.add(IVAN, OLEG);
            Optional<User> maybeUser = userService.login(userName, password);
            assertThat(maybeUser).isEqualTo(user);
        }

    }

    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("Ivan", "123", Optional.of(IVAN)),
                Arguments.of("Oleg", "123", Optional.of(OLEG)),
                Arguments.of("Oleg", "dummy", Optional.empty()),
                Arguments.of("dummy", "123", Optional.empty())
        );
    }
}
