// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.event.model;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.extensions.model.SclFileType;
import org.mockito.Mockito;

import javax.websocket.Session;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateEventRequestTest {
    @Test
    void constructor_WhenCalledWith3Arguments_ThenValuesSet() {
        var session = Mockito.mock(Session.class);
        var type = SclFileType.CID;
        var name = "Some name";
        var who = "Who executed it";
        var comment = "Some comment";
        var sclData = "Some SCL Data";

        var result = new CreateEventRequest(session, type, name, who, comment, sclData);

        assertEquals(session, result.getSession());
        assertEquals(type, result.getType());
        assertEquals(name, result.getName());
        assertEquals(who, result.getWho());
        assertEquals(comment, result.getComment());
        assertEquals(sclData, result.getSclData());
    }

    @Test
    void validateSettersAndGetters() {
        var personPojo = PojoClassFactory.getPojoClass(CreateEventRequest.class);
        var validator = ValidatorBuilder.create()
                // Let's make sure that we have a getter for every field defined.
                .with(new GetterMustExistRule())
                // Let's also validate that they are behaving as expected
                .with(new GetterTester())
                .build();

        // Start the Test
        validator.validate(personPojo);
    }
}
