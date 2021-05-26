package org.lfenergy.compas.scl.data.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ObjectFactoryTest {
    @Test
    void createItem_WhenMethodsCalled_ThenObjectsAreCreated() {
        var objectFactory = new ObjectFactory();

        var item = objectFactory.createItem();
        assertNotNull(item);

        var itemElement = objectFactory.createItem(item);
        assertNotNull(itemElement);
        assertEquals(Item.class, itemElement.getDeclaredType());
        assertEquals(item, itemElement.getValue());
    }
}