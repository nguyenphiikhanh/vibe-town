export type CropDefinition = {
  id: string;
  name: string;
  growingFrames: FarmSpriteFrame[];
  readyFrame: FarmSpriteFrame;
  growthSeconds: number;
};

export type FarmSpriteFrame = {
  atlas: "0.png" | "1.png";
  x: number;
  y: number;
  width: number;
  height: number;
};

// Coordinates are the HD (2x) crops defined in res/data/farm/image_farm.dat.
export const crops: CropDefinition[] = [
  {
    id: "watermelon",
    name: "Dưa hấu",
    growingFrames: [
      { atlas: "0.png", x: 192, y: 90, width: 40, height: 30 },
      { atlas: "0.png", x: 106, y: 0, width: 54, height: 38 },
    ],
    readyFrame: { atlas: "0.png", x: 0, y: 0, width: 56, height: 54 },
    growthSeconds: 45,
  },
  {
    id: "pineapple",
    name: "Dứa",
    growingFrames: [
      { atlas: "0.png", x: 42, y: 140, width: 46, height: 40 },
      { atlas: "0.png", x: 0, y: 54, width: 48, height: 50 },
    ],
    readyFrame: { atlas: "0.png", x: 56, y: 0, width: 50, height: 58 },
    growthSeconds: 60,
  },
  {
    id: "grapes",
    name: "Nho",
    growingFrames: [
      { atlas: "0.png", x: 172, y: 118, width: 48, height: 48 },
      { atlas: "0.png", x: 190, y: 0, width: 52, height: 48 },
    ],
    readyFrame: { atlas: "0.png", x: 96, y: 64, width: 46, height: 52 },
    growthSeconds: 75,
  },
  {
    id: "sunflower",
    name: "Hướng dương",
    growingFrames: [
      { atlas: "1.png", x: 48, y: 194, width: 32, height: 28 },
      { atlas: "1.png", x: 128, y: 56, width: 36, height: 48 },
    ],
    readyFrame: { atlas: "1.png", x: 210, y: 0, width: 34, height: 50 },
    growthSeconds: 90,
  },
];

export const plotCount = 48;

export function findCrop(cropId: string): CropDefinition | undefined {
  return crops.find((crop) => crop.id === cropId);
}
