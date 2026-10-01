import "dotenv/config";
import { defineRoom, defineServer } from "colyseus";
import { api } from "./api.js";
import { closeDatabase } from "./db/pool.js";
import { config } from "./config.js";
import { FarmRoom } from "./farm/room.js";

const gameServer = defineServer({
  rooms: {
    farm: defineRoom(FarmRoom).filterBy(["ownerId"]),
  },
});

await gameServer.listen(config.gamePort);
const apiServer = api.listen(config.apiPort);

console.info(`Farm API listening on http://localhost:${config.apiPort}`);
console.info(`Colyseus farm rooms listening on http://localhost:${config.gamePort}`);

async function shutdown(): Promise<void> {
  await gameServer.gracefullyShutdown();
  apiServer.stop();
  await closeDatabase();
}

process.once("SIGINT", () => void shutdown());
process.once("SIGTERM", () => void shutdown());
