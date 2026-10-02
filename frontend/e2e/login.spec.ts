import { expect, test } from "@playwright/test";

test("la página de acceso se muestra sin WorkOS", async ({ page }) => {
  await page.goto("/");
  await expect(page.getByRole("heading", { name: "Iniciar sesión" })).toBeVisible();
});
