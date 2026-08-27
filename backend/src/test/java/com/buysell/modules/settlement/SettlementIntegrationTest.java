package com.buysell.modules.settlement;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureEmbeddedDatabase
@ActiveProfiles("test")
public class SettlementIntegrationTest {

    @Test
    void dummyTest() {
        assertTrue(true);
    }
}
