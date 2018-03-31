package com.ngemail.configuration;

import com.ngemail.configuration.gateway.MailGunProperties;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class MailGunPropertiesTest {
    @Test
    public void testFields() {
        Field[] fields = MailGunProperties.class.getFields();
        List<String> expectedFields = new ArrayList<>();
        expectedFields.add("apiKey");
        expectedFields.add("baseUrl");

        for (Field field : fields) {
            assertTrue("Unexpected field: " + field.getName(), expectedFields.contains(field.getName()));
        }
    }
}
