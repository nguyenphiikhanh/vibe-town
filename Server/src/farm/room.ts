import { Client, Room } from "colyseus";
import { verifyAccessToken, type FarmIdentity } from "../auth/token.js";
import { FarmRepository } from "./repository.js";

type FarmClient = Client & { auth: FarmIdentity };

export class FarmRoom extends Room {
  maxClients = 1;
  private readonly farm = new FarmRepository();

  async onAuth(_client: Client, options: { ownerId?: number }, context: { token?: string }) {
    if (!context.token) throw new Error("Đăng nhập trước khi vào nông trại.");
    const identity = await verifyAccessToken(context.token);
    if (Number(options.ownerId) !== identity.userId) throw new Error("Bạn không thể vào nông trại này.");
    return identity;
  }

  async onJoin(client: FarmClient) {
    await this.sendSnapshot(client);
  }

  onCreate() {
    this.onMessage("farm:plant", async (client: FarmClient, payload: { plotIndex?: number; cropId?: string }) => {
      try {
        await this.farm.plant(client.auth.userId, Number(payload?.plotIndex), String(payload?.cropId ?? ""));
        await this.sendSnapshot(client);
      } catch (error) {
        this.sendError(client, error);
      }
    });

    this.onMessage("farm:harvest", async (client: FarmClient, payload: { plotIndex?: number }) => {
      try {
        await this.farm.harvest(client.auth.userId, Number(payload?.plotIndex));
        await this.sendSnapshot(client);
      } catch (error) {
        this.sendError(client, error);
      }
    });
  }

  private async sendSnapshot(client: FarmClient): Promise<void> {
    client.send("farm:state", await this.farm.snapshot(client.auth.userId));
  }

  private sendError(client: Client, error: unknown): void {
    const message = error instanceof Error ? error.message : "Không thể cập nhật nông trại.";
    client.send("farm:error", { message });
  }
}
