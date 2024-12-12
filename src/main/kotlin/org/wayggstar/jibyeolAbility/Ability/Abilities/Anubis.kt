package org.wayggstar.jibyeolAbility.Ability.Abilities

import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.wayggstar.jibyeolAbility.Ability.Ability
import org.wayggstar.jibyeolAbility.Ability.cooldownManager
import org.wayggstar.jibyeolAbility.GameManger
import org.wayggstar.jibyeolAbility.JibyeolAbility

class Anubis(private val gameManager: GameManger, private val cooldownManager: cooldownManager): Ability, Listener {
    override val name: String = "아누비스"
    override val description: List<String> = listOf(
        "§0죽은자§7들의 §5심판자§7",
        "§f철괴§7로 적을 §4타격§7할 시 §c적§7에게 §0죽음§7의 §5심판§7을 내립니다.(§c쿨타임§7: §650§7초)",
        "§0죽음§7의 §5심판§7: 적에게 §2독, §7나약함 5초, §0구속§7, §0어둠 §7256레벨 3초를 부여합니다.",
        "§0죽음§7의 §5심판§7도중 사망한다면 그 적의 §c체력§7일부를 회복합니다.(죽은 적 §c최대체력§7의 20%)"
    )
    override val rank: String = "S"

    @EventHandler
    fun onIronRightClick(event: EntityDamageByEntityEvent){
        val attacker = event.damager as? Player ?: return
        val target = event.entity as? LivingEntity ?: return
        val itemInHand = attacker.inventory.itemInMainHand
        if (!isAnubis(attacker)){return}
        if ((itemInHand.type == Material.IRON_INGOT)){
            if (cooldownManager.isOnCooldown(attacker, "죽음의 심판")){
                cooldownManager.notifyCooldown(attacker, "죽음의 심판")
                return
            }
            attacker.sendMessage("${target.name}에게 §0죽음§7의 §5심판§7을 내립니다.")
            applyDeathJudgment(attacker, target)
            cooldownManager.startCooldown(attacker, "죽음의 심판", 50L)

        }
    }


    private fun applyDeathJudgment(player: Player, target: LivingEntity) {
        applyBindEffect(target)
        applyCorruptionEffect(target)
        if (target.health <= 0) {
            restoreHealth(player, target)
        }

    }

    private fun applyBindEffect(target: LivingEntity) {
        target.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 60, 255, true, true))
        target.addPotionEffect(PotionEffect(PotionEffectType.DARKNESS, 60, 255, true, true))
        startEffect(target)
    }

    private fun applyCorruptionEffect(target: LivingEntity) {
        target.addPotionEffect(PotionEffect(PotionEffectType.POISON, 100, 1, true, false))
        target.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 100, 1, true, false))
    }

    private fun restoreHealth(player: Player, target: LivingEntity) {
        val healingAmount = target.maxHealth * 0.2
        player.health = Math.min(player.health + healingAmount, player.maxHealth)
        player.sendMessage("§c체력§7일부를 회복합니다")
    }

    private fun isAnubis(player: Player): Boolean {
        val ability = gameManager.getPlayerAbility(player)
        return ability is Anubis
    }

    private val radius = 1.0
    private val particleCount = 10

    private fun spawnBlackRedstoneParticles(target: LivingEntity) {
        val location = target.location
        for (i in 0 until particleCount) {
            val angle = Math.PI * 2 * i / particleCount
            val x = radius * Math.cos(angle)
            val z = radius * Math.sin(angle)
            val particleLocation = location.clone().add(x, 1.5, z)
            target.world.spawnParticle(Particle.REDSTONE, particleLocation, 0, 0.0, 0.0, 0.0, 0.1, Color.fromRGB(0, 0, 0))
        }
    }

    fun startEffect(target: LivingEntity) {
        var ticks = 0

        object : BukkitRunnable() {
            override fun run() {
                spawnBlackRedstoneParticles(target)
                ticks++
                if (ticks >= 60) {
                    cancel()
                }
            }
        }.runTaskTimer(JibyeolAbility.instance, 0L, 1L)
    }
}