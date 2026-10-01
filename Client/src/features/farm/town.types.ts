import type { FarmCrop } from "./farm.types";

export type TownProduct = {
  id: string;
  name: string;
  buyPrice: number;
  sellPrice: number;
  stock: number;
};

export type TownShop = {
  id: string;
  name: string;
  objectId: number;
  price: number;
  unlocked: boolean;
  level: number;
};

export type TownSnapshot = {
  economy: { coins: number; reputation: number; totalSales: number };
  shops: TownShop[];
  products: TownProduct[];
};

export type TownCatalogResponse = { crops: FarmCrop[] };
