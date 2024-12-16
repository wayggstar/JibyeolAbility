package org.wayggstar.jibyeolAbility.Ability.Abilities

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.scheduler.BukkitRunnable
import org.wayggstar.jibyeolAbility.Ability.Ability
import org.wayggstar.jibyeolAbility.Ability.cooldownManager
import org.wayggstar.jibyeolAbility.Debuff.Debuff
import org.wayggstar.jibyeolAbility.GameManger
import org.wayggstar.jibyeolAbility.JibyeolAbility

class Herumes(private var gameManger: GameManger, private var cooldownManager: cooldownManager) : Ability, Listener {

    override val name: String = "§b헤르메스"
    override val description: List<String> = listOf(
        "§a여행§7의 §e신",
        "§c낙하데미지§7를 받지 않습니다.",
        "§f철괴§7를 우클릭할 시 5초간 §b비행§7한다.(§c쿨타임§7: §630§7초)"
    )
    override val rank: String = "A"

    override fun activate() {
    }

    private val flyingPlayers: MutableSet<Player> = mutableSetOf()

    @EventHandler
    fun onIronRightClick(event: PlayerInteractEvent){
        val player = event.player
        val itemInHand = player.inventory.itemInMainHand
        val action = event.action
        if (!isHerumes(player)){return}
        if (Debuff.hasDebuff(player, Debuff.DebuffType.Silence)) {
            player.sendMessage("§c현재 침묵 상태로 인해 능력을 사용할 수 없습니다!")
            return
        }
        if ((itemInHand.type == Material.IRON_INGOT) && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            if (event.hand != EquipmentSlot.HAND) return
            if (cooldownManager.isOnCooldown(player, "헤르메스")) {
                cooldownManager.notifyCooldown(player, "헤르메스")
                return
            }
            player.sendMessage("§b여행§7의 §e신§7이 §b여행§7을 시작합니다.")
            flyingPlayers.add(player)
            player.allowFlight = true

            cooldownManager.startCooldown(player, "헤르메스", 30L) // 10초 쿨타임

            object : BukkitRunnable(){
                override fun run() {
                    if (!flyingPlayers.contains(player) || !player.isOnline) {
                        cancel()
                        return
                    }
                    flyingPlayers.remove(player)
                    player.isFlying = false
                    player.allowFlight = false
                    player.sendMessage("§b여행§7의 §e신§7이 §b여행§7을 끝냅니다.")
                }
            }.runTaskLater(JibyeolAbility.instance, 100L)
        }
    }

    @EventHandler
    fun onPlayerToggleFlight(event: PlayerToggleFlightEvent) {
        val player = event.player
        if (flyingPlayers.contains(player) && player.gameMode != GameMode.CREATIVE) {
            event.isCancelled = false
        }
    }


    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        flyingPlayers.remove(player)
        player.allowFlight = false
    }

    @EventHandler
    fun onFallDamage(event: EntityDamageEvent){
        val entity = event.entity
        if (entity is Player){
            val damageCause = event.cause
            if (!isHerumes(entity)){return}
            when (damageCause){
                EntityDamageEvent.DamageCause.FALL -> event.isCancelled = true
                else -> {}
            }
        }
    }

    private fun isHerumes(player: Player): Boolean {
        val ability = gameManger.getPlayerAbility(player)
        return ability is Herumes
    }


}