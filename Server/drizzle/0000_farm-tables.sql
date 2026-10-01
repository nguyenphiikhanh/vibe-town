CREATE TABLE `farm_inventory` (
	`user_id` int NOT NULL,
	`crop_id` varchar(32) NOT NULL,
	`seeds` int unsigned NOT NULL DEFAULT 0,
	`harvested` int unsigned NOT NULL DEFAULT 0,
	CONSTRAINT `farm_inventory_user_id_crop_id_pk` PRIMARY KEY(`user_id`,`crop_id`)
);
--> statement-breakpoint
CREATE TABLE `farm_plots` (
	`user_id` int NOT NULL,
	`plot_index` smallint unsigned NOT NULL,
	`crop_id` varchar(32),
	`planted_at` bigint,
	`ready_at` bigint,
	CONSTRAINT `farm_plots_user_id_plot_index_pk` PRIMARY KEY(`user_id`,`plot_index`)
);
--> statement-breakpoint
ALTER TABLE `farm_inventory` ADD CONSTRAINT `farm_inventory_user_id_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE cascade ON UPDATE no action;--> statement-breakpoint
ALTER TABLE `farm_plots` ADD CONSTRAINT `farm_plots_user_id_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE cascade ON UPDATE no action;
