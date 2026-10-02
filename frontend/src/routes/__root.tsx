import { useEffect, useState } from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { createRootRoute, Outlet } from "@tanstack/react-router";
import { AuthKitProvider, useAuth } from "@workos-inc/authkit-react";

import { configureApi } from "@/lib/api";
import { useUiStore } from "@/lib/ui-store";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: false,
    },
  },
});

interface PublicConfig {
  clientId: string;
  apiHostname: string | null;
  redirectUri: string | null;
}

function ThemeSync() {
  const theme = useUiStore((state) => state.theme);
  useEffect(() => {
    document.documentElement.classList.toggle("dark", theme === "dark");
  }, [theme]);
  return null;
}

function ApiBridge() {
  const { getAccessToken } = useAuth();
  useEffect(() => {
    configureApi(getAccessToken);
  }, [getAccessToken]);
  return <Outlet />;
}

function AuthenticatedShell({ config }: { config: PublicConfig }) {
  const redirectUri = config.redirectUri ?? window.location.origin;
  return (
    <AuthKitProvider
      clientId={config.clientId}
      apiHostname={config.apiHostname ?? undefined}
      redirectUri={redirectUri}
      devMode={window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1"}
    >
      <QueryClientProvider client={queryClient}>
        <ThemeSync />
        <ApiBridge />
      </QueryClientProvider>
    </AuthKitProvider>
  );
}

function Root() {
  const [config, setConfig] = useState<PublicConfig | null>(null);
  const [failed, setFailed] = useState(false);

  useEffect(() => {
    const controller = new AbortController();
    const timer = window.setTimeout(() => controller.abort(), 2500);
    fetch("/api/config", { signal: controller.signal })
      .then((response) => {
        if (!response.ok) {
          throw new Error("config");
        }
        return response.json() as Promise<PublicConfig>;
      })
      .then((value) => {
        if (!value.clientId) {
          throw new Error("client");
        }
        setConfig(value);
      })
      .catch(() => setFailed(true))
      .finally(() => window.clearTimeout(timer));
    return () => {
      window.clearTimeout(timer);
      controller.abort();
    };
  }, []);

  if (!config) {
    return (
      <main className="mx-auto flex min-h-screen max-w-lg flex-col justify-center gap-4 px-6">
        <h1 className="text-3xl font-semibold tracking-tight">Iniciar sesión</h1>
        <p className="text-muted-foreground">
          {failed
            ? "No pudimos leer la configuración de WorkOS. Revisa que el backend esté en marcha y que WORKOS_CLIENT_ID esté definido."
            : "Cargando configuración…"}
        </p>
      </main>
    );
  }

  return <AuthenticatedShell config={config} />;
}

export const Route = createRootRoute({
  component: Root,
});
