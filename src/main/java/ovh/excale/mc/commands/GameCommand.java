package ovh.excale.mc.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.annotations.Alias;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Subcommand;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import ovh.excale.mc.UHC;
import ovh.excale.mc.core.Game;
import ovh.excale.mc.utils.UhcWorldUtil;

import java.util.concurrent.Callable;
import java.util.logging.Level;

@Alias("xkuhc")
@Command("uhc")
public class GameCommand {

	private static Callable<Game> gameProvider;

	public static void setGameProvider(Callable<Game> gameProvider) {
		GameCommand.gameProvider = gameProvider;
	}

	@Subcommand("create")
	public static void createGame(CommandSender sender) throws WrapperCommandSyntaxException {

		Game game = UHC.getGame();

		if(game != null)
			CommandAPI.fail("There's already another session");

		try {

			UHC.setGame(gameProvider.call());

		} catch(Exception e) {
			UHC.logger()
					.log(Level.SEVERE, e.getMessage(), e);
			CommandAPI.fail("There has been an internal error");
		}

		sender.sendMessage("Game created");

	}

	@Subcommand("start")
	public static void startGame(CommandSender sender) throws WrapperCommandSyntaxException {

		Game game = UHC.getGame();

		if(game == null)
			CommandAPI.fail("No game found");

		try {

			game.tryStart();

		} catch(IllegalStateException e) {
			CommandAPI.fail(e.getMessage());
		} catch(Exception e) {
			UHC.logger()
					.log(Level.SEVERE, e.getMessage(), e);
			CommandAPI.fail("There has been an internal error");
		}

	}

	@Subcommand("stop")
	public static void stopGame(CommandSender sender) throws WrapperCommandSyntaxException {

		Game game = UHC.getGame();

		if(game == null)
			CommandAPI.fail("No game found");

		try {

			game.stop();
			game.broadcast("Game stopped forcefully.");
			UHC.setGame(null);

		} catch(IllegalStateException e) {
			CommandAPI.fail(e.getMessage());
		} catch(Exception e) {
			UHC.logger()
					.log(Level.SEVERE, e.getMessage(), e);
			CommandAPI.fail("There has been an internal error");
		}

	}

	@Subcommand("dump")
	public static void dump(CommandSender sender) throws WrapperCommandSyntaxException {

		Game game = UHC.getGame();

		if(game == null)
			CommandAPI.fail("No game found");

		StringBuilder sb = new StringBuilder("\n [Game dump]");
		game.dump()
				.forEach((key, value) -> sb.append("\n - ")
						.append(key)
						.append(": ")
						.append(value));

		sb.append('\n');
		sender.sendMessage(sb.toString());

	}

	@Subcommand("clean")
	public static void cleanWorlds(CommandSender sender) {

		UhcWorldUtil.purgeWorlds(count -> sender.sendMessage("Removed " + count + " world(s) from previous instances"));

	}

	@Subcommand("reload")
	public static void reloadConfiguration(CommandSender sender) {

		Plugin plugin = UHC.plugin();
		plugin.reloadConfig();

		UHC.DEBUG = plugin.getConfig()
				.getBoolean("debug", false);

		sender.sendMessage("Configuration reloaded!");

	}

}
