import "dotenv/config";
import { createHash, timingSafeEqual } from "node:crypto";
import { readFile } from "node:fs/promises";
import path from "node:path";
import { Elysia, t } from "elysia";
import { cors } from "@elysia/cors";
import type { RowDataPacket } from "mysql2/promise";
import { createAccessToken, verifyAccessToken } from "./auth/token.js";
import { config } from "./config.js";
import { pool } from "./db/pool.js";
import { crops } from "./farm/catalog.js";
import { TownRepository } from "./town/repository.js";

type UserRow = RowDataPacket & {
  id: number;
  username: string;
  password: string;
  active: number;
  ban: string | null;
};

type PlayerRow = RowDataPacket & {
  id: number;
  xu: number;
  exp_farm: number;
};

function passwordMatches(password: string, storedHash: string): boolean {
  const supplied = createHash("md5").update(password).digest();
  const stored = Buffer.from(storedHash, "hex");
  return stored.length === supplied.length && timingSafeEqual(stored, supplied);
}

function isCurrentlyBanned(value: string | null): boolean {
  if (!value) return false;
  try {
    const ban = JSON.parse(value) as { type?: number; forever?: unknown; minutes?: number; start?: string };
    if (ban.type !== 2) return false;
    if (ban.forever !== undefined) return true;
    const startedAt = ban.start ? new Date(ban.start).getTime() : Number.NaN;
    return Number.isFinite(startedAt) && startedAt + Number(ban.minutes ?? 0) * 60_000 > Date.now();
  } catch {
    return true;
  }
}

const allowedPacks = new Set(["farm", "bigFarm"]);
const allowedResolutions = new Set(["hd", "medium"]);
const town = new TownRepository();

async function identityFromHeader(authorization?: string) {
  const token = authorization?.match(/^Bearer\s+(.+)$/i)?.[1];
  if (!token) throw new Error("Bạn cần đăng nhập để vào thị trấn.");
  return verifyAccessToken(token);
}

export const api = new Elysia({ adapter: (await import("@elysia/node")).node() })
  .use(cors({ origin: config.clientOrigin, allowedHeaders: ["content-type", "authorization"] }))
  .get("/health", () => ({ ok: true, service: "avatar-town-api" }))
  .post(
    "/auth/login",
    async ({ body, set }) => {
      const [users] = await pool.execute<UserRow[]>(
        "SELECT id, username, password, active, ban FROM users WHERE username = ? LIMIT 1",
        [body.username.trim()],
      );
      const user = users[0];
      if (!user || user.active !== 1 || !passwordMatches(body.password, user.password) || isCurrentlyBanned(user.ban)) {
        set.status = 401;
        return { error: "Tên đăng nhập hoặc mật khẩu không đúng, hoặc tài khoản chưa được kích hoạt." };
      }

      const [players] = await pool.execute<PlayerRow[]>(
        "SELECT id, xu, exp_farm FROM players WHERE user_id = ? LIMIT 1",
        [user.id],
      );
      let player = players[0];
      if (!player) {
        await pool.execute(
          "INSERT INTO players (user_id, level_main, exp_main, exp_farm, chests, wearing) VALUES (?, 1, 0, 0, '[]', '[]')",
          [user.id],
        );
        const [createdPlayers] = await pool.execute<PlayerRow[]>(
          "SELECT id, xu, exp_farm FROM players WHERE user_id = ? LIMIT 1",
          [user.id],
        );
        player = createdPlayers[0];
      }

      const token = await createAccessToken({ userId: user.id, username: user.username });
      return {
        token,
        user: { id: user.id, username: user.username },
        player: { xu: player?.xu ?? 0, farmExperience: player?.exp_farm ?? 0 },
      };
    },
    { body: t.Object({ username: t.String({ minLength: 1, maxLength: 20 }), password: t.String({ minLength: 1, maxLength: 128 }) }) },
  )
  .get("/farm/catalog", () => ({ crops }))
  .get("/town/state", async ({ headers, set }) => {
    try {
      const identity = await identityFromHeader(headers.authorization);
      return await town.snapshot(identity.userId);
    } catch (error) {
      set.status = 401;
      return { error: error instanceof Error ? error.message : "Không thể tải dữ liệu thị trấn." };
    }
  })
  .post("/town/restock", async ({ headers, body, set }) => {
    try {
      const identity = await identityFromHeader(headers.authorization);
      return await town.restock(identity.userId, body.productId, body.quantity);
    } catch (error) {
      set.status = 400;
      return { error: error instanceof Error ? error.message : "Không thể nhập hàng." };
    }
  }, { body: t.Object({ productId: t.String({ minLength: 1, maxLength: 32 }), quantity: t.Integer({ minimum: 1, maximum: 10 }) }) })
  .post("/town/serve", async ({ headers, body, set }) => {
    try {
      const identity = await identityFromHeader(headers.authorization);
      return await town.serveCustomer(identity.userId, body.productId);
    } catch (error) {
      set.status = 400;
      return { error: error instanceof Error ? error.message : "Không thể phục vụ khách." };
    }
  }, { body: t.Object({ productId: t.String({ minLength: 1, maxLength: 32 }) }) })
  .post("/town/unlock", async ({ headers, body, set }) => {
    try {
      const identity = await identityFromHeader(headers.authorization);
      return await town.unlockShop(identity.userId, body.shopId);
    } catch (error) {
      set.status = 400;
      return { error: error instanceof Error ? error.message : "Không thể mở cửa hàng." };
    }
  }, { body: t.Object({ shopId: t.String({ minLength: 1, maxLength: 32 }) }) })
  .get("/assets/:resolution/:pack/:filename", async ({ params, set }) => {
    if (
      !allowedResolutions.has(params.resolution) ||
      !(allowedPacks.has(params.pack) || (params.resolution === "hd" && params.pack === "object")) ||
      !/^\d+\.png$/.test(params.filename)
    ) {
      set.status = 404;
      return { error: "Không tìm thấy tài nguyên." };
    }

    const assetPath = path.resolve(config.resourceRoot, params.resolution, params.pack, params.filename);
    try {
      const data = await readFile(assetPath);
      return new Response(new Uint8Array(data), {
        headers: { "content-type": "image/png", "cache-control": "public, max-age=3600" },
      });
    } catch {
      set.status = 404;
      return { error: "Không tìm thấy tài nguyên." };
    }
  });
