package org.wayggstar.jibyeolAbility.Debuff

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class ReverseControlManager : Listener {

    companion object {
        private val reversedPlayers: MutableSet<Player> = mutableSetOf()

        fun addPlayer(player: Player) {
            reversedPlayers.add(player)
        }

        fun removePlayer(player: Player) {
            reversedPlayers.remove(player)
        }

        @EventHandler
        fun onPlayerMove(event: PlayerMoveEvent) {
            val player = event.player
            if (!reversedPlayers.contains(player)) return

            val from: Location = event.from
            val to: Location = event.to ?: return
            val reversed = from.clone().add(from.x - to.x, 0.0, from.z - to.z)
            event.setTo(reversed)
        }
    }
}