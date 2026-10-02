import { describe, expect, it } from "vitest";

import { useUiStore } from "@/lib/ui-store";

describe("useUiStore", () => {
  it("alterna el tema", () => {
    useUiStore.setState({ theme: "light" });
    useUiStore.getState().toggleTheme();
    expect(useUiStore.getState().theme).toBe("dark");
    useUiStore.getState().toggleTheme();
    expect(useUiStore.getState().theme).toBe("light");
  });
});
