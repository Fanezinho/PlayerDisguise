package br.com.gmfane.playerdisguise.data.skin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import br.com.gmfane.playerdisguise.Utilitaries;
import br.com.gmfane.playerdisguise.data.skin.SkinFindResult.Result;
import br.com.gmfane.playerdisguise.data.sql.MySQL;
import br.com.gmfane.playerdisguise.data.sql.MySQLQueries;
import br.com.gmfane.playerdisguise.handler.DisguiseHandler;
import br.com.gmfane.playerdisguise.handler.SimpleHandler;
import br.com.gmfane.playerdisguise.player.DisguisePlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.util.com.mojang.authlib.Agent;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.ProfileLookupCallback;
import net.minecraft.util.com.mojang.authlib.properties.Property;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class SkinHandler extends SimpleHandler {

	private final Map<UUID, DisguisePlayer> disguisePlayers;
	private final Cache<String, SkinData> skinCache;

	private int databaseCacheExpiry, localCacheExpiry, localCacheSizeLimit;

	public SkinHandler(DisguiseHandler disguiseHandler) {
		super(disguiseHandler);

		this.databaseCacheExpiry = 86400;
		this.localCacheExpiry = 3600;
		this.localCacheSizeLimit = 50;

		this.disguisePlayers = new ConcurrentHashMap<>(200);
		this.skinCache = CacheBuilder.newBuilder().maximumSize(this.localCacheSizeLimit).expireAfterAccess(this.localCacheExpiry, TimeUnit.SECONDS).build(new SkinDataCacheLoader());
	}

	public boolean initialize() {
		return true;
	}

	public void onDisable() {
		List<DisguisePlayer> disguisePlayers = new ArrayList<DisguisePlayer>(this.disguisePlayers.values());
		List<DisguisePlayer> pendingChange = new ArrayList<DisguisePlayer>();
		for (DisguisePlayer fPlayer : disguisePlayers) {
			if (fPlayer.getCurrentDisguiseName() != null) {
				Player player = fPlayer.getPlayer();
				String originalName = fPlayer.getOriginalName();
				getLogger().log("Reverting '%s' back to '%s'...", player.getName(), originalName);
				if ((Utilitaries.isNameInUse(originalName) && !player.getName().equals(originalName)) || (Utilitaries.isPlayerListNameInUse(originalName) && !player.getPlayerListName().equals(originalName))) {
					try {
						String randomName = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
						getLogger().log("The name '%s' is being used by someone! Temporarily using '%s' instead!", originalName, randomName);
						Utilitaries.updateName(player, randomName, randomName, randomName);
						Utilitaries.updateEntityModel(this, player, false);
						fPlayer.setCurrentDisguiseName(randomName);
						pendingChange.add(fPlayer);
					} catch (Exception e) {
						getLogger().log(Level.SEVERE, e, "An error occurred while executing the temporary rename!");
						player.kickPlayer(ChatColor.RED + "Ocorreu uma falha ao tentar remover seu disfarce!");
					}
					continue;
				}
				Utilitaries.updateName(player, fPlayer.getOriginalName(), null, null);
				Utilitaries.updateEntityModel(this, player, false);
				fPlayer.setCurrentDisguiseName(null);
			}
		}

		disguisePlayers = new ArrayList<DisguisePlayer>(this.disguisePlayers.values());
		for (DisguisePlayer fPlayer : pendingChange) {
			Player player = fPlayer.getPlayer();
			getLogger().log("Reverting temporary '%s' back to '%s'!", player.getName(), fPlayer.getOriginalName());
			Utilitaries.updateName(player, fPlayer.getOriginalName(), null, null);
			Utilitaries.updateEntityModel(this, player, false);
			fPlayer.setCurrentDisguiseName(null);
		}
	}

	public void cacheSkinData(Player player) {
		Property prop = Utilitaries.getTextureProperty(player);
		skinCache.asMap().put(player.getName().toLowerCase(), new SkinData(Utilitaries.unixTimestamp(), prop));
	}

	public SkinData getCachedSkinData(String name) {
		return skinCache.asMap().get(name.toLowerCase());
	}

	public void retrieveSkinData(Player player, String name) {
		Bukkit.getScheduler().runTaskAsynchronously(getDisguiseHandler().getPlugin(), new SkinDataRetriever(player, name));
	}

	public void registerDisguisePlayer(DisguisePlayer fPlayer) {
		this.disguisePlayers.put(fPlayer.getPlayer().getUniqueId(), fPlayer);
	}

	public DisguisePlayer getDisguisePlayer(Player player) {
		return getDisguisePlayer(player.getUniqueId());
	}

	public DisguisePlayer getDisguisePlayer(UUID uniqueId) {
		return disguisePlayers.get(uniqueId);
	}

	public void unregisterDisguisePlayer(DisguisePlayer fPlayer) {
		this.disguisePlayers.remove(fPlayer.getPlayer().getUniqueId());
	}

	public synchronized SkinFindResult findSkinDataByName(String name) {
		PreparedStatement selectStatement = null;
		ResultSet resultSet = null;
		try {
			selectStatement = getMySQL().getConnection().prepareStatement(MySQLQueries.SKIN_CACHE_GET.toString());
			selectStatement.setString(1, name.toLowerCase());
			resultSet = selectStatement.executeQuery();

			if (!resultSet.first()) {
				return new SkinFindResult(Result.NOT_FOUND, null);
			}

			int timestamp = resultSet.getInt("timestamp");
			String compressedData = resultSet.getString("skin_data");

			return new SkinFindResult(Result.FOUND, new SkinData(timestamp, compressedData));
		} catch (Exception e) {
			getLogger().error("An error while trying to retrieve the cached data for '%s'!", e, name);
			return new SkinFindResult(Result.ERROR, null);
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
				}
			}
			if (selectStatement != null) {
				try {
					selectStatement.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public synchronized boolean removeSkinData(String name) {
		PreparedStatement deleteStatement = null;
		try {
			deleteStatement = getMySQL().getConnection().prepareStatement(MySQLQueries.SKIN_CACHE_DELETE.toString());
			deleteStatement.setString(1, name.toLowerCase());
			deleteStatement.executeUpdate();
			return true;
		} catch (Exception e) {
			getLogger().error("An error while trying to delete the cached data for '%s'!", e, name);
			return false;
		} finally {
			if (deleteStatement != null) {
				try {
					deleteStatement.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public synchronized boolean saveSkinData(String name, SkinData skinData) {
		PreparedStatement insertStatement = null;
		try {
			insertStatement = getMySQL().getConnection().prepareStatement(MySQLQueries.SKIN_CACHE_INSERT.toString());
			insertStatement.setString(1, name.toLowerCase());

			String compressedData = skinData.getCompressedData();
			for (int i = 0; i < 2; i++) {
				insertStatement.setInt(i * 2 + 2, Utilitaries.unixTimestamp());
				insertStatement.setString(i * 2 + 3, compressedData);
			}
			insertStatement.executeUpdate();
			return true;
		} catch (Exception e) {
			getLogger().error("An error while trying to save the cached data for '%s'!", e, name);
			return false;
		} finally {
			if (insertStatement != null) {
				try {
					insertStatement.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public Cache<String, SkinData> getSkinCache() {
		return skinCache;
	}

	public Map<UUID, DisguisePlayer> getDisguisePlayers() {
		return disguisePlayers;
	}

	public MySQL getMySQL() {
		return getDisguiseHandler().getDataManager().getMySQL();
	}

	private final class SkinDataCacheLoader extends CacheLoader<String, SkinData> {

		public SkinData load(String name) throws Exception {

			SkinFindResult result = findSkinDataByName(name);
			if (result.getResult() == Result.FOUND) {
				if (result.getSkinData().getTimestamp() + databaseCacheExpiry > Utilitaries.unixTimestamp()) {
					return result.getSkinData();
				}

				removeSkinData(name);
			}

			SkinLookupCallback callback = new SkinLookupCallback();
			MinecraftServer.getServer().getGameProfileRepository().findProfilesByNames(new String[] { name }, Agent.MINECRAFT, callback);

			if (callback.ex != null) {
				getLogger().log("The player '%s' doesn't exist!", name);
				return new SkinData();
			}

			GameProfile profile = callback.profile;
			MinecraftServer.getServer().av().fillProfileProperties(profile, true);
			Collection<Property> textures = profile.getProperties().get("textures");

			if (textures.size() < 1) {
				getLogger().log("Skin data for '%s' is empty!", name);
				return new SkinData();
			}

			SkinData skinData = new SkinData(Utilitaries.unixTimestamp(), textures.iterator().next());
			saveSkinData(name, skinData);
			return skinData;
		}

		private final class SkinLookupCallback implements ProfileLookupCallback {

			private GameProfile profile;
			private Exception ex;

			public void onProfileLookupSucceeded(GameProfile profile) {
				this.profile = profile;
			}

			public void onProfileLookupFailed(GameProfile profile, Exception ex) {
				this.profile = profile;
				this.ex = ex;
			}
		}
	}

	private final class SkinDataRetriever implements Runnable {

		private final Player player;
		private final String name;

		private SkinDataRetriever(Player player, String name) {
			this.player = player;
			this.name = name;
		}

		@Override
		public void run() {
			try {
				SkinData skinData = skinCache.get(name.toLowerCase());
				if (skinData.getProperty() == null) {
					return;
				}

				Bukkit.getScheduler().runTask(getDisguiseHandler().getPlugin(), new Runnable() {
					@Override
					public void run() {
						if (!player.isOnline()) {
							return;
						}

						DisguisePlayer fPlayer = getDisguisePlayer(player);
						if (fPlayer == null) {
							return;
						}

						Utilitaries.updateEntityModel(SkinHandler.this, player, false);
					}
				});
			} catch (Exception e) {
			}
		}
	}

}
