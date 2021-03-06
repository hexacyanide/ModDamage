package com.moddamage.external.vault;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.moddamage.StringMatcher;
import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.BaseDataParser;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.FunctionParser;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.parsing.property.Properties;
import com.moddamage.parsing.property.SettableProperty;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class VaultSupport
{
	public static Chat chat;
	public static Economy economy;
	public static EconomyResponse lastResponse;

	public static void setupPermission() {
		RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatProvider == null) {
			return;
		}
		chat = chatProvider.getProvider();

		Properties.register(new SettableProperty<String, Player>("prefix", String.class, Player.class) {

			@Override
			public String get(Player start, EventData data) throws BailException {
				return chat.getPlayerPrefix(start);
			}

			@Override
			public void set(Player start, EventData data, String value) throws BailException {
				chat.setPlayerPrefix(start, value);
			}
		});

		Properties.register(new SettableProperty<String, Player>("suffix", String.class, Player.class) {

			@Override
			public String get(Player start, EventData data) throws BailException {
				return chat.getPlayerSuffix(start);
			}

			@Override
			public void set(Player start, EventData data, String value) throws BailException {
				chat.setPlayerSuffix(start, value);
			}
		});
	}

	public static void setupEcon() {
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider == null) {
        	return;
        }

        economy = economyProvider.getProvider();

		DataProvider.register(Economy.class, Pattern.compile("economy", Pattern.CASE_INSENSITIVE),
				new BaseDataParser<Economy>() {
					@Override
                    public IDataProvider<Economy> parse(EventInfo info, Matcher m, StringMatcher sm) {
						return sm.acceptIf(new IDataProvider<Economy>() {
							@Override
                            public Economy get(EventData data) {
								return economy;
							}

							@Override
                            public Class<? extends Economy> provides() {
								return Economy.class;
							}

							@Override
                            public String toString() {
								return "economy";
							}
						});
					}
				});

		DataProvider.register(String.class, Economy.class, Pattern.compile("_format", Pattern.CASE_INSENSITIVE), new FunctionParser<String, Economy>(Double.class) {
			@Override
            @SuppressWarnings("rawtypes")
			protected IDataProvider<String> makeProvider(EventInfo info, IDataProvider<Economy> economyDP, IDataProvider[] arguments) {
				@SuppressWarnings("unchecked")
				final IDataProvider<Double> balanceDP = arguments[0];

				return new IDataProvider<String>() {
						@Override
                        public String get(EventData data) throws BailException {
							Double balance = balanceDP.get(data);
							if (balance == null) return null;

							return economy.format(balance);
						}

						@Override
                        public Class<? extends String> provides() {
							return String.class;
						}
					};
			}
		});


		DataProvider.register(Bank.class, Object.class, Pattern.compile("bank"), new FunctionParser<Bank, Object>(String.class) {
			@Override
            @SuppressWarnings("rawtypes")
			protected IDataProvider<Bank> makeProvider(EventInfo info, IDataProvider<Object> startDP, IDataProvider[] arguments) {
				@SuppressWarnings("unchecked")
				final IDataProvider<String> nameDP = arguments[0];

				return new IDataProvider<Bank>() {
						@Override
                        public Bank get(EventData data) throws BailException {
							String name = nameDP.get(data);
							if (name == null) return null;

							return new Bank(name);
						}

						@Override
                        public Class<? extends Bank> provides() {
							return Bank.class;
						}
					};
			}
		});

		VaultProperties.register();
		VaultConditionals.register();
	}

	public static void register()
	{
		setupEcon();
		setupPermission();
	}
}
