import { useEffect } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "@tanstack/react-router";
import { useAuth } from "@workos-inc/authkit-react";
import { useForm } from "react-hook-form";

import {
  createNoteMutation,
  deleteNoteMutation,
  listNotesOptions,
  listNotesQueryKey,
} from "@/client/@tanstack/react-query.gen";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { noteSchema, type NoteFormValues } from "@/features/notes/note-schema";
import { useUiStore } from "@/lib/ui-store";

const dateFormat = new Intl.DateTimeFormat("es-CL", {
  dateStyle: "medium",
  timeStyle: "short",
});

export function NotesPage() {
  const { user, isLoading, signOut, organizationId } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const theme = useUiStore((state) => state.theme);
  const toggleTheme = useUiStore((state) => state.toggleTheme);
  const form = useForm<NoteFormValues>({
    resolver: zodResolver(noteSchema),
    defaultValues: { title: "", body: "" },
  });

  useEffect(() => {
    if (!isLoading && !user) {
      void navigate({ to: "/" });
    }
  }, [isLoading, navigate, user]);

  const notes = useQuery({
    ...listNotesOptions(),
    enabled: Boolean(user),
  });

  const createNote = useMutation({
    ...createNoteMutation(),
    onSuccess: async () => {
      form.reset();
      await queryClient.invalidateQueries({ queryKey: listNotesQueryKey() });
    },
  });

  const deleteNote = useMutation({
    ...deleteNoteMutation(),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: listNotesQueryKey() });
    },
  });

  if (isLoading || !user) {
    return <p className="p-6 text-sm text-muted-foreground">Cargando sesión…</p>;
  }

  return (
    <main className="mx-auto flex min-h-screen max-w-3xl flex-col gap-6 px-6 py-10">
      <header className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <p className="text-sm text-muted-foreground">Organización {organizationId ?? "activa"}</p>
          <h1 className="text-3xl font-semibold tracking-tight">Notas</h1>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" onClick={toggleTheme}>
            {theme === "light" ? "Tema oscuro" : "Tema claro"}
          </Button>
          <Button variant="ghost" onClick={() => signOut()}>
            Cerrar sesión
          </Button>
        </div>
      </header>

      <Card>
        <CardHeader>
          <h2 className="text-lg font-medium">Nueva nota</h2>
        </CardHeader>
        <CardContent>
          <form
            className="flex flex-col gap-4"
            onSubmit={form.handleSubmit((values) =>
              createNote.mutate({
                body: values,
              }),
            )}
          >
            <div className="flex flex-col gap-2">
              <Label htmlFor="title">Título</Label>
              <Input id="title" {...form.register("title")} />
              {form.formState.errors.title ? (
                <p className="text-sm text-destructive">{form.formState.errors.title.message}</p>
              ) : null}
            </div>
            <div className="flex flex-col gap-2">
              <Label htmlFor="body">Contenido</Label>
              <Textarea id="body" {...form.register("body")} />
              {form.formState.errors.body ? (
                <p className="text-sm text-destructive">{form.formState.errors.body.message}</p>
              ) : null}
            </div>
            <Button type="submit" disabled={createNote.isPending}>
              Guardar
            </Button>
            {createNote.isError ? (
              <p className="text-sm text-destructive">No se pudo guardar la nota.</p>
            ) : null}
          </form>
        </CardContent>
      </Card>

      <section className="flex flex-col gap-3">
        {notes.isLoading ? <p className="text-sm text-muted-foreground">Cargando notas…</p> : null}
        {notes.isError ? <p className="text-sm text-destructive">No se pudieron cargar las notas.</p> : null}
        {notes.data?.length === 0 ? (
          <p className="text-sm text-muted-foreground">Todavía no hay notas en esta organización.</p>
        ) : null}
        {notes.data?.map((note) => (
          <Card key={note.id}>
            <CardHeader className="flex-row items-start justify-between">
              <div>
                <h2 className="text-lg font-medium">{note.title}</h2>
                <p className="text-xs text-muted-foreground">
                  {note.createdAt ? dateFormat.format(new Date(note.createdAt)) : ""}
                </p>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={() =>
                  note.id
                    ? deleteNote.mutate({
                        path: { id: note.id },
                      })
                    : undefined
                }
              >
                Eliminar
              </Button>
            </CardHeader>
            <CardContent>
              <p className="whitespace-pre-wrap text-sm">{note.body}</p>
            </CardContent>
          </Card>
        ))}
      </section>
    </main>
  );
}
