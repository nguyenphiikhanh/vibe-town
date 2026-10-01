"use client";

import { Client as ColyseusClient, type Room } from "@colyseus/sdk";
import { useCallback, useEffect, useState } from "react";
import type { FarmSnapshot } from "./farm.types";

const gameUrl = process.env.NEXT_PUBLIC_GAME_URL ?? "http://localhost:2567";

export function useFarmRoom(token: string | null, userId: number | null) {
  const [snapshot, setSnapshot] = useState<FarmSnapshot | null>(null);
  const [room, setRoom] = useState<Room | null>(null);
  const [connection, setConnection] = useState<"idle" | "connecting" | "connected" | "error">("idle");
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!token || userId === null) return;
    let active = true;
    let joinedRoom: Room | null = null;
    const client = new ColyseusClient(gameUrl);
    client.auth.token = token;
    setConnection("connecting");
    setError(null);

    client
      .joinOrCreate("farm", { ownerId: userId })
      .then((joined) => {
        joinedRoom = joined;
        if (!active) {
          void joined.leave();
          return;
        }
        setRoom(joined);
        setConnection("connected");
        joined.onMessage("farm:state", (data: FarmSnapshot) => setSnapshot(data));
        joined.onMessage("farm:error", (data: { message: string }) => setError(data.message));
      })
      .catch((cause: unknown) => {
        if (!active) return;
        setConnection("error");
        setError(cause instanceof Error ? cause.message : "Không thể kết nối nông trại.");
      });

    return () => {
      active = false;
      void joinedRoom?.leave();
    };
  }, [token, userId]);

  const plant = useCallback((plotIndex: number, cropId: string) => {
    room?.send("farm:plant", { plotIndex, cropId });
    setError(null);
  }, [room]);

  const harvest = useCallback((plotIndex: number) => {
    room?.send("farm:harvest", { plotIndex });
    setError(null);
  }, [room]);

  return { snapshot, connection, error, plant, harvest };
}
