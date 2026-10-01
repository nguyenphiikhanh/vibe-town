export type FarmSpriteFrame = {
  atlas: "0.png" | "1.png";
  x: number;
  y: number;
  width: number;
  height: number;
};

export type FarmCrop = {
  id: string;
  name: string;
  growingFrames: FarmSpriteFrame[];
  readyFrame: FarmSpriteFrame;
  growthSeconds: number;
};

export type FarmPlot = {
  index: number;
  cropId: string | null;
  plantedAt: number | null;
  readyAt: number | null;
  phase: "empty" | "growing" | "ready";
  remainingSeconds: number;
};

export type FarmSnapshot = {
  plots: FarmPlot[];
  inventory: Array<{ cropId: string; seeds: number; harvested: number }>;
  crops: FarmCrop[];
  serverTime: number;
};

export type FarmUser = { id: number; username: string };

export type LoginResponse = {
  token: string;
  user: FarmUser;
  player: { xu: number; farmExperience: number };
};
