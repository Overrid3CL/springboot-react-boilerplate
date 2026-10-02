import { describe, expect, it } from "vitest";

import { noteSchema } from "@/features/notes/note-schema";

describe("noteSchema", () => {
  it("exige título y contenido", () => {
    expect(noteSchema.safeParse({ title: "  ", body: "" }).success).toBe(false);
    expect(noteSchema.safeParse({ title: "Reunión", body: "Notas del lunes" }).success).toBe(true);
  });
});
