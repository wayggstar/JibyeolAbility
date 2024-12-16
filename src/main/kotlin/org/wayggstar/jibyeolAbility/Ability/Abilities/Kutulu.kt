package org.wayggstar.jibyeolAbility.Ability.Abilities

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.wayggstar.jibyeolAbility.Ability.Ability
import org.wayggstar.jibyeolAbility.Ability.cooldownManager
import org.wayggstar.jibyeolAbility.Debuff.Debuff
import org.wayggstar.jibyeolAbility.GameManger
import org.wayggstar.jibyeolAbility.JibyeolAbility
import kotlin.random.Random

class Kutulu(private val gameManager: GameManger, private val cooldownManager: cooldownManager, ): Ability, Listener {

    override val name: String = "크툴루"
    override val description: List<String> = listOf(
        "§5?????",
        "§f철괴§7를 우클릭하면 §7주변 플레이어의 정신에 혼란을 줍니다",
        "§7(§c쿨타임§7: §6100§7초)"
    )
    override val rank: String = "§5???????"

    @EventHandler
    fun onIronRightClick(event: PlayerInteractEvent){
        val player = event.player
        val itemInHand = player.inventory.itemInMainHand
        val action = event.action
        if (!isKutulu(player)){return}
        if ((itemInHand.type == Material.IRON_INGOT) && (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR)){
            if (event.hand != EquipmentSlot.HAND)return
            if (cooldownManager.isOnCooldown(player, "크툴루")){
                cooldownManager.notifyCooldown(player, "크툴루")
                return
            }
            Bukkit.getScheduler().runTaskTimer(JibyeolAbility.instance, Runnable {
                KutuluActive(player)
            }, 0L, 20L)

            Bukkit.getScheduler().runTaskLater(JibyeolAbility.instance, Runnable {
                player.sendMessage("§5혼란§a이 끝났습니다!")
            }, 10 * 20L)
            cooldownManager.startCooldown(player, "크툴루", 60L) // 10초 쿨타임

        }
    }

    private fun KutuluActive(player: Player) {
        val radius = 10.0
        val location = player.location
        val world = location.world ?: return
        world.getNearbyEntities(location, radius, radius, radius).forEach { target ->
            if (target is Player && target != player) {
                Debuff.addDebuff(target, Debuff.DebuffType.Silence, 30)
                induceMadness(target)
            }
        }
    }

    private fun induceMadness(player: Player) {
        player.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 60, 1))
        player.addPotionEffect(PotionEffect(PotionEffectType.DARKNESS, 60, 0))
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 60, 4))
        player.addPotionEffect(PotionEffect(PotionEffectType.POISON, 60, 1))
        player.addPotionEffect(PotionEffect(PotionEffectType.WITHER, 60, 2))
        player.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 60, 1))

        object : BukkitRunnable() {
            override fun run() {
                val randomYaw = Random.nextFloat() * 360 - 180
                val randomPitch = Random.nextFloat() * 180 - 90

                val location = player.location
                location.yaw = randomYaw
                location.pitch = randomPitch
                player.teleport(location)
            }
        }.runTaskTimer(JibyeolAbility.instance, 0L, 20L)
        if (Random.nextDouble() < 0.3) {
            when (Random.nextInt(3)) {
                0 -> player.sendMessage("§c당신은 크툴루의 환영을 봅니다!")
                1 -> player.sendMessage("§c주변이 이상하게 느껴집니다...")
            }
        }
    }

    private fun isKutulu(player: Player): Boolean {
        val ability = gameManager.getPlayerAbility(player)
        return ability is Kutulu
    }
}