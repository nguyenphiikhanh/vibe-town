export type TownProduct = {
  id: string;
  name: string;
  buyPrice: number;
  sellPrice: number;
};

export const townProducts: TownProduct[] = [
  { id: "watermelon", name: "Dưa hấu", buyPrice: 12, sellPrice: 22 },
  { id: "pineapple", name: "Dứa", buyPrice: 15, sellPrice: 28 },
  { id: "grapes", name: "Nho", buyPrice: 18, sellPrice: 34 },
  { id: "sunflower", name: "Hoa hướng dương", buyPrice: 10, sellPrice: 20 },
];

export const townShops = [
  { id: "grocery", name: "Cửa hàng tạp hóa", objectId: 831, price: 0 },
  { id: "gift", name: "Tiệm quà lưu niệm", objectId: 833, price: 700 },
  { id: "bakery", name: "Tiệm bánh nhỏ", objectId: 1029, price: 1200 },
  { id: "boutique", name: "Cửa hàng thời trang", objectId: 852, price: 2000 },
] as const;
