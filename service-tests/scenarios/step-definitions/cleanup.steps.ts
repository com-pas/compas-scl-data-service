import { TestWorld } from "@ase-compas/compas-service-tests/support/world";
import { Then } from "@cucumber/cucumber";

Then('I schedule a POST for {string}', function (this: TestWorld, url: string) {
    const resolvedUrl = this.resolveVariables(url);
    this.savedValues.cleanUpTasks.push(async () => {
        const cleanupHeaders = this.authHeader ?? {};
        this.log(`[Manual-Cleanup] Triggering POST for: ${resolvedUrl}`);
        const postRes = await this.http.post(resolvedUrl, { headers: cleanupHeaders }).catch((err: any) => err.response || err);
        this.log(`[Manual-Cleanup] Result for ${resolvedUrl}: ${postRes.statusCode || postRes.status || 'Unknown Status'}`);
    });
});

Then('I schedule a DELETE for {string}', function (this: TestWorld, url: string) {
    const resolvedUrl = this.resolveVariables(url);
    this.savedValues.cleanUpTasks.push(async () => {
        const cleanupHeaders = this.authHeader ?? {};
        this.log(`[Manual-Cleanup] Triggering DELETE for: ${resolvedUrl}`);
        const delRes = await this.http.delete(resolvedUrl, { headers: cleanupHeaders }).catch((err: any) => err.response || err);
        this.log(`[Manual-Cleanup] Result for ${resolvedUrl}: ${delRes.statusCode || delRes.status || 'Unknown Status'}`);
    });
});
