import { FarmSprite } from "./FarmSprite";
import type { FarmCrop, FarmPlot } from "./farm.types";

const apiUrl = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:3001";
const soilTiles = [47, 50, 48, 49, 49, 47, 50, 48, 50, 49, 47, 48, 48, 50, 49, 47];

type FarmBoardProps = {
  plots: FarmPlot[];
  crops: FarmCrop[];
  selectedCropId: string;
  now: number;
  onPlotClick: (plot: FarmPlot) => void;
};

export function FarmBoard({ plots, crops, selectedCropId, now, onPlotClick }: FarmBoardProps) {
  const cropById = new Map(crops.map((crop) => [crop.id, crop]));

  return (
    <div aria-label="48 ô đất nông trại" className="farm-board" role="group">
      {plots.map((plot) => {
        const crop = plot.cropId ? cropById.get(plot.cropId) : null;
        const ready = Boolean(plot.readyAt && now >= plot.readyAt);
        const secondsLeft = plot.readyAt ? Math.max(0, Math.ceil((plot.readyAt - now) / 1000)) : 0;
        const action = plot.phase === "empty" ? "Gieo" : ready ? "Thu hoạch" : `${secondsLeft} giây`;
        const soilTile = soilTiles[((plot.index - 1) * 7 + Math.floor((plot.index - 1) / 8) * 3) % soilTiles.length];
        const progress = plot.plantedAt && plot.readyAt
          ? Math.max(0, Math.min(0.999, (now - plot.plantedAt) / (plot.readyAt - plot.plantedAt)))
          : 0;

        return (
          <button
            aria-label={
              plot.phase === "empty"
                ? `Ô ${plot.index}, đất trống. Gieo ${cropById.get(selectedCropId)?.name ?? "hạt giống đã chọn"}`
                : `Ô ${plot.index}, ${crop?.name ?? "cây trồng"}, ${ready ? "đã sẵn sàng thu hoạch" : `còn ${secondsLeft} giây`}`
            }
            className={`farm-plot farm-plot--${plot.phase}${ready ? " farm-plot--ready" : ""}`}
            key={plot.index}
            onClick={() => onPlotClick(plot)}
            style={{ backgroundImage: `url("${apiUrl}/assets/hd/farm/${soilTile}.png")` }}
            type="button"
          >
            {crop && <FarmSprite crop={crop} phase={ready ? "ready" : "growing"} progress={progress} />}
            {plot.phase === "empty" && (
              <span
                aria-hidden="true"
                className="plot-seed-mark"
                style={{ backgroundImage: `url("${apiUrl}/assets/hd/farm/1.png")` }}
              />
            )}
            {crop && <span className={`plot-caption${ready ? " plot-caption--ready" : ""}`}>{action}</span>}
          </button>
        );
      })}
    </div>
  );
}
