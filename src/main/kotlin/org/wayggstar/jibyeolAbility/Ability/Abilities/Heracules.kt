package org.wayggstar.jibyeolAbility.Ability.Abilities

import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.wayggstar.jibyeolAbility.Ability.Ability
import org.wayggstar.jibyeolAbility.Debuff.Debuff
import org.wayggstar.jibyeolAbility.GameManger
import org.wayggstar.jibyeolAbility.JibyeolAbility

class Heracules(private val gameManager: GameManger): Ability, Listener {
    override val name: String = "§c헤라클레스"
    override val description: List<String> = listOf(
        "§c힘§7과 §6불굴§7의 §b의지§7를 지닌 §e영웅",
        "§7죽을 위기에 처할 때 3초동안 §f무적§7이 되고",
        "§c힘2§7버프에 걸립니다.(1회용)(§e아이디어§7: §e쪼이§7)"
    )
    override val rank: String = "S"

    private val finishabil: MutableMap<Player, Boolean> = mutableMapOf<Player, Boolean>().withDefault { key: Player -> false}
    private val activeImmunity = mutableSetOf<Player>()

    @EventHandler
    fun onHeracDam(event: EntityDamageEvent){
        val player = event.entity as? Player ?: return
        val health = player.health
        if (finishabil.get(player) == true){return}
        if (!isHeracules(player)){return}
        if (Debuff.hasDebuff(player, Debuff.DebuffType.Silence)) {
            player.sendMessage("§c현재 침묵 상태로 인해 능력을 사용할 수 없습니다!")
            return
        }
        if (activeImmunity.contains(player)) {
            event.isCancelled = true
            return
        }
        if ((health <= event.finalDamage || health <= 2.0)){
            activateAbility(player)
            event.isCancelled = true
        }
        finishabil.put(player, true)
    }

    private fun activateAbility(player: Player){
        player.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 3*20, 1))
        val location = player.location
        location.world?.spawnParticle(Particle.FLAME, location, 100, 1.0, 1.0, 1.0, 0.1)
        location.world?.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f)
        Bukkit.getScheduler().runTaskLater(JibyeolAbility.instance, Runnable {
            activeImmunity.remove(player)
        }, 3 * 20L)
    }

    private fun isHeracules(player: Player): Boolean {
        val ability = gameManager.getPlayerAbility(player)
        return ability is Heracules
    }
}