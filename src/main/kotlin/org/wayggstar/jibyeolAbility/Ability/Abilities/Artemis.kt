package org.wayggstar.jibyeolWar.Ability.Abilities

import org.bukkit.Bukkit
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.wayggstar.jibyeolWar.Ability.Ability
import org.wayggstar.jibyeolWar.GameManager
import org.wayggstar.jibyeolWar.JibyeolWar

class Artemis: Ability, Listener {
    private lateinit var gameManager: GameManager

    override val name: String = "아르테미스"
    override val description: List<String> = listOf(
        "화살을 적에게 맞출시에 적은 5초간 §b발광§r효과에 걸립니다.",
        "발광효과에 걸린적은 §c받는 피해§r가 10% 증가합니다."
    )
    override val rank: String = "A"

    private val arrowShooters: MutableMap<Arrow, Player> = mutableMapOf()

    @EventHandler
    fun onArrowLaunch(event: ProjectileLaunchEvent) {
        val projectile = event.entity
        if (projectile is Arrow) {
            val shooter = projectile.shooter
            if (shooter is Player) {
                if (!isArtemis(shooter))return
                arrowShooters[projectile] = shooter
            }
        }
    }


    @EventHandler
    fun onEntityHitByArrow(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        val vic = event.entity
        if (damager is Arrow) {
            val shooter = arrowShooters[damager]
            val target = event.entity

            if (shooter != null && target is LivingEntity) {
                target.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 100, 4))
            }
            arrowShooters.remove(damager)
        }
        if (damager is Player){
            if (vic is LivingEntity){
                if (vic.hasPotionEffect(PotionEffectType.GLOWING)){
                    val originalDamage = event.damage
                    val increasedDamage = originalDamage * 1.1
                    event.damage = increasedDamage
                    damager.sendMessage("§a발광 효과로 ${vic.name}에게 추가 피해를 입혔습니다!")
                }
            }
        }
    }

    override fun activate() {
    }

    override fun deactivate() {
    }

    private fun isArtemis(player: Player): Boolean {
        val ability = gameManager.getPlayerAbility(player)
        return ability is Artemis
    }
}