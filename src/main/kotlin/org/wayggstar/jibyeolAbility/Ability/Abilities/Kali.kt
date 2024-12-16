package org.wayggstar.jibyeolAbility.Ability.Abilities

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.wayggstar.jibyeolAbility.Ability.Ability
import org.wayggstar.jibyeolAbility.Ability.cooldownManager
import org.wayggstar.jibyeolAbility.Debuff.Debuff
import org.wayggstar.jibyeolAbility.GameManger
import org.wayggstar.jibyeolAbility.JibyeolAbility

class Kali(private val gameManager: GameManger, private var cooldownManager: cooldownManager): Listener, Ability {
    override val name: String = "§5칼리"
    override val description: List<String> = listOf(
        "§5파괴§7의 §e여신",
        "§710초 동안 플레이어 주변에 칼리의 §0검은 영역§7을 생성한다",
        "§7영역 내 적들에게 §c지속 데미지(초당 2)§7와 §0침묵§7을 부여합니다.(§c쿨타임§7: §650§7초)",
        "§7플레이어에게는 §4힘과 §b신속§7이 부여된다.§5침묵§7을 무시합니다."
    )
    override val rank: String = "SS"


    @EventHandler
    fun onIronRightClick(event: PlayerInteractEvent){
        val player = event.player
        val itemInHand = player.inventory.itemInMainHand
        val action = event.action
        if (!isKali(player)){return}
        if ((itemInHand.type == Material.IRON_INGOT) && (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR)){
            if (event.hand != EquipmentSlot.HAND)return
            if (cooldownManager.isOnCooldown(player, "칼리")){
                cooldownManager.notifyCooldown(player, "칼리")
                return
            }
            activateKaliWrath(player)
            cooldownManager.startCooldown(player, "칼리", 60L) // 10초 쿨타임

        }
    }

    private fun activateKaliWrath(player: Player) {
        val location = player.location
        player.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 10 * 20, 1))
        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 10 * 20, 1))

        location.world?.playSound(location, Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f)
        createVisualEffect(player, 10)

        Bukkit.getScheduler().runTaskTimer(JibyeolAbility.instance, Runnable {
            createKaliZone(player)
        }, 0L, 20L)

        Bukkit.getScheduler().runTaskLater(JibyeolAbility.instance, Runnable {
            player.sendMessage("§a칼리의 분노가 끝났습니다!")
        }, 10 * 20L)
    }

    private fun createVisualEffect(player: Player, duration: Int) {
        val location = player.location
        val world = location.world ?: return

        Bukkit.getScheduler().runTaskTimer(JibyeolAbility.instance, Runnable {
            world.spawnParticle(Particle.SMOKE_LARGE, location, 50, 1.0, 1.0, 1.0, 0.1)
            world.spawnParticle(Particle.REDSTONE, location, 50, 1.0, 1.0, 1.0, 0.1)
        }, 0L, 5L)

        Bukkit.getScheduler().runTaskLater(JibyeolAbility.instance, Runnable {}, duration * 20L)
    }

    private fun createKaliZone(player: Player) {
        val radius = 5.0
        val location = player.location
        val world = location.world ?: return
        world.getNearbyEntities(location, radius, radius, radius).forEach { entity ->
            if (entity is Player && entity != player) {
                entity.damage(2.0, player)
                Debuff.addDebuff(entity, Debuff.DebuffType.Silence, 30)
            }
        }

        world.spawnParticle(Particle.PORTAL, location, 100, radius, 0.5, radius, 0.1)
    }

    private fun isKali(player: Player): Boolean {
        val ability = gameManager.getPlayerAbility(player)
        return ability is Kali
    }

}