package org.wayggstar.jibyeolAbility.Ability.Abilities

import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.wayggstar.jibyeolAbility.Ability.Ability
import org.wayggstar.jibyeolAbility.Ability.cooldownManager
import org.wayggstar.jibyeolAbility.Debuff.Debuff
import org.wayggstar.jibyeolAbility.GameManger
import org.wayggstar.jibyeolAbility.JibyeolAbility

class Poseidon(private val gameManager: GameManger, private val cooldownManager: cooldownManager): Ability, Listener {
    override val name: String = "§1포세이돈"
    override val description: List<String> = listOf(
        "§1바다§7의 §e신§7",
        "§f철괴§7를 우클릭할 시 §1해일§7을 일으켜 주변에 피해를 줍니다.(§c쿨타임§7: §640§7초)",
        "§e금괴§7를 우클릭할 시 모든 포션효과를 제거합니다.(§c쿨타임§7: §650§7초)",
    )
    override val rank: String = "A"

    @EventHandler
    fun onIronGoldRightClick(event: PlayerInteractEvent) {
        val player = event.player
        val itemInHand = player.inventory.itemInMainHand
        val action = event.action
        if (!isPoseidon(player)) {
            return
        }
        if (Debuff.hasDebuff(player, Debuff.DebuffType.Silence)) {
            player.sendMessage("§c현재 침묵 상태로 인해 능력을 사용할 수 없습니다!")
            return
        }
        if ((itemInHand.type == Material.IRON_INGOT) && (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR)) {
            if (event.hand != EquipmentSlot.HAND) return
            if (cooldownManager.isOnCooldown(player, "해일")) {
                cooldownManager.notifyCooldown(player, "해일")
                return
            }
            summonTsunami(player)
            cooldownManager.startCooldown(player, "해일", 30L)
        }
        if ((itemInHand.type == Material.GOLD_INGOT) && (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR)) {
            if (event.hand != EquipmentSlot.HAND) return
            if (cooldownManager.isOnCooldown(player, "정화")) {
                cooldownManager.notifyCooldown(player, "정화")
                return
            }
            GoldPose(player)
            cooldownManager.startCooldown(player, "정화", 30L)
        }
    }


    private fun summonTsunami(player: Player) {
        val world: World = player.world
        val startLocation = player.location

        player.sendMessage("포세이돈의 해일이 시작됩니다!")

        object : BukkitRunnable() {
            var radius = 1

            override fun run() {
                if (radius > 10) {
                    clearTsunami(startLocation, world, radius - 1)
                    cancel()
                    return
                }

                createWaterRing(startLocation, world, radius)
                radius++
            }
        }.runTaskTimer(JibyeolAbility.instance, 0L, 10L)
    }

    private fun createWaterRing(center: org.bukkit.Location, world: World, radius: Int) {
        val blocksToChange = mutableListOf<Block>()

        for (x in -radius..radius) {
            for (z in -radius..radius) {
                if (x * x + z * z <= radius * radius && x * x + z * z > (radius - 1) * (radius - 1)) {
                    val block = center.clone().add(x.toDouble(), 0.0, z.toDouble()).block
                    if (block.type == Material.AIR) {
                        blocksToChange.add(block)
                    }
                }
            }
        }
        blocksToChange.forEach { it.type = Material.WATER }
    }

    private fun clearTsunami(center: org.bukkit.Location, world: World, radius: Int) {
        for (x in -radius..radius) {
            for (z in -radius..radius) {
                if (x * x + z * z <= radius * radius) {
                    val block = center.clone().add(x.toDouble(), 0.0, z.toDouble()).block
                    if (block.type == Material.WATER) {
                        block.type = Material.AIR
                    }
                }
            }
        }
    }

    private fun GoldPose(player: Player) {
        player.activePotionEffects.forEach { effect: PotionEffect ->
            player.removePotionEffect(effect.type)
        }
        val location = player.location
        val world = player.world
        world.spawnParticle(Particle.WATER_SPLASH, location, 50, 1.0, 1.0, 1.0, 0.1)
        world.playSound(location, Sound.ENTITY_DOLPHIN_PLAY, 1.0f, 1.0f)
        player.sendMessage("§1포세이돈§7의 힘으로 모든 상태 이상이 §b정화§7되었습니다!")
    }

    private fun isPoseidon(player: Player): Boolean {
        val ability = gameManager.getPlayerAbility(player)
        return ability is Poseidon
    }
}