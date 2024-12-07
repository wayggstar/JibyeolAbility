package org.wayggstar.jibyeolWar.Ability.Abilities

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.scheduler.BukkitRunnable
import org.wayggstar.jibyeolWar.Ability.Ability
import org.wayggstar.jibyeolWar.GameManager
import org.wayggstar.jibyeolWar.JibyeolWar
import kotlin.random.Random

class Thor : Ability, Listener {
    private lateinit var gameManager: GameManager

    private val stunnedTargets = mutableSetOf<Player>()

    override val name: String = "토르"
    override val description: List<String> = listOf(
        "§b번개§r를 다루는 §e신§r이다.",
        "철도끼로 사람을 공격할 시 5% 확률로 번개를 내려친다.",
        "번개에 맞은 적은 2초간 §b감전§r되어서 움직일 수 없다."
    )
    override val rank: String = "A"

    override fun activate() {
    }

    override fun deactivate() {
    }

    @EventHandler
    fun onHitThor(event: EntityDamageByEntityEvent) {
        val attacker = event.damager as? Player ?: return
        val target = event.entity
        if (!isThor(attacker)) return
        if (attacker.inventory.itemInMainHand.type == Material.IRON_AXE) {
            val chance = Random.nextInt(100)
            if (chance < 5) {
                val location = target.location
                target.world.strikeLightning(location)

                if (target is Player) {
                    stunPlayer(target)
                }
            }
        }
    }

    private fun stunPlayer(player: Player) {
        stunnedTargets.add(player)
        player.sendMessage("§b번개에 맞아 §b감전§r되었습니다!")

        object : BukkitRunnable() {
            override fun run() {
                stunnedTargets.remove(player)
                player.sendMessage("§a감전이 해제되었습니다!")
            }
        }.runTaskLater(JibyeolWar.instance, 40L)
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        if (stunnedTargets.contains(player)) {
            event.isCancelled = true  // 이동 이벤트 취소
            player.sendMessage("§c당신은 §b감전§c되어 움직일 수 없습니다!")
        }
    }

    private fun isThor(player: Player): Boolean {
        val ability = gameManager.getPlayerAbility(player)  // 플레이어의 능력 확인
        return ability is Thor
    }
}