import { test, expect } from "@playwright/test";

test.describe("Signup flow", () => {
  const email = `user_${Date.now()}@example.com`;
  const password = "Password1!";

  test("successful signup redirects to login", async ({ page, request }) => {
    await page.goto("http://localhost:5173/sign-up");

    try {
      await page.goto("http://localhost:5173/sign-up");

      await page.fill('input[name="email"]', email);
      await page.fill('input[name="password"]', password);
      await page.fill('input[name="confirm-password"]', password);

      await page.click('button[type="submit"]');

      await expect(page).toHaveURL(/\/login$/);
    } finally {
      // cleanup: delete the created user
      await request.post(
        "http://localhost:8080/api/auth/delete-user-by-email",
        {
          headers: {
            "Content-Type": "application/json",
          },
          data: {
            email,
          },
        },
      );
    }
  });

  test("shows password validation error", async ({ page }) => {
    await page.goto("http://localhost:5173/sign-up");

    await page.fill('input[name="password"]', "weak");
    await page.fill('input[name="confirm-password"]', "weak");

    await expect(page.locator(".error-message")).toBeVisible();
  });

  test("shows password mismatch error", async ({ page }) => {
    await page.goto("http://localhost:5173/sign-up");

    await page.fill('input[name="password"]', "Password1!");
    await page.fill('input[name="confirm-password"]', "Password2!");

    await expect(page.locator(".signup-error-message")).toContainText(
      "Passwords must match",
    );
  });

  test("shows backend error on duplicate email", async ({ page }) => {
    await page.goto("http://localhost:5173/sign-up");

    await page.fill('input[name="email"]', "existing@example.com");
    await page.fill('input[name="password"]', "Password1!");
    await page.fill('input[name="confirm-password"]', "Password1!");

    await page.click('button[type="submit"]');

    await expect(page.locator(".signup-error-message")).toBeVisible();
  });
});
