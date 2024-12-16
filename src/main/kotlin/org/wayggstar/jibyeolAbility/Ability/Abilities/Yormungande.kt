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
import org.wayggstar.jibyeolAbility.Debuff.Debuff
import org.wayggstar.jibyeolAbility.GameManger

class Yormungande(private val gameManager: GameManger, private val cooldownManager: cooldownManager): Ability, Listener {
    override val name: String = "요르문간드"
    override val description: List<String> = listOf(
        "§b세계§7의 §2뱀§7",
        "§f철괴§7를 우클릭할 시 주변적에게 §2독§72를 건다.",
        "§e금괴§7를 우클릭할 시 모든적에게 5초간 §b발광§7을 겁니다.(§c쿨타임§7: §630§7초)",
        "발광효과에 걸린적은 §c받는 근접피해§7가 20% 증가합니다.(§c쿨타임§7: §660§7초)"
    )
    override val rank: String = "S"

    @EventHandler
    fun onIronGoldRightClick(event: PlayerInteractEvent){
        val player = event.player
        val itemInHand = player.inventory.itemInMainHand
        val action = event.action
        if (!isYormungande(player)){return}
        if (Debuff.hasDebuff(player, Debuff.DebuffType.Silence)) {
            player.sendMessage("§c현재 침묵 상태로 인해 능력을 사용할 수 없습니다!")
            return
        }
        if ((itemInHand.type == Material.IRON_INGOT) && (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR)){
            if (event.hand != EquipmentSlot.HAND)return
            if (cooldownManager.isOnCooldown(player, "요르문철")){
                cooldownManager.notifyCooldown(player, "요르문철")
                return
            }
            IronYormun(player)
            cooldownManager.startCooldown(player, "요르문철", 30L)
        }
        if ((itemInHand.type == Material.GOLD_INGOT) && (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR)){
            if (event.hand != EquipmentSlot.HAND)return
            if (cooldownManager.isOnCooldown(player, "요르문금")){
                cooldownManager.notifyCooldown(player, "요르문금")
                return
            }
            GoldYormun(player)
            cooldownManager.startCooldown(player, "요르문금", 30L)
        }
    }

    private fun IronYormun(player: Player) {
        val radius = 10.0
        val location = player.location
        val world = location.world ?: return
        world.getNearbyEntities(location, radius, radius, radius).forEach { target ->
            if (target is Player && target != player) {
                target.addPotionEffect(PotionEffect(PotionEffectType.POISON, 100, 1))
            }
        }
    }

    private fun GoldYormun(player: Player){
        for (target in Bukkit.getOnlinePlayers()){
            if(target == player){
                target.removePotionEffect(PotionEffectType.GLOWING)
            }
            target.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 100, 0))
        }
    }

    private fun isYormungande(player: Player): Boolean {
        val ability = gameManager.getPlayerAbility(player)
        return ability is Yormungande
    }
}