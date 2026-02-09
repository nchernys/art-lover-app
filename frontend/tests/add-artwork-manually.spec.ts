import { test, expect } from "@playwright/test";

test("uploads artwork with existing artist", async ({ page }) => {
  await page.goto("http://localhost:5173/upload");

  await page.waitForResponse("**/api/artists");

  const artistSelect = page.locator('select[name="artistId"]');
  await artistSelect.selectOption({ index: 1 });

  const fileInput = page.locator('input[name="image"]');
  await fileInput.setInputFiles("tests/fixtures/test-picture.jpg");

  await page.fill('input[name="title"]', "Amazing Artwork");
  await page.fill('input[name="year"]', "1900");
  await page.fill('input[name="movement"]', "Impressionism");
  await page.fill('textarea[name="description"]', "Test artwork");

  const submitButton = page.locator('button[type="submit"]');

  const saveRequestPromise = page.waitForRequest("**/api/save");

  await submitButton.click();

  const saveRequest = await saveRequestPromise;
  const saveResponse = await saveRequest.response();

  expect(saveResponse?.status()).toBe(200);
});

test("uploads artwork with custom artist (Other)", async ({ page }) => {
  await page.goto("http://localhost:5173/upload");

  await page.waitForResponse("**/api/artists");

  const artistSelect = page.locator('select[name="artistId"]');
  await artistSelect.selectOption("__OTHER__");

  await page.fill('input[name="artist"]', "Unknown Artist");

  const fileInput = page.locator('input[name="image"]');
  await fileInput.setInputFiles("tests/fixtures/test-picture.jpg");

  await page.fill('input[name="title"]', "Other Artist Artwork");
  await page.fill('input[name="year"]', "1200");
  await page.fill('input[name="movement"]', "Medieval");
  await page.fill('textarea[name="description"]', "Artwork with custom artist");

  const submitButton = page.locator('button[type="submit"]');

  const saveRequestPromise = page.waitForRequest("**/api/save");

  await submitButton.click();

  const saveRequest = await saveRequestPromise;
  const saveResponse = await saveRequest.response();

  expect(saveResponse?.status()).toBe(200);
});
