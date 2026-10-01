---
version: alpha
name: Avatar Web Farm
description: A nostalgic browser farm built around the source project's original HD pixel-art assets.
colors:
  primary: "#34743C"
  leaf: "#6EAA47"
  leaf-light: "#ABD277"
  soil: "#8A5737"
  soil-shadow: "#5A3828"
  sky: "#BFE6EC"
  canvas: "#F4F0D9"
  surface: "#FFFBEA"
  ink: "#293A2B"
  ink-soft: "#66745A"
  gold: "#F2C14E"
  error: "#AD433C"
typography:
  display:
    fontFamily: Georgia, serif
    fontSize: 36px
    fontWeight: 700
    lineHeight: 1.1
  title:
    fontFamily: Georgia, serif
    fontSize: 24px
    fontWeight: 700
    lineHeight: 1.2
  body:
    fontFamily: system-ui, sans-serif
    fontSize: 15px
    fontWeight: 500
    lineHeight: 1.5
  label:
    fontFamily: system-ui, sans-serif
    fontSize: 12px
    fontWeight: 700
    lineHeight: 1.2
    letterSpacing: 0.04em
rounded:
  sm: 8px
  md: 14px
  lg: 20px
spacing:
  xs: 4px
  sm: 8px
  md: 12px
  lg: 20px
  xl: 28px
  xxl: 40px
components:
  action:
    backgroundColor: "{colors.primary}"
    textColor: "{colors.surface}"
    rounded: "{rounded.sm}"
    padding: "{spacing.md}"
  panel:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.ink}"
    rounded: "{rounded.md}"
    padding: "{spacing.lg}"
---

# Avatar Web Farm

## Overview
Reading this as a cozy, nostalgic farming game for returning Avatar players. The supplied HD sprites set the visual tone. The surrounding interface uses warm paper, leaf green, and soil brown, with crisp pixel edges and simple responsive controls.

## Colors
- Use deep leaf green for the main action and status.
- Keep farm terrain in the supplied sprite palette; use soil brown only for plot beds and earthy dividers.
- Use warm canvas and surface colors around the game scene so the original sprites remain prominent.
- Reserve gold for harvest-ready and reward states.

## Typography
- Use a gentle serif for the farm title and compact system sans-serif for controls and status.
- Keep text short and legible at desktop and mobile sizes.

## Layout
- Place the farm scene at the center of the page with inventory and crop selection in nearby panels.
- Preserve a landscape game board on desktop; allow the board to scale within the viewport on smaller screens.
- Use a fixed logical pixel scale for sprite rendering and nearest-neighbor interpolation.

## Elevation & Depth
- Use thin olive borders, a warm paper surface, and restrained shadows to separate controls from the farm scene.
- Let the existing pixel art provide most visual depth.

## Shapes
- UI panels use modest rounded corners; game sprites and farm plots retain hard pixel edges.

## Components
- Primary actions use leaf green; disabled actions use muted sage.
- Each plot exposes its crop, growth phase, and remaining time with both visual and text cues.
- Inventory counts remain visible without obscuring the scene.

## Do's and Don'ts
- Do use the existing HD farm sprites and keep pixel edges sharp.
- Do show loading, connection, empty-plot, growing, ready, and error states.
- Don't interpolate sprite art smoothly or apply filters that blur it.
- Don't add watering, fertilizer, livestock, or other systems to this MVP.
