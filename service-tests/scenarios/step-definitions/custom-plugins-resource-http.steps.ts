// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0


import { When } from '@cucumber/cucumber';
import {TestWorld} from "@ase-compas/compas-service-tests/support/world";
import FormData from 'form-data';


When('I POST {string} with multipart form data:', async function (this: TestWorld, url: string, formDataStr: string) {
    const headers = this.authHeader ?? {};
    const resolvedUrl = this.resolveVariables(url);

    const resolvedStr = this.resolveVariables(formDataStr);
    let parsedData: any = {};
    try {
        parsedData = JSON.parse(resolvedStr);
    } catch (e) {
        throw new Error('Provided form data must be valid JSON: ' + e);
    }

    const form = new FormData();
    for (const [key, value] of Object.entries(parsedData)) {
        const strValue = typeof value === 'object' ? JSON.stringify(value) : String(value);
        form.append(key, strValue);
    }

    await this.executeRequest(() =>
        this.http.post(resolvedUrl, {
            headers: {
                ...form.getHeaders(),
                ...headers
            },
            data: form
        })
    );
});