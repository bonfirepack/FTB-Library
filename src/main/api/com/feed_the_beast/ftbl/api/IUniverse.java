package com.feed_the_beast.ftbl.api;

import com.google.common.base.Preconditions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author LatvianModder
 */
public interface IUniverse
{
	default MinecraftServer getServer()
	{
		return Preconditions.checkNotNull(getOverworld().getMinecraftServer());
	}

	WorldServer getOverworld();

	@Nullable
	INBTSerializable<?> getData(ResourceLocation id);

	Collection<? extends IForgePlayer> getPlayers();

	@Nullable
	IForgePlayer getPlayer(@Nullable Object o);

	Collection<? extends IForgeTeam> getTeams();

	@Nullable
	IForgeTeam getTeam(String id);

	Collection<IForgePlayer> getOnlinePlayers();
}