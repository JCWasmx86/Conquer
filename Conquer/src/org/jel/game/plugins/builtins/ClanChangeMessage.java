package org.jel.game.plugins.builtins;

import org.jel.game.data.City;
import org.jel.game.data.Clan;
import org.jel.game.messages.Message;

public record ClanChangeMessage(City city, Clan oldClan, Clan newClan) implements Message {

}
