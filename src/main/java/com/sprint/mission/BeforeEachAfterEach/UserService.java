package com.sprint.mission.BeforeEachAfterEach;

import java.util.Objects;

public class UserService {
    public String create(String name) {
        if (Objects.isNull(name)) throw new IllegalArgumentException();
        return name;
    }
}
