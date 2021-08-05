// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_ELEMENT_NAME;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_NS_URI;

@ExtendWith(MockitoExtension.class)
class CompasSclDataPostgreSQLRepositoryTest {
    @InjectMocks
    private CompasSclDataPostgreSQLRepository repository;

    @Test
    void list_WhenCalled_ThenUnsupportedExceptionThrown() {
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.list(SclType.SCD);
        });
    }

    @Test
    void listVersionsByUUID_WhenCalled_ThenUnsupportedExceptionThrown() {
        var uuid = UUID.randomUUID();
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.listVersionsByUUID(SclType.SCD, uuid);
        });
    }

    @Test
    void findByUUID_WhenCalledWithoutVersion_ThenUnsupportedExceptionThrown() {
        var uuid = UUID.randomUUID();
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.findByUUID(SclType.SCD, uuid);
        });
    }

    @Test
    void findByUUID_WhenCalledWithVersion_ThenUnsupportedExceptionThrown() {
        var uuid = UUID.randomUUID();
        var version = new Version(1, 0, 0);
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.findByUUID(SclType.SCD, uuid, version);
        });
    }

    @Test
    void create_WhenCalled_ThenUnsupportedExceptionThrown() {
        var uuid = UUID.randomUUID();
        var scl = createSCL();
        var version = new Version(1, 0, 0);
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.create(SclType.SCD, uuid, scl, version);
        });
    }

    @Test
    void delete_WhenCalledWithoutVersion_ThenUnsupportedExceptionThrown() {
        UUID uuid = UUID.randomUUID();
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.delete(SclType.SCD, uuid);
        });
    }

    @Test
    void delete_WhenCalledWithVersion_ThenUnsupportedExceptionThrown() {
        var uuid = UUID.randomUUID();
        var version = new Version(1, 0, 0);
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.delete(SclType.SCD, uuid, version);
        });
    }

    private Element createSCL() {
        var qName = new QName(SCL_NS_URI, SCL_ELEMENT_NAME);
        return new JAXBElement<>(qName, Element.class, null).getValue();
    }
}