package org.wayggstar.jibyeolAbility

import org.bukkit.plugin.java.JavaPlugin
import org.wayggstar.jibyeolAbility.Ability.Abilities.*
import org.wayggstar.jibyeolAbility.Ability.cooldownManager
import org.wayggstar.jibyeolAbility.Command.Command

class JibyeolAbility : JavaPlugin() {

    companion object {
        lateinit var instance: JibyeolAbility
            private set
    }

    private lateinit var gameManager: GameManger
    private lateinit var cooldownManager: cooldownManager
    private lateinit var command: Command

    override fun onEnable() {

        instance = this


        if (!dataFolder.exists()) {
            dataFolder.mkdir()
        }

        cooldownManager = cooldownManager(this)
        gameManager = GameManger(this, cooldownManager)
        command = Command(gameManager)
        gameManager.initializeAbilitiesConfig()
        server.pluginManager.registerEvents(Thor(gameManager), this)
        server.pluginManager.registerEvents(Artemis(gameManager), this)
        server.pluginManager.registerEvents(Persepone(gameManager, cooldownManager), this)
        server.pluginManager.registerEvents(Herumes(gameManager, cooldownManager), this)
        server.pluginManager.registerEvents(Anubis(gameManager, cooldownManager), this)
        server.pluginManager.registerEvents(Hephaitos(gameManager, cooldownManager), this)
        server.pluginManager.registerEvents(Heracules(gameManager), this)
        server.pluginManager.registerEvents(Kali(gameManager, cooldownManager), this)
        server.pluginManager.registerEvents(Kutulu(gameManager,cooldownManager), this)
        server.pluginManager.registerEvents(Ra(gameManager, cooldownManager), this)
        server.pluginManager.registerEvents(Yormungande(gameManager, cooldownManager), this)

        getCommand("능력자")?.setExecutor(Command(gameManager))

        logger.info("JibyeolAbility 플러그인이 활성화되었습니다.")
    }

    override fun onDisable() {
        logger.info("능력자 플러그인이 비활성화되었습니다.")
    }
}
