import "dotenv/config";
import path from "node:path";

const projectRoot = path.resolve(import.meta.dirname, "../..");

export const config = {
  apiPort: Number(process.env.PORT ?? 3001),
  gamePort: Number(process.env.GAME_PORT ?? 2567),
  clientOrigin: process.env.CLIENT_ORIGIN ?? "http://localhost:3000",
  databaseUrl: process.env.DATABASE_URL ?? "mysql://root@127.0.0.1:3306/avatar",
  jwtSecret: process.env.JWT_SECRET ?? "development-only-secret-change-me",
  resourceRoot: path.resolve(projectRoot, process.env.RESOURCE_ROOT ?? "res"),
};

if (config.jwtSecret.length < 24) {
  throw new Error("JWT_SECRET must contain at least 24 characters.");
}
