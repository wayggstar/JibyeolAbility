package org.wayggstar.jibyeolAbility

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.java.JavaPlugin
import org.wayggstar.jibyeolAbility.Ability.Abilities.Artemis
import org.wayggstar.jibyeolAbility.Ability.Abilities.Herumes
import org.wayggstar.jibyeolAbility.Ability.Abilities.Persepone
import org.wayggstar.jibyeolAbility.Ability.Abilities.Thor
import org.wayggstar.jibyeolAbility.Ability.Ability
import org.wayggstar.jibyeolAbility.Ability.cooldownManager
import java.io.File
import java.io.IOException

class GameManger(private val plugin: JavaPlugin, private val cooldownManager: cooldownManager) {
    private lateinit var abilitiesConfig: FileConfiguration
    public var gameplaying: Boolean = false
    private val playerAbilities: MutableMap<Player, Ability> = mutableMapOf()
    private val confirmedPlayers = mutableListOf<Player>()
    private val abilities: List<Ability> = listOf(
        Thor(this),
        Artemis(this),
        Herumes(this, cooldownManager),
        Persepone(this, cooldownManager)
    )

    init {
        initializeAbilitiesConfig()
    }

    fun startGameIfReady() {
        val players = Bukkit.getOnlinePlayers()
        if (players.size == confirmedPlayers.size) {
            Bukkit.broadcastMessage("§a모든 플레이어가 능력을 확정했습니다. 게임을 시작합니다!")
            startGame()
        }
    }

    fun confirmAbility(player: Player) {
        if (!confirmedPlayers.contains(player)) {
            confirmedPlayers.add(player)
            startGameIfReady()
        }
    }

    fun initializeAbilitiesConfig() {
        val abilitiesFile = File(plugin.dataFolder, "abilities.yml")
        if (!abilitiesFile.exists()) {
            plugin.saveResource("abilities.yml", false)
        }

        abilitiesConfig = YamlConfiguration.loadConfiguration(abilitiesFile)
        abilities.forEach { ability ->
            if (!abilitiesConfig.contains("abilities.${ability.name}")) {
                abilitiesConfig.set("abilities.${ability.name}", true)
            }
        }

        try {
            abilitiesConfig.save(abilitiesFile)
        } catch (e: IOException) {
            plugin.logger.severe("abilities.yml 파일 저장 중 오류 발생: ${e.message}")
        }
    }

    fun isAbilityEnabled(ability: Ability): Boolean {
        return abilitiesConfig.getBoolean("abilities.${ability.name}", true)
    }

    fun assignRandomAbility(player: Player) {
        val enabledAbilities = abilities.filter { isAbilityEnabled(it) }
        if (enabledAbilities.isNotEmpty()) {
            val randomAbility = enabledAbilities.random()
            playerAbilities[player] = randomAbility
            player.sendMessage("§a당신의 능력은 '${randomAbility.name}'입니다!")
            player.sendMessage("§e등급: ${randomAbility.rank}")
            player.sendMessage("§7능력 설명:")
            randomAbility.description.forEach { player.sendMessage("§7- $it") }
            randomAbility.activate()
        } else {
            player.sendMessage("§c활성화된 능력이 없습니다.")
        }
    }

    fun reassignAbility(player: Player) {
        val enabledAbilities = abilities.filter { isAbilityEnabled(it) }
        if (enabledAbilities.isNotEmpty()) {
            val randomAbility = enabledAbilities.random()
            playerAbilities[player] = randomAbility
            player.sendMessage("§a당신의 능력이 다시 재추첨되었습니다. 새로운 능력은 '${randomAbility.name}'입니다!")
            player.sendMessage("§e등급: ${randomAbility.rank}")
            player.sendMessage("§7능력 설명:")
            randomAbility.description.forEach { player.sendMessage("§7- $it") }
            randomAbility.activate()
        } else {
            player.sendMessage("§c활성화된 능력이 없습니다.")
        }
    }

    fun startGame() {
        Bukkit.getOnlinePlayers().forEach { assignRandomAbility(it) }
        gameplaying = true
    }

    fun endGame() {
        Bukkit.getOnlinePlayers().forEach {
            playerAbilities.remove(it)
            gameplaying = false
        }
        Bukkit.broadcastMessage("§c게임이 종료되었습니다!")
    }

    fun openAbilityGUI(player: Player) {
        val inventory: Inventory = Bukkit.createInventory(null, 27, "§a능력자 목록")
        abilities.forEachIndexed { index, ability ->
            val isEnabled = isAbilityEnabled(ability)
            val material = if (isEnabled) Material.LIME_WOOL else Material.RED_WOOL
            val item = ItemStack(material)
            val meta: ItemMeta? = item.itemMeta
            meta?.setDisplayName(if (isEnabled) "§a${ability.name}" else "§c${ability.name}")
            meta?.lore = ability.description.map { "§7$it" }
            item.itemMeta = meta
            inventory.setItem(index, item)
        }
        player.openInventory(inventory)
    }

    @EventHandler
    fun onClickAbilityGUI(event: InventoryClickEvent){
        if (event.view.title == "§a능력자 목록") {
            event.isCancelled = true
        }
    }

    fun getPlayerAbility(player: Player): Ability? {
        return playerAbilities[player]
    }

    fun toggleAbility(ability: Ability) {
        val currentState = abilitiesConfig.getBoolean("abilities.${ability.name}", true)
        abilitiesConfig.set("abilities.${ability.name}", !currentState)
        saveAbilitiesConfig()
    }

    private fun saveAbilitiesConfig() {
        try {
            abilitiesConfig.save(File(plugin.dataFolder, "abilities.yml"))
        } catch (e: IOException) {
            plugin.logger.severe("abilities.yml 파일 저장 중 오류 발생: ${e.message}")
        }
    }
}