import { bigint, int, mysqlTable, primaryKey, smallint, varchar } from "drizzle-orm/mysql-core";

// Avatar owns this legacy table. This minimal declaration exists only so farm
// tables can express their foreign keys; farm migrations must never create it.
export const users = mysqlTable("users", {
  id: int("id").autoincrement().primaryKey(),
});

export const farmPlots = mysqlTable(
  "farm_plots",
  {
    userId: int("user_id")
      .notNull()
      .references(() => users.id, { onDelete: "cascade" }),
    plotIndex: smallint("plot_index", { unsigned: true }).notNull(),
    cropId: varchar("crop_id", { length: 32 }),
    plantedAt: bigint("planted_at", { mode: "number" }),
    readyAt: bigint("ready_at", { mode: "number" }),
  },
  (table) => [primaryKey({ columns: [table.userId, table.plotIndex] })],
);

export const farmInventory = mysqlTable(
  "farm_inventory",
  {
    userId: int("user_id")
      .notNull()
      .references(() => users.id, { onDelete: "cascade" }),
    cropId: varchar("crop_id", { length: 32 }).notNull(),
    seeds: int("seeds", { unsigned: true }).notNull().default(0),
    harvested: int("harvested", { unsigned: true }).notNull().default(0),
  },
  (table) => [primaryKey({ columns: [table.userId, table.cropId] })],
);

export const townEconomy = mysqlTable("town_economy", {
  userId: int("user_id")
    .notNull()
    .primaryKey()
    .references(() => users.id, { onDelete: "cascade" }),
  coins: int("coins", { unsigned: true }).notNull().default(500),
  reputation: smallint("reputation", { unsigned: true }).notNull().default(0),
  totalSales: int("total_sales", { unsigned: true }).notNull().default(0),
});

export const townBusinesses = mysqlTable(
  "town_businesses",
  {
    userId: int("user_id")
      .notNull()
      .references(() => users.id, { onDelete: "cascade" }),
    shopId: varchar("shop_id", { length: 32 }).notNull(),
    level: smallint("level", { unsigned: true }).notNull().default(1),
  },
  (table) => [primaryKey({ columns: [table.userId, table.shopId] })],
);

export const townInventory = mysqlTable(
  "town_inventory",
  {
    userId: int("user_id")
      .notNull()
      .references(() => users.id, { onDelete: "cascade" }),
    productId: varchar("product_id", { length: 32 }).notNull(),
    stock: int("stock", { unsigned: true }).notNull().default(0),
  },
  (table) => [primaryKey({ columns: [table.userId, table.productId] })],
);
