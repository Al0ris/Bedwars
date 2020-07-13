package me.joeseph.bedwars.mechanics;

import me.lucko.helper.Commands;

public class Game {

	private static Generators generators = new Generators();

	public void startGame() {
		generators.generatorMechanics();
	}

	public void startGameCommand() {
		Commands.create()
				.handler(c -> {
					System.out.println("Started Game");
					startGame();
					c.sender().sendMessage("Game Started");
				})
				.register("startgame");
	}
}
