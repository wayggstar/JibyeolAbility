package org.wayggstar.jibyeolAbility.Command

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import org.wayggstar.jibyeolAbility.GameManger


class Command(private val gameManger: GameManger): CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            when (args.getOrNull(0)) {
                "시작" -> {
                    if (gameManger.gameplaying){
                        sender.sendMessage("§a게임이 진행중입니다.")
                        return false
                    }
                    gameManger.startGame()
                    return true
                }

                "목록" -> {
                    gameManger.openAbilityGUI(sender)
                    sender.sendMessage("§a능력자 목록을 표시합니다.")
                    return true
                }

                "종료" -> {
                    if (!gameManger.gameplaying){
                        sender.sendMessage("§a게임이 진행중이 아닙니다.")
                        return false
                    }
                    gameManger.endGame()
                    return true
                }

                "수락" -> {
                    val ability = gameManger.getPlayerAbility(sender)
                    if (ability != null) {
                        gameManger.confirmAbility(sender)
                        sender.sendMessage("§a능력이 확정되었습니다. 능력: '${ability.name}'")
                        Bukkit.broadcastMessage("${sender.name}§a님이 능력을 확정했습니다.")
                    } else {
                        sender.sendMessage("§c능력을 선택하지 않았습니다.")
                    }
                    return true
                }

                "거절" -> {
                    gameManger.reassignAbility(sender) // 능력 거절 처리
                    sender.sendMessage("§c능력이 거절되었습니다. 새로운 능력이 재배정되었습니다.")
                    Bukkit.broadcastMessage("${sender.name}§a님이 능력을 거절하고 새로운 능력이 재배정되었습니다.")
                    return true
                }

                null -> {
                    sender.sendMessage("§7==============§e§l지별능력자§r§7==============")
                    sender.sendMessage("§r§7/능력자 시작 §r§7- §7게임을 시작합니다.[§c§lOP전용§r§7]")
                    sender.sendMessage("§r§7/능력자 종료 §r§7- §7게임을 종료합니다.[§c§lOP전용§r§7]")
                    sender.sendMessage("§r§7/능력자 목록 §r§7- §7현재 존재하는 능력목록을 확인합니다.")
                    sender.sendMessage("§r§7/능력자 §a수락§7/§c거절 §r§7- §7능력을 확정할지 거부할지 결정합니다.")
                    sender.sendMessage("§7====================================")
                }
            }
        } else {
            sender.sendMessage("§c이 명령어는 플레이어만 실행가능합니다.")
            return false
        }
        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        if (args.size == 1) {
            return getSuggestions(args[0], listOf("시작", "목록", "종료", "수락", "거절"))
        }
        return mutableListOf()
    }

    private fun getSuggestions(input: String, options: List<String>): MutableList<String> {
        val suggestions = mutableListOf<String>()
        StringUtil.copyPartialMatches(input, options, suggestions)
        return suggestions
    }
}