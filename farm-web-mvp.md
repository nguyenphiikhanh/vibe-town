# Web farm MVP

## Goal
Create separate `Client` and `Server` applications for an HD pixel-art farm with authenticated Avatar accounts and persisted plant, grow, and harvest gameplay.

## Tasks
- [x] Define the shared visual language and API/data boundaries → Verify: `DESIGN.md`, `Client/`, and `Server/` exist.
- [x] Add a Drizzle farm migration using the existing `users` identity → Verify: `Server/drizzle/` defines plots and crop inventory without recreating legacy tables.
- [x] Implement login, farm catalog, and Colyseus farm room actions → Verify: API and room source compile.
- [x] Build the responsive pixel-art farm UI using the existing HD farm sprites → Verify: browser client compiles and exposes plant/grow/harvest interactions.
- [x] Document setup and local run commands → Verify: README points to both apps and migration.
- [x] Verify both applications with available static/build checks → Verify: builds complete without errors.

## Done when
- A player can log into an existing Avatar account, plant a crop in an empty plot, see server-derived growth progress, and harvest a ready crop.
- Farm plots and seed/harvest counts persist in MariaDB.
- Client and server live under separate `Client` and `Server` directories.

## Notes
- Initial scope is the personal farm only: no watering, fertilizer, animals, shop, or visiting other farms.
- Reuse the existing users/players tables without changing legacy password hashes; create separate normalized farm tables.
- Keep growth timing server-authoritative and short enough for an MVP session.
