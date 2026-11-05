// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0

import { TestWorld } from "@ase-compas/compas-service-tests/support/world";
import { After } from "@cucumber/cucumber";

After(async function (this: TestWorld, scenario) {
    const revCleanupTasks = this.savedValues.cleanUpTasks.reverse();
    for (const task of revCleanupTasks) {
        try {
            await task();
        } catch (e) {
            this.log(`Cleanup task failed: ${e}`);
        }
    }
    this.log(`Finished: ${scenario.pickle.name} - ${scenario.result?.status}`);
});
