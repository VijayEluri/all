package com.oldhu.fileserver.command;

import java.util.Map;

public class CommandFactory
{

	private Map cmdMap;

	public CommandFactory(Map cmdMap)
	{
		this.cmdMap = cmdMap;
	}

	public Command getCommand(byte cmd) throws CommandException
	{
		String cmdstr = String.valueOf((char) cmd);
		Command obj = (Command) cmdMap.get(cmdstr);
		if (obj == null) {
			throw new CommandException("Cannot recognize command: " + Character.toString((char) cmd));
		} else {
			return obj.clone();
		}
//		switch (cmd) {
//			case 'S':
//				return new Save();
//			case 'C':
//				return new Create();
//			case 'Z':
//				return new Dummy();
//		}
	}
}
