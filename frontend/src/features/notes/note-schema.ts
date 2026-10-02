import { z } from "zod";

export const noteSchema = z.object({
  title: z.string().trim().min(1, "El título es obligatorio").max(200, "El título es demasiado largo"),
  body: z
    .string()
    .trim()
    .min(1, "El contenido es obligatorio")
    .max(10000, "El contenido es demasiado largo"),
});

export type NoteFormValues = z.infer<typeof noteSchema>;
