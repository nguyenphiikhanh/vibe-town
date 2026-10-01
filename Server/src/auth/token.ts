import { createSecretKey } from "node:crypto";
import { SignJWT, jwtVerify } from "jose";
import { config } from "../config.js";

const secret = createSecretKey(Buffer.from(config.jwtSecret, "utf8"));

export type FarmIdentity = {
  userId: number;
  username: string;
};

export async function createAccessToken(identity: FarmIdentity): Promise<string> {
  return new SignJWT({ username: identity.username })
    .setProtectedHeader({ alg: "HS256" })
    .setSubject(String(identity.userId))
    .setIssuedAt()
    .setExpirationTime("2h")
    .sign(secret);
}

export async function verifyAccessToken(token: string): Promise<FarmIdentity> {
  const { payload } = await jwtVerify(token, secret);
  const userId = Number(payload.sub);
  if (!Number.isSafeInteger(userId) || userId <= 0 || typeof payload.username !== "string") {
    throw new Error("Phiên đăng nhập không hợp lệ.");
  }

  return { userId, username: payload.username };
}
