import { test } from "@playwright/test";

test("test", async ({ page }) => {
  await page.goto("http://localhost:5173/login");
  await page.locator('input[name="email"]').click();
  await page.locator('input[name="email"]').click();
  await page.locator('input[name="email"]').fill("chernysn@gmail.com");
  await page.locator('input[name="email"]').press("Tab");
  await page.locator('input[name="password"]').fill("Voloshin=00");
  await page.getByRole("button", { name: "Submit" }).click();

  await page.waitForSelector(".gallery-wrapper");

  await page.context().storageState({ path: "auth.json" });
});

// npx playwright test tests/auth.setup.spec.ts --headed
