import tailwindcss from "@tailwindcss/vite";
import { tanstackRouter } from "@tanstack/router-plugin/vite";
import react from "@vitejs/plugin-react";
import { defineConfig } from "vitest/config";

export default defineConfig({
  plugins: [
    tanstackRouter({
      target: "react",
      autoCodeSplitting: true,
    }),
    react(),
    tailwindcss(),
  ],
  resolve: {
    alias: {
      "@": new URL("./src", import.meta.url).pathname,
    },
  },
  server: {
    port: 5173,
    proxy: {
      "/api": "http://127.0.0.1:8080",
      "/v3": "http://127.0.0.1:8080",
      "/swagger-ui": "http://127.0.0.1:8080",
      "/actuator": "http://127.0.0.1:8080",
    },
  },
  test: {
    environment: "node",
    include: ["src/**/*.test.ts"],
  },
});
