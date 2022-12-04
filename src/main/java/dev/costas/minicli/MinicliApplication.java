package dev.costas.minicli;

import dev.costas.minicli.annotation.Command;
import dev.costas.minicli.processors.*;
import org.reflections.Reflections;

import java.io.OutputStream;
import java.util.List;

public class MinicliApplication {
	private final ArgumentParser argumentParser;
	private final CommandExecutor commandExecutor;
	private final HelpGenerator helpGenerator;
	private final OutputStream outputStream;

	protected MinicliApplication(ArgumentParser argumentParser, CommandExecutor commandExecutor, HelpGenerator helpGenerator, OutputStream outputStream) {
		this.argumentParser = argumentParser;
		this.commandExecutor = commandExecutor;
		this.helpGenerator = helpGenerator;
		this.outputStream = outputStream;
	}

	public static MinicliApplicationBuilder builder() {
		return new MinicliApplicationBuilder();
	}

	/**
	 * Scans the given package for commands and executes the command with the given arguments.
	 *
	 * @param clazz The package to scan for commands. It also scans subpackages.
	 * @param args  The arguments to pass to the command.
	 */
	public void run(Class<?> clazz, String[] args) {
		var classes = getCommands(clazz.getPackageName());

		var invocation = argumentParser.parse(args);

		if (invocation.command == null || invocation.command.isBlank() || invocation.command.equals("help")) {
			this.helpGenerator.show(classes, outputStream, " - ");
			return;
		}

		var commandClass = classes.stream()
				.filter(c -> {
					var name = c.getAnnotation(Command.class).name().toLowerCase();
					var shortName = c.getAnnotation(Command.class).shortname().toLowerCase();
					return name.equals(invocation.command) || shortName.equals(invocation.command);
				})
				.findFirst();

		if (commandClass.isEmpty()) {
			this.helpGenerator.show(classes, outputStream, " - ");
		} else {
			commandExecutor.execute(commandClass.get(), invocation);
		}
	}

	/**
	 * Gets all classes in classpath that are annotated with @Command
	 *
	 * @param prefix The package to scan for commands.
	 */
	protected List<Class<?>> getCommands(String prefix) {
		var reflections = new Reflections(prefix);
		return reflections.getTypesAnnotatedWith(Command.class).stream().toList();
	}

}