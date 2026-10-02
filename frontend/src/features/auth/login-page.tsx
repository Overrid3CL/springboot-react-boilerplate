import { useEffect } from "react";
import { useNavigate } from "@tanstack/react-router";
import { useAuth } from "@workos-inc/authkit-react";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader } from "@/components/ui/card";

export function LoginPage() {
  const { isLoading, user, signIn, signUp } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (user) {
      void navigate({ to: "/notes" });
    }
  }, [navigate, user]);

  return (
    <main className="mx-auto flex min-h-screen max-w-lg flex-col justify-center px-6">
      <Card>
        <CardHeader>
          <p className="text-sm font-medium text-muted-foreground">Boilerplate</p>
          <h1 className="text-3xl font-semibold tracking-tight">Iniciar sesión</h1>
          <p className="text-sm text-muted-foreground">
            Entra con tu cuenta de la organización para ver las notas de tu equipo.
          </p>
        </CardHeader>
        <CardContent className="flex flex-col gap-3">
          <Button disabled={isLoading} onClick={() => void signIn()}>
            Iniciar sesión
          </Button>
          <Button variant="outline" disabled={isLoading} onClick={() => void signUp()}>
            Crear cuenta
          </Button>
        </CardContent>
      </Card>
    </main>
  );
}
