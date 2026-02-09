import { test, expect } from "@playwright/test";
import dotenv from "dotenv";
dotenv.config();

test.describe("Login flow", () => {
  const email = process.env.E2E_USER_EMAIL!;
  const password = process.env.E2E_USER_PASSWORD!;

  test("successful login redirects to home", async ({ page }) => {
    await page.goto("http://localhost:5173/login");

    await page.fill('input[name="email"]', email);
    await page.fill('input[name="password"]', password);
    await page.click('button[type="submit"]');

    await expect(page).toHaveURL("http://localhost:5173/");
  });

  test("shows error message on invalid credentials", async ({ page }) => {
    await page.goto("http://localhost:5173/login");

    await page.fill('input[name="email"]', "wrong@example.com");
    await page.fill('input[name="password"]', "WrongPassword!");
    await page.click('button[type="submit"]');

    await expect(page.locator(".login-error")).toContainText(
      "Login failed. Please try again.",
    );
  });

  test("clears error message when user edits input", async ({ page }) => {
    await page.goto("http://localhost:5173/login");

    await page.fill('input[name="email"]', "wrong@example.com");
    await page.fill('input[name="password"]', "WrongPassword!");
    await page.click('button[type="submit"]');

    await expect(page.locator(".login-error")).toBeVisible();

    await page.fill('input[name="email"]', email);

    await expect(page.locator(".login-error")).toHaveText("");
  });
});
