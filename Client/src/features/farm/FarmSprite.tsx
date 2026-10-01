import type { CSSProperties } from "react";
import type { FarmCrop } from "./farm.types";

const apiUrl = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:3001";

type FarmSpriteProps = {
  crop: FarmCrop;
  phase: "growing" | "ready";
  progress?: number;
  compact?: boolean;
};

export function FarmSprite({ crop, phase, progress = 0, compact = false }: FarmSpriteProps) {
  const stageIndex = Math.min(crop.growingFrames.length - 1, Math.floor(progress * crop.growingFrames.length));
  const frame = phase === "ready" ? crop.readyFrame : crop.growingFrames[stageIndex];
  const scale = compact ? 0.62 : phase === "ready" ? 1.18 : 1;
  const style = {
    width: frame.width * scale,
    height: frame.height * scale,
    backgroundImage: `url("${apiUrl}/assets/hd/bigFarm/${frame.atlas}")`,
    backgroundSize: frame.atlas === "0.png" ? "496px 520px" : "488px 444px",
    backgroundPosition: `${-frame.x * scale}px ${-frame.y * scale}px`,
    transform: phase === "ready" ? "translateY(-3px)" : "translateY(2px)",
  } as CSSProperties;

  return <span aria-hidden="true" className={`crop-pixel${compact ? " crop-pixel--icon" : ` crop-pixel--${phase}`}`} style={style} />;
}
