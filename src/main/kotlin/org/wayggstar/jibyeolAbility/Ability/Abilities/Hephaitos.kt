package org.wayggstar.jibyeolAbility.Ability.Abilities

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.wayggstar.jibyeolAbility.Ability.Ability
import org.wayggstar.jibyeolAbility.Ability.cooldownManager
import org.wayggstar.jibyeolAbility.GameManger

class Hephaitos(private val gameManager: GameManger, private var cooldownManager: cooldownManager): Listener, Ability {
    override val name: String = "헤파이토스"
    override val description: List<String> = listOf(
        "§6대장장이§7의 §e신",
        "§f철괴§7를 우클릭할 시 왼손에 있는 장비의 §b인첸트§7를 §6강화§7한다.(§c쿨타임§7: §660§7초)"    )
    override val rank: String = "A"

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player: Player = event.player
        val itemInMainHand = player.inventory.itemInMainHand
        val itemInOffHand = player.inventory.itemInOffHand
        if (!isHephaitos(player)){return}
        if (itemInMainHand.type == Material.IRON_INGOT && (event.action == Action.RIGHT_CLICK_AIR|| event.action == Action.RIGHT_CLICK_BLOCK)) {
            if (itemInOffHand.type != Material.AIR) {
                val itemMeta = itemInOffHand.itemMeta ?: return
                val enchantments = itemInOffHand.enchantments
                if (event.hand != EquipmentSlot.HAND) return
                if (cooldownManager.isOnCooldown(player, "헤파이토스")){
                    cooldownManager.notifyCooldown(player, "헤파이토스")
                    return
                }
                if (enchantments.isNotEmpty()) {
                    for ((enchantment, level) in enchantments) {
                        val newLevel = (level + 1)
                        itemInOffHand.addUnsafeEnchantment(enchantment, newLevel)
                    }
                    player.world.spawnParticle(org.bukkit.Particle.LAVA, player.location, 50, 0.5, 0.5, 0.5, 0.05)
                    player.world.playSound(player.location, Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f)
                    player.sendMessage("§6헤파이스토스§7의 §e축복§7이 왼손 아이템에 적용되었습니다!")
                    cooldownManager.startCooldown(player, "헤파이토스", 60L)
                } else {
                    player.sendMessage("§c왼손에 인챈트된 아이템이 없습니다.")
                }
            } else {
                player.sendMessage("§c왼손에 아이템이 없습니다.")
            }
            event.isCancelled = true
        }
    }

    override fun activate() {
    }

    private fun isHephaitos(player: Player): Boolean {
        val ability = gameManager.getPlayerAbility(player)
        return ability is Hephaitos
    }

}
