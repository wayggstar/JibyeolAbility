package org.wayggstar.jibyeolAbility.Ability.Abilities

import org.bukkit.Bukkit
import org.bukkit.Material
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
import org.wayggstar.jibyeolAbility.GameManger

class Ra(private val gameManager: GameManger, private val cooldownManager: cooldownManager): Ability, Listener {

    override val name: String = "라"
    override val description: List<String> = listOf(
        "§6태양§7의 §e신",
        "§f철괴§7를 우클릭 할시 §6태양 §e빛§7을 강화해 모든적에게 §c불§7을 붙이고",
        "§7자신은 §c재생§73과 §b신속§71이 걸립니다.(§c쿨타임§7: §640§7초)"
    )
    override val rank: String = "A"

    @EventHandler
    fun onIronRightClick(event: PlayerInteractEvent){
        val player = event.player
        val itemInHand = player.inventory.itemInMainHand
        val action = event.action
        if (!isRa(player)){return}
        if ((itemInHand.type == Material.IRON_INGOT) && (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR)){
            if (event.hand != EquipmentSlot.HAND)return
            if (cooldownManager.isOnCooldown(player, "태양")){
                cooldownManager.notifyCooldown(player, "태양")
                return
            }
            Bukkit.getServer().broadcastMessage("§6태양 §e빛§a이 강해집니다.")
            sunRising(player)
            cooldownManager.startCooldown(player, "태양", 40L)
        }
    }

    private fun sunRising(player: Player){
        for (player in Bukkit.getOnlinePlayers()){
            player.fireTicks = 100
        }
        player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 100, 2))
        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 100, 0))
    }
    private fun isRa(player: Player): Boolean {
        val ability = gameManager.getPlayerAbility(player)
        return ability is Ra
    }
}