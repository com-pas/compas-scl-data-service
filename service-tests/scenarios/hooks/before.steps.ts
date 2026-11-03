// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0


import { Before } from "@cucumber/cucumber";
import { TestWorld } from "@ase-compas/compas-service-tests/support/world";

Before(async function (this: TestWorld, scenario) {
    this.log(`Starting: ${scenario.pickle.name}`);
    this.requestHeaders = {};
    this.savedValues = {};
    this.savedValues.cleanUpTasks = [];
});
