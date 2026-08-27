package com.buysell.modules.settlement;

import com.buysell.security.CurrentUserService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureEmbeddedDatabase
@ActiveProfiles("test")
public class SettlementIntegrationTest {

    @SuppressWarnings("unused")
    @MockBean
    private CurrentUserService currentUserService;

    @Test
    void dummyTest() {
        assertTrue(true);
    }
}
