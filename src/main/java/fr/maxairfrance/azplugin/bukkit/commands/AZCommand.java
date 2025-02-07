package fr.maxairfrance.azplugin.bukkit.commands;


import org.bukkit.command.CommandSender;

public interface AZCommand {

    String name();

    String permission();
    String description();

    void execute(CommandSender sender, String[] args);


}
