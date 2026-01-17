# Orbz

## Features

*   **Shared Island Currency:** Orbs are tied to the island team, allowing collaborative progression.
*   **Customizable Upgrade Path:** A branching system for island limits and boosters, configurable in gamew or via YAML.
*   **SuperiorSkyblock2 Integration:** Supports SSB2 upgrades such as Hopper and Member limits.
*   **AxGens Integration:** Syncs EXP, Sell, Shards, and Bits multipliers across the server network.
*   **Flexible Storage:** Supports YAML and SQL databases (MySQL, MariaDB, PostgreSQL) with HikariCP connection pooling.
*   **PlaceholderAPI Support:** Provides placeholders for island balances, node levels, and current multipliers.
*   **In-Game Editor:** Modify upgrade nodes directly within the game interface.
*   **Visual Customization:** Full support for Base64 heads, custom model data, sounds, and particles.

---

## Commands & Permissions

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/upgrades` | Open the island upgrade menu. | `orbz.command.upgrades` |
| `/upgrades edit` | Open the editor mode for upgrades. | `orbz.upgrades.edit` |
| `/orbs balance` | View the island orb balance. | `orbz.command.orbs` |
| `/orbs give <p> <n>` | Give orbs to an island. | `orbz.admin.give` |
| `/orbs take <p> <n>` | Remove orbs from an island. | `orbz.admin.set` |
| `/orbs set <p> <n>` | Set an island's orb balance. | `orbz.admin.set` |
| `/orbs item <p> <n>` | Give an Orb Voucher item. | `orbz.admin.item` |
| `/orbs reload` | Reload configuration files. | `orbz.admin.reload` |
| `/pickupallgens` | Pick up all generators on the island. | `orbz.pickupallgens` |

---

## Configuration

### Upgrade Path (`path.yml`)
The upgrade tree is fully configurable. Each node supports:
*   **Requirements:** Configurable costs (Orbs, Vault), Island Level, or prerequisite nodes.
*   **Actions:** Configurable console commands, SSB2 upgrades, or messages.
*   **Display:** Configurable material (including `BASE64:texture`), name, and lore.

### Plugin Settings (`config.yml`)
The following are fully configurable:
*   **Storage:** Database connection details and storage type.
*   **Vouchers:** Material, name, lore, and custom model data for orb voucher items.
*   **GUI:** Title, size, and filler item appearance.
*   **Boosters:** Multiplier rates (per-level) and maximum levels for EXP, SELL, SHARDS, and BITS.

---

## Placeholders

The following placeholders are available via PlaceholderAPI:

*   `%orbz_balance%` - Island orb balance.
*   `%orbz_node_level_<nodeId>%` - Level of a specific upgrade.
*   `%orbz_booster_<type>%` - Current multiplier (`EXP`, `SELL`, `SHARDS`, or `BITS`).

---

## API

Developers can query island multipliers using the following method:

```java
double multiplier = IslandBoosters.getMultiplier(player, BoosterType.EXP);
```
