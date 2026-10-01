import type { RowDataPacket } from "mysql2/promise";
import { pool } from "../db/pool.js";
import { crops, findCrop, plotCount, type CropDefinition } from "./catalog.js";

type PlotRow = RowDataPacket & {
  plot_index: number;
  crop_id: string | null;
  planted_at: number | null;
  ready_at: number | null;
};

type InventoryRow = RowDataPacket & {
  crop_id: string;
  seeds: number;
  harvested: number;
};

const startingSeeds: Record<string, number> = {
  watermelon: 5,
  pineapple: 5,
  grapes: 5,
  sunflower: 5,
};

export type FarmSnapshot = {
  plots: Array<{
    index: number;
    cropId: string | null;
    plantedAt: number | null;
    readyAt: number | null;
    phase: "empty" | "growing" | "ready";
    remainingSeconds: number;
  }>;
  inventory: Array<{ cropId: string; seeds: number; harvested: number }>;
  crops: CropDefinition[];
  serverTime: number;
};

export class FarmRepository {
  async initialize(userId: number): Promise<void> {
    const connection = await pool.getConnection();
    try {
      await connection.beginTransaction();
      const plotValues = Array.from({ length: plotCount }, (_, index) => [userId, index + 1]);
      const plotPlaceholders = plotValues.map(() => "(?, ?)").join(", ");
      await connection.query(
        `INSERT IGNORE INTO farm_plots (user_id, plot_index) VALUES ${plotPlaceholders}`,
        plotValues.flat(),
      );

      for (const crop of crops) {
        await connection.execute(
          "INSERT IGNORE INTO farm_inventory (user_id, crop_id, seeds, harvested) VALUES (?, ?, ?, 0)",
          [userId, crop.id, startingSeeds[crop.id] ?? 0],
        );
      }

      await connection.commit();
    } catch (error) {
      await connection.rollback();
      throw error;
    } finally {
      connection.release();
    }
  }

  async snapshot(userId: number): Promise<FarmSnapshot> {
    await this.initialize(userId);
    const [plotRows] = await pool.execute<PlotRow[]>(
      "SELECT plot_index, crop_id, planted_at, ready_at FROM farm_plots WHERE user_id = ? ORDER BY plot_index",
      [userId],
    );
    const [inventoryRows] = await pool.execute<InventoryRow[]>(
      "SELECT crop_id, seeds, harvested FROM farm_inventory WHERE user_id = ? ORDER BY crop_id",
      [userId],
    );
    const now = Date.now();

    return {
      plots: plotRows.map((plot) => {
        const readyAt = plot.ready_at === null ? null : Number(plot.ready_at);
        const phase = plot.crop_id === null ? "empty" : readyAt !== null && now >= readyAt ? "ready" : "growing";
        return {
          index: plot.plot_index,
          cropId: plot.crop_id,
          plantedAt: plot.planted_at === null ? null : Number(plot.planted_at),
          readyAt,
          phase,
          remainingSeconds: readyAt === null ? 0 : Math.max(0, Math.ceil((readyAt - now) / 1000)),
        };
      }),
      inventory: inventoryRows.map((item) => ({
        cropId: item.crop_id,
        seeds: item.seeds,
        harvested: item.harvested,
      })),
      crops,
      serverTime: now,
    };
  }

  async plant(userId: number, plotIndex: number, cropId: string): Promise<void> {
    const crop = findCrop(cropId);
    if (!crop) throw new Error("Loại hạt giống không hợp lệ.");
    if (!Number.isInteger(plotIndex) || plotIndex < 1 || plotIndex > plotCount) {
      throw new Error("Ô đất không hợp lệ.");
    }

    await this.initialize(userId);
    const connection = await pool.getConnection();
    try {
      await connection.beginTransaction();
      const [plotRows] = await connection.execute<PlotRow[]>(
        "SELECT plot_index, crop_id, planted_at, ready_at FROM farm_plots WHERE user_id = ? AND plot_index = ? FOR UPDATE",
        [userId, plotIndex],
      );
      const [inventoryRows] = await connection.execute<InventoryRow[]>(
        "SELECT crop_id, seeds, harvested FROM farm_inventory WHERE user_id = ? AND crop_id = ? FOR UPDATE",
        [userId, cropId],
      );
      if (!plotRows[0] || plotRows[0].crop_id !== null) throw new Error("Ô đất này đang được sử dụng.");
      if (!inventoryRows[0] || inventoryRows[0].seeds < 1) throw new Error("Bạn đã hết hạt giống này.");

      const plantedAt = Date.now();
      const readyAt = plantedAt + crop.growthSeconds * 1000;
      await connection.execute(
        "UPDATE farm_inventory SET seeds = seeds - 1 WHERE user_id = ? AND crop_id = ?",
        [userId, cropId],
      );
      await connection.execute(
        "UPDATE farm_plots SET crop_id = ?, planted_at = ?, ready_at = ? WHERE user_id = ? AND plot_index = ?",
        [cropId, plantedAt, readyAt, userId, plotIndex],
      );
      await connection.commit();
    } catch (error) {
      await connection.rollback();
      throw error;
    } finally {
      connection.release();
    }
  }

  async harvest(userId: number, plotIndex: number): Promise<void> {
    if (!Number.isInteger(plotIndex) || plotIndex < 1 || plotIndex > plotCount) {
      throw new Error("Ô đất không hợp lệ.");
    }

    await this.initialize(userId);
    const connection = await pool.getConnection();
    try {
      await connection.beginTransaction();
      const [plotRows] = await connection.execute<PlotRow[]>(
        "SELECT plot_index, crop_id, planted_at, ready_at FROM farm_plots WHERE user_id = ? AND plot_index = ? FOR UPDATE",
        [userId, plotIndex],
      );
      const plot = plotRows[0];
      if (!plot?.crop_id || !plot.ready_at || Number(plot.ready_at) > Date.now()) {
        throw new Error("Cây chưa sẵn sàng để thu hoạch.");
      }

      await connection.execute(
        "UPDATE farm_inventory SET harvested = harvested + 1 WHERE user_id = ? AND crop_id = ?",
        [userId, plot.crop_id],
      );
      await connection.execute(
        "UPDATE farm_plots SET crop_id = NULL, planted_at = NULL, ready_at = NULL WHERE user_id = ? AND plot_index = ?",
        [userId, plotIndex],
      );
      await connection.commit();
    } catch (error) {
      await connection.rollback();
      throw error;
    } finally {
      connection.release();
    }
  }
}
