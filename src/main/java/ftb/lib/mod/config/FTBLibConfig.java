package ftb.lib.mod.config;

import ftb.lib.FTBLib;
import ftb.lib.api.config.ConfigFile;
import ftb.lib.api.config.ConfigGroup;
import ftb.lib.api.config.ConfigRegistry;

import java.io.File;

public class FTBLibConfig
{
	public static final ConfigFile configFile = new ConfigFile("ftblib");
	
	public static void load()
	{
		configFile.setFile(new File(FTBLib.folderLocal, "ftblib.json"));
		configFile.setDisplayName("FTBLib");
		configFile.add(new ConfigGroup("commands").addAll(FTBLibConfigCmd.class, null, false), false);
		configFile.add(new ConfigGroup("command_names").addAll(FTBLibConfigCmdNames.class, null, false), false);
		ConfigRegistry.add(configFile);
		configFile.load();
	}
}