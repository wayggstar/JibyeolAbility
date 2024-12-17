package org.wayggstar.jibyeolAbility.Ability.Abilities

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
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
import kotlin.random.Random

class Ragnarok (private val gameManager: GameManger, private val cooldownManager: cooldownManager): Ability, Listener {
    override val name: String = "§e라그나로크"
    override val description: List<String> = listOf(
        "§f철괴§7를 우클릭하여 §e라그나로크§7를 발동하면 세상이 §5혼돈§7에 빠지고, 모든 생명이,",
        "§7“§b§l운명§7의 §e§l심판§7”을 받는다.",
        "§7주변에 있는 생명체가 랜덤한 효과를 받으며, 결과는 예측할 수 없다."
    )
    override val rank: String = "§6EX"

    @EventHandler
    fun onIronRightClick(event: PlayerInteractEvent){
        val player = event.player
        val itemInHand = player.inventory.itemInMainHand
        val action = event.action
        if (!isRagnarok(player)){return}
        if ((itemInHand.type == Material.IRON_INGOT) && (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR)){
            if (event.hand != EquipmentSlot.HAND)return
            if (cooldownManager.isOnCooldown(player, "라그나로크")){
                cooldownManager.notifyCooldown(player, "라그나로크")
                return
            }
            cooldownManager.startCooldown(player, "라그나로크", 600L)

        }
    }

    private fun activateRagnarok(player: Player) {
        val world = player.world
        world.playSound(player.location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.5f)
        world.spawnParticle(Particle.EXPLOSION_HUGE, player.location, 5)
        val entities = world.getNearbyEntities(player.location, 20.0, 20.0, 20.0)
        for (entity in entities) {
            if (entity is Player) {
                applyRandomEffect(entity)
                player.sendMessage(ChatColor.RED.toString() + "라그나로크가 발동되었습니다! 운명은 누구도 피할 수 없습니다.")

            }
        }
        player.damage(5.0)
        player.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 200, 1))
    }



    private fun applyRandomEffect(player: Player) {
        val random = Random.nextInt(1, 11)

        when (random) {
            1 -> player.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 1))
            2 -> player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 200, 2))
            3 -> player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 400, 2))
            4 -> player.addPotionEffect(PotionEffect(PotionEffectType.HUNGER, 200, 1))
            5 -> player.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 300, 1))
            6 -> player.addPotionEffect(PotionEffect(PotionEffectType.POISON, 200, 1))
            7 -> player.health = 0.0
            8 -> player.world.strikeLightning(player.location)
            9 -> player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 10))
            10 -> Debuff.addDebuff(player, Debuff.DebuffType.Silence, 100)
            11 -> Debuff.addDebuff(player, Debuff.DebuffType.ReverseControl, 200)
        }

        player.sendMessage(ChatColor.GOLD.toString() + "당신은 라그나로크의 심판을 받았습니다.")
    }

    private fun isRagnarok(player: Player): Boolean {
        val ability = gameManager.getPlayerAbility(player)
        return ability is Ragnarok
    }


}