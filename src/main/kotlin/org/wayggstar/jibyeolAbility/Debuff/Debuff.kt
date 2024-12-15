package org.wayggstar.jibyeolAbility.Debuff

import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.wayggstar.jibyeolAbility.JibyeolAbility

class Debuff(val player: Player, val type: DebuffType, val duration: Long) {

    fun apply() {
        // 디버프 적용 로직
        when (type) {
            DebuffType.Silence -> Silence()
            else -> {}
        }
    }

    private fun Silence() {
        player.sendMessage("§0침묵§7당했습니다!!!!")
    }

    enum class DebuffType {
        Silence
    }

    companion object {
        private val playerDebuffs: MutableMap<Player, MutableList<Debuff>> = mutableMapOf()

        fun addDebuff(player: Player, type: DebuffType, duration: Long) {
            val debuffs = playerDebuffs.getOrPut(player) { mutableListOf() }
            debuffs.add(Debuff(player, type, duration).apply { apply() })
        }

        fun removeDebuff(player: Player, type: DebuffType) {
            val debuffs = playerDebuffs[player] ?: return
            debuffs.removeIf { it.type == type }
            if (debuffs.isEmpty()) {
                playerDebuffs.remove(player)
            }
        }

        fun hasDebuff(player: Player, type: DebuffType): Boolean {
            val debuffs = playerDebuffs[player] ?: return false
            return debuffs.any { it.type == type }
        }

        fun getDebuffs(player: Player): List<Debuff> {
            return playerDebuffs[player]?.toList() ?: emptyList()
        }
    }
}