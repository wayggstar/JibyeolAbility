package org.wayggstar.jibyeolAbility.Ability.Abilities

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.wayggstar.jibyeolAbility.Ability.Ability
import org.wayggstar.jibyeolAbility.Ability.cooldownManager
import org.wayggstar.jibyeolAbility.Debuff.Debuff
import org.wayggstar.jibyeolAbility.GameManger
import org.wayggstar.jibyeolAbility.JibyeolAbility

class Chukuyomi(private val gameManager: GameManger, private val cooldownManager: cooldownManager): Ability, Listener {
    override val name: String = "§e츠쿠요미"
    override val description: List<String> = listOf(
        "§e달§7의 §e신",
        "§f철괴§7로 적을 타격할 시 §b발광§7효과에 걸고 §5침묵3§7초를 건다.(§c쿨타임§7: §630§7초)",
        "§0밤§7에는 §b신속§7과 §b점프강화 §7그리고 §e야간투시§7가 걸립니다."
        )
    override val rank: String = "A"


    init {
        object : BukkitRunnable() {
            override fun run() {
                giveNightBuffToPlayers()
            }
        }.runTaskTimer(JibyeolAbility.instance, 0L, 200L)  // 200 ticks = 10초마다 실행
    }

    @EventHandler
    fun onIronRightClick(event: EntityDamageByEntityEvent){
        val attacker = event.damager as? Player ?: return
        val target = event.entity as? Player ?: return
        val itemInHand = attacker.inventory.itemInMainHand
        if (!isChukuyomi(attacker)){return}
        if (Debuff.hasDebuff(attacker, Debuff.DebuffType.Silence)) {
            attacker.sendMessage("§c현재 침묵 상태로 인해 능력을 사용할 수 없습니다!")
            return
        }
        if ((itemInHand.type == Material.IRON_INGOT)){
            if (cooldownManager.isOnCooldown(attacker, "달의 침묵")){
                cooldownManager.notifyCooldown(attacker, "달의 침묵")
                return
            }
            cooldownManager.startCooldown(attacker, "달의 침묵", 30L)
            Debuff.addDebuff(target, Debuff.DebuffType.Silence, 60)
            target.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 60, 0))
        }
    }

    private fun giveNightBuffToPlayers() {
        val world: World = Bukkit.getWorlds()[0]
        val isNight = world.time in 13000L..23000L

        if (isNight) {
            for (player in Bukkit.getOnlinePlayers()) {
                if (isChukuyomi(player)) {
                    val nightVision = PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 10, 1)
                    val speed = PotionEffect(PotionEffectType.SPEED, 20 * 10, 1)
                    val jumpboost = PotionEffect(PotionEffectType.JUMP, 20 * 10, 1)
                    player.addPotionEffect(nightVision)
                    player.addPotionEffect(speed)
                    player.addPotionEffect(jumpboost)
                }
            }
        }
    }

    private fun isChukuyomi(player: Player): Boolean {
        val ability = gameManager.getPlayerAbility(player)
        return ability is Chukuyomi
    }
}