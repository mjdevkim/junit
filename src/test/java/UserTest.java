import com.sprint.mission.assertions.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void userValidation() {
        User user = new User("mj@example.com", "mj");

        assertAll("사용자 검증",
                () -> assertEquals("mj", user.getName()),
                () -> assertTrue(user.getEmail().contains("@")),
                () -> assertNotNull(user));
        System.out.println("UserTest - userValidation 실행");
    }

}
