package org.wayggstar.jibyeolAbility.Ability.Abilities

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
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
import kotlin.random.Random

class Persepone(private val gameManager: GameManger, private var cooldownManager: cooldownManager): Listener, Ability {
    override val name: String = "페르세포네"
    override val description: List<String> = listOf(
        "§a봄§7과 §5지하§7세계의 §e여신§7이다.",
        "§2독, §0위더, §7데미지를 받지 않습니다.",
        "§f철괴§7를 우클릭할 시 §5죽음§7의 꽃밭을 불러옵니다.(§c쿨타임§7: §630§7초)")
    override val rank: String = "A"

    private val FLOWERS: List<Material> = listOf(
        Material.WITHER_ROSE
    )
    private var deathflower: Boolean = false

    @EventHandler
    fun onIronRightClick(event: PlayerInteractEvent){
        val player = event.player
        val itemInHand = player.inventory.itemInMainHand
        val action = event.action
        if (!isPersepone(player)){return}
        if ((itemInHand.type == Material.IRON_INGOT) && (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR)){
            if (event.hand != EquipmentSlot.HAND)return
            if (cooldownManager.isOnCooldown(player, "페르세포네")){
                cooldownManager.notifyCooldown(player, "페르세포네")
                return
            }
            createDeathGarden(player.location)
            player.sendMessage("§5죽음§7의 꽃밭을 불러옵니다")
            deathflower = true
            cooldownManager.startCooldown(player, "페르세포네", 30L) // 10초 쿨타임

        }
    }

    private fun createDeathGarden(center: Location){
        val world = center.world ?: return
        val radius = 3
        val original = mutableMapOf<Location, Material>()

        for (x in -radius..radius){
            for (z in -radius..radius){
                val blockLocation = center.clone().add(x.toDouble(), -1.0, z.toDouble())
                val block = world.getBlockAt(blockLocation)
                original[blockLocation] = block.type
                block.type = Material.GRASS_BLOCK
            }
        }
        for (x in -radius..radius){
            for (z in -radius..radius){
                val flowerloc = center.clone().add(x.toDouble(), -0.0, z.toDouble())
                val block = world.getBlockAt(flowerloc)
                if (block.type == Material.AIR && Random.nextBoolean()){
                    original[flowerloc] = block.type
                    block.type = FLOWERS.random()
                }
            }
        }
        object : BukkitRunnable() {
            override fun run() {
                applyPoisonEffect(center,radius)
            }
        }.runTaskTimer(JibyeolAbility.instance, 0L, 20L,)

        object  : BukkitRunnable(){
            override fun run() {
                restoreOriginalBlocks(original)
                deathflower = false
            }
        }.runTaskLater(JibyeolAbility.instance, 160L)
    }

    private fun applyPoisonEffect(center: Location, radius: Int) {
        val world = center.world ?: return
        if (!deathflower){return}
        world.players.forEach { player ->
            if (player.location.distance(center) <= radius) {
                player.addPotionEffect(PotionEffect(PotionEffectType.POISON, 60, 1, true, true))
            }
        }
    }

    private fun restoreOriginalBlocks(originalBlocks: Map<Location, Material>) {
        originalBlocks.forEach { (location, material) ->
            val block = location.world?.getBlockAt(location)
            block?.type = material
        }
    }
    private fun isPersepone(player: Player): Boolean {
        val ability = gameManager.getPlayerAbility(player)
        return ability is Persepone
    }

    @EventHandler
    fun onWitherPoison(event: EntityDamageEvent){
        val entity = event.entity
        if (entity is Player){
            val damageCause = event.cause
            if(!isPersepone(entity)){return}
            when (damageCause){
                EntityDamageEvent.DamageCause.POISON -> event.isCancelled = true
                EntityDamageEvent.DamageCause.WITHER -> event.isCancelled = true
                else -> {}
            }
        }
    }
}