import { client } from "@/client/client.gen";

/**
 * El access token de AuthKit viaja como Bearer. El cliente generado lo pide en cada llamada.
 */
export function configureApi(getAccessToken: () => Promise<string>) {
  client.setConfig({
    baseUrl: "",
    auth: async () => getAccessToken(),
  });
}
