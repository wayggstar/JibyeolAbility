package org.wayggstar.jibyeolAbility.Debuff

import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.wayggstar.jibyeolAbility.JibyeolAbility

class Debuff(val player: Player, val type: DebuffType, val duration: Long) {

    fun apply() {
        when (type) {
            DebuffType.Silence -> applySilence()
            DebuffType.ReverseControl -> applyReverseControl()
            else -> {}
        }
        object : BukkitRunnable() {
            var timeLeft = duration / 20
            override fun run() {
                if (!hasDebuff(player, type) || timeLeft <= 0) {
                    cancel()
                    return
                }

                val message = "§c[디버프: ${type.name}] §7남은 시간: §a${timeLeft}s"
                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, TextComponent(message))

                timeLeft--
            }
        }.runTaskTimer(JibyeolAbility.instance, 0L, 20L)

        object : BukkitRunnable() {
            override fun run() {
                removeDebuff(player, type)
            }
        }.runTaskLater(JibyeolAbility.instance, duration / 50)
    }

    private fun applySilence() {
        player.sendMessage("§c[침묵] §7스킬을 사용할 수 없습니다!")
    }

    private fun applyReverseControl() {
        player.sendMessage("§c[조작 반전] §7이동 방향이 반대로 작동합니다!")
        ReverseControlManager.addPlayer(player)
    }

    enum class DebuffType {
        Silence,
        ReverseControl
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
            when (type) {
                DebuffType.ReverseControl -> ReverseControlManager.removePlayer(player)
                else -> {}
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