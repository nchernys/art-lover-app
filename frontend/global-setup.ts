import dotenv from "dotenv";
dotenv.config();
/// <reference types="node" />

import { chromium } from "@playwright/test";

export default async function globalSetup() {
  const browser = await chromium.launch();
  const context = await browser.newContext();
  const page = await context.newPage();

  const email = process.env.E2E_USER_EMAIL!;
  const password = process.env.E2E_USER_PASSWORD!;

  const response = await page.request.post(
    "http://localhost:8080/api/auth/login",
    {
      headers: { "Content-Type": "application/json" },
      data: { email, password },
    },
  );

  if (!response.ok()) {
    throw new Error("API login failed");
  }

  await context.storageState({ path: "auth.json" });

  await browser.close();
}
