package com.oldhu.fileserver.command;

public abstract class AbstractCommand implements Command
{

	@Override
	public Command clone()
	{
		try {
			return (Command) super.clone();
		} catch (CloneNotSupportedException ex) {
			return null;
		}
	}
}
