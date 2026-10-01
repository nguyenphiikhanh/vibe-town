import "dotenv/config";
import mysql from "mysql2/promise";
import { config } from "../config.js";

export const pool = mysql.createPool(config.databaseUrl);

export async function closeDatabase(): Promise<void> {
  await pool.end();
}
