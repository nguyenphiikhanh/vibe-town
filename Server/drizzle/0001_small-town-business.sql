CREATE TABLE `town_businesses` (
	`user_id` int NOT NULL,
	`shop_id` varchar(32) NOT NULL,
	`level` smallint unsigned NOT NULL DEFAULT 1,
	CONSTRAINT `town_businesses_user_id_shop_id_pk` PRIMARY KEY(`user_id`,`shop_id`)
);
--> statement-breakpoint
CREATE TABLE `town_economy` (
	`user_id` int NOT NULL,
	`coins` int unsigned NOT NULL DEFAULT 500,
	`reputation` smallint unsigned NOT NULL DEFAULT 0,
	`total_sales` int unsigned NOT NULL DEFAULT 0,
	CONSTRAINT `town_economy_user_id_pk` PRIMARY KEY(`user_id`)
);
--> statement-breakpoint
CREATE TABLE `town_inventory` (
	`user_id` int NOT NULL,
	`product_id` varchar(32) NOT NULL,
	`stock` int unsigned NOT NULL DEFAULT 0,
	CONSTRAINT `town_inventory_user_id_product_id_pk` PRIMARY KEY(`user_id`,`product_id`)
);
--> statement-breakpoint
ALTER TABLE `town_businesses` ADD CONSTRAINT `town_businesses_user_id_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE cascade ON UPDATE no action;
--> statement-breakpoint
ALTER TABLE `town_economy` ADD CONSTRAINT `town_economy_user_id_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE cascade ON UPDATE no action;
--> statement-breakpoint
ALTER TABLE `town_inventory` ADD CONSTRAINT `town_inventory_user_id_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE cascade ON UPDATE no action;
