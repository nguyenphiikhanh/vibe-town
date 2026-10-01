import type { RowDataPacket } from "mysql2/promise";
import { pool } from "../db/pool.js";
import { townProducts, townShops } from "./catalog.js";

type EconomyRow = RowDataPacket & { coins: number; reputation: number; total_sales: number };
type ShopRow = RowDataPacket & { shop_id: string; level: number };
type StockRow = RowDataPacket & { product_id: string; stock: number };

export type TownSnapshot = {
  economy: { coins: number; reputation: number; totalSales: number };
  shops: Array<{ id: string; name: string; objectId: number; price: number; unlocked: boolean; level: number }>;
  products: Array<{ id: string; name: string; buyPrice: number; sellPrice: number; stock: number }>;
};

export class TownRepository {
  async initialize(userId: number): Promise<void> {
    const connection = await pool.getConnection();
    try {
      await connection.beginTransaction();
      await connection.execute(
        "INSERT IGNORE INTO town_economy (user_id, coins, reputation, total_sales) VALUES (?, 500, 0, 0)",
        [userId],
      );
      await connection.execute(
        "INSERT IGNORE INTO town_businesses (user_id, shop_id, level) VALUES (?, 'grocery', 1)",
        [userId],
      );
      for (const product of townProducts) {
        await connection.execute(
          "INSERT IGNORE INTO town_inventory (user_id, product_id, stock) VALUES (?, ?, 3)",
          [userId, product.id],
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

  async snapshot(userId: number): Promise<TownSnapshot> {
    await this.initialize(userId);
    const [[economies], [businesses], [inventory]] = await Promise.all([
      pool.execute<EconomyRow[]>("SELECT coins, reputation, total_sales FROM town_economy WHERE user_id = ?", [userId]),
      pool.execute<ShopRow[]>("SELECT shop_id, level FROM town_businesses WHERE user_id = ?", [userId]),
      pool.execute<StockRow[]>("SELECT product_id, stock FROM town_inventory WHERE user_id = ?", [userId]),
    ]);
    const shopById = new Map(businesses.map((shop) => [shop.shop_id, shop]));
    const stockById = new Map(inventory.map((item) => [item.product_id, item.stock]));
    const economy = economies[0];
    return {
      economy: { coins: economy.coins, reputation: economy.reputation, totalSales: economy.total_sales },
      shops: townShops.map((shop) => ({
        ...shop,
        unlocked: shopById.has(shop.id),
        level: shopById.get(shop.id)?.level ?? 0,
      })),
      products: townProducts.map((product) => ({ ...product, stock: stockById.get(product.id) ?? 0 })),
    };
  }

  async restock(userId: number, productId: string, quantity: number): Promise<TownSnapshot> {
    const product = townProducts.find((item) => item.id === productId);
    if (!product) throw new Error("Mặt hàng này không có trong danh mục.");
    if (!Number.isInteger(quantity) || quantity < 1 || quantity > 10) throw new Error("Mỗi lần nhập từ 1 đến 10 món.");
    await this.initialize(userId);
    const connection = await pool.getConnection();
    try {
      await connection.beginTransaction();
      const [rows] = await connection.execute<EconomyRow[]>("SELECT coins, reputation, total_sales FROM town_economy WHERE user_id = ? FOR UPDATE", [userId]);
      const cost = product.buyPrice * quantity;
      if (!rows[0] || rows[0].coins < cost) throw new Error("Không đủ xu để nhập hàng.");
      await connection.execute("UPDATE town_economy SET coins = coins - ? WHERE user_id = ?", [cost, userId]);
      await connection.execute("UPDATE town_inventory SET stock = stock + ? WHERE user_id = ? AND product_id = ?", [quantity, userId, productId]);
      await connection.commit();
    } catch (error) {
      await connection.rollback();
      throw error;
    } finally {
      connection.release();
    }
    return this.snapshot(userId);
  }

  async serveCustomer(userId: number, productId: string): Promise<{ snapshot: TownSnapshot; earned: number }> {
    const product = townProducts.find((item) => item.id === productId);
    if (!product) throw new Error("Khách không mua mặt hàng này.");
    await this.initialize(userId);
    const connection = await pool.getConnection();
    try {
      await connection.beginTransaction();
      const [stockRows] = await connection.execute<StockRow[]>("SELECT product_id, stock FROM town_inventory WHERE user_id = ? AND product_id = ? FOR UPDATE", [userId, productId]);
      if (!stockRows[0] || stockRows[0].stock < 1) throw new Error("Mặt hàng đã hết. Hãy nhập thêm trước nhé.");
      await connection.execute("UPDATE town_inventory SET stock = stock - 1 WHERE user_id = ? AND product_id = ?", [userId, productId]);
      await connection.execute("UPDATE town_economy SET coins = coins + ?, reputation = LEAST(reputation + 1, 9999), total_sales = total_sales + 1 WHERE user_id = ?", [product.sellPrice, userId]);
      await connection.commit();
    } catch (error) {
      await connection.rollback();
      throw error;
    } finally {
      connection.release();
    }
    return { snapshot: await this.snapshot(userId), earned: product.sellPrice };
  }

  async unlockShop(userId: number, shopId: string): Promise<TownSnapshot> {
    const shop = townShops.find((item) => item.id === shopId);
    if (!shop) throw new Error("Cửa hàng không hợp lệ.");
    await this.initialize(userId);
    const connection = await pool.getConnection();
    try {
      await connection.beginTransaction();
      const [economies] = await connection.execute<EconomyRow[]>("SELECT coins, reputation, total_sales FROM town_economy WHERE user_id = ? FOR UPDATE", [userId]);
      const [businesses] = await connection.execute<ShopRow[]>("SELECT shop_id, level FROM town_businesses WHERE user_id = ? AND shop_id = ? FOR UPDATE", [userId, shopId]);
      if (businesses[0]) throw new Error("Cửa hàng này đã được mở rồi.");
      if (!economies[0] || economies[0].coins < shop.price) throw new Error("Chưa đủ xu để mở cửa hàng này.");
      await connection.execute("UPDATE town_economy SET coins = coins - ? WHERE user_id = ?", [shop.price, userId]);
      await connection.execute("INSERT INTO town_businesses (user_id, shop_id, level) VALUES (?, ?, 1)", [userId, shopId]);
      await connection.commit();
    } catch (error) {
      await connection.rollback();
      throw error;
    } finally {
      connection.release();
    }
    return this.snapshot(userId);
  }
}
