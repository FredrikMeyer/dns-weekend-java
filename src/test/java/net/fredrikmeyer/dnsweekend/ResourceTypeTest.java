package net.fredrikmeyer.dnsweekend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ResourceTypeTest {

    @Test
    void testValueOf() {
        ResourceType resourceType = ResourceType.fromCode("A");

        assertEquals(ResourceType.TYPE_A,
                resourceType);
    }

    @Test
    void testValueOfNullReturnsNull() {
        ResourceType resourceType = ResourceType.fromCode(null);

        assertNull(resourceType);
    }
}