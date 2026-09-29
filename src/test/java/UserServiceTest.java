import com.sprint.mission.BeforeEachAfterEach.UserService;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * > Task :testClasses
 * BeforeEach 실행
 * createUser_shouldNotReturnNull 실행
 * AfterEach 실행
 * BeforeEach 실행
 * createUser_shouldReturnName 실행
 * AfterEach 실행
 * > Task :test
 * BUILD SUCCESSFUL in 1s
 * 3 actionable tasks: 3 executed
 */

public class UserServiceTest {
    private UserService userService;

    @BeforeAll
    static void connectToDatabase() {
        System.out.println("테스트 DB 연결 시작");
    }

    @BeforeEach
    void setUp() {
        System.out.println("BeforeEach 실행");
        userService = new UserService();
    }

    @AfterEach
    void tearDown() {
        System.out.println("AfterEach 실행");
    }

    @AfterAll
    static void disconnectFromDatabase() {
        System.out.println("테스트 DB 연결 해제");
    }

    @Test
    void createUser_shouldReturnName() {
        String result = userService.create("kim");

        assertEquals("kim", result);
        System.out.println("createUser_shouldReturnName 실행");
    }

    @Test
    void createUser_shouldNotReturnNull() {
        String result = userService.create("lee");

        assertNotNull(result);
        System.out.println("createUser_shouldNotReturnNull 실행");
    }
}
