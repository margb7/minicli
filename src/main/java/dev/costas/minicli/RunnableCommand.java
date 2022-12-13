package dev.costas.minicli;

import dev.costas.minicli.annotation.OnInvoke;
import dev.costas.minicli.models.CommandOutput;

/**
 * Interface to be implemented by commands that can be invoked. If a command doesn't implement this
 * interface, it will be ignored.
 */
public interface RunnableCommand {
	@OnInvoke
	CommandOutput run();
}