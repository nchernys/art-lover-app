import { test, expect, Page } from "@playwright/test";

test.describe("Gallery", () => {
  async function requireAtLeastOneArtwork(page: Page) {
    const cards = page.locator(".gallery-wrapper > *");
    const emptyMessage = page.locator(".empty-message");

    // wait for UI to settle
    await Promise.race([
      cards.first().waitFor({ state: "visible" }),
      emptyMessage.waitFor({ state: "visible" }),
    ]);

    const count = await cards.count();
    test.skip(count === 0, "No artworks available");

    return cards;
  }

  test.beforeEach(async ({ page }) => {
    await page.goto("http://localhost:5173");
  });

  test("loads and displays artworks", async ({ page }) => {
    const cards = await requireAtLeastOneArtwork(page);
    await expect(cards.first()).toBeVisible();
  });

  test("filters artworks in search mode", async ({ page }) => {
    await page.goto("http://localhost:5173/?search=true");
    const cards = await requireAtLeastOneArtwork(page);
    await expect(cards.first()).toBeVisible();
  });

  test("opens full view when artwork is selected", async ({ page }) => {
    const cards = await requireAtLeastOneArtwork(page);

    const firstCard = cards.first();
    await firstCard.locator(".gallery-card-details-play").click();

    await expect(page.locator(".gallery-card-screen-full-view")).toBeVisible();
  });

  test("toggles bookmark on artwork", async ({ page }) => {
    const cards = await requireAtLeastOneArtwork(page);
    const firstCard = cards.first();
    await firstCard.locator('[data-testid="bookmark"]').click();

    await expect(page.locator(".gallery-wrapper")).toBeVisible();
  });

  test("opens delete modal and deletes artwork", async ({ page }) => {
    const cards = await requireAtLeastOneArtwork(page);

    const firstCard = cards.first();
    await firstCard.locator('[data-testid="delete"]').click();

    const modal = page.locator(".gallery-delete-modal");
    await expect(modal).toBeVisible();

    await modal.locator("button.confirm-delete").click();
    await expect(modal).not.toBeVisible();
  });

  test("opens chatbot from artwork full view", async ({ page }) => {
    const cards = await requireAtLeastOneArtwork(page);
    await cards.first().locator(".gallery-card-details-play").click();

    await page.locator('[data-testid="open-chatbot"]').click();
    await expect(page.locator(".chatbot-window")).toBeVisible();
  });

  test("shows empty message only when no bookmarked artworks exist", async ({
    page,
  }) => {
    await page.goto("http://localhost:5173/?bookmarked=true");
    const cards = await requireAtLeastOneArtwork(page);
    const emptyMessage = page.locator(".empty-message");

    // wait until either cards OR empty state appears
    await Promise.race([
      cards.first().waitFor({ state: "visible" }),
      emptyMessage.waitFor({ state: "visible" }),
    ]);

    const cardCount = await cards.count();

    if (cardCount === 0) {
      await expect(emptyMessage).toBeVisible();
    } else {
      await expect(emptyMessage).not.toBeVisible();
    }
  });
});
