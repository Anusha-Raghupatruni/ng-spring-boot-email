package com.ngemail.configuration;

import com.ngemail.configuration.gateway.SendGridProperties;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class SendGridPropertiesTest {
    @Test
    public void testFields() {
        Field[] fields = SendGridProperties.class.getFields();
        List<String> expectedFields = new ArrayList<>();
        expectedFields.add("apiKey");
        expectedFields.add("baseUrl");

        for (Field field : fields) {
            assertTrue("Unexpected field: " + field.getName(), expectedFields.contains(field.getName()));
        }
    }
}
