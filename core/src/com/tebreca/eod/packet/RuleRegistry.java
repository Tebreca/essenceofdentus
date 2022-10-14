package com.tebreca.eod.packet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.tebreca.eod.common.Player;
import com.tebreca.eod.helper.HashRegistry;
import com.tebreca.eod.packet.rules.*;

@Singleton
public class RuleRegistry extends HashRegistry<PacketRule> {

    public static final RuleRegistry instance = new RuleRegistry();


    @Override
    public void addEntries() {
        register(new PacketRule(JoinRule.class, JoinRule.Response.class));
        register(new PacketRule(JoinTeamRule.class, JoinTeamRule.Response.class));
        register(new PacketRule(PlayerListRule.class, Player[].class));
        register(new PacketRule(StartChampSelectRule.class, String[].class));
        register(new PacketRule(SelectChampRule.class, SelectChampRule.Response.class));
        register(new PacketRule(LoadedIntoGameRule.class, LoadedIntoGameRule.Response.class));

        register(new OneWayRule(Player.class));
        register(new OneWayRule(StartPreGameRule.class));
        register(new OneWayRule(StartGameRule.class));
        register(new OneWayRule(TeamSyncRule.class));

    }

    @Inject
    public RuleRegistry() {
        super();
    }

    @Override
    protected PacketRule[] createArray(int size) {
        return new PacketRule[0];
    }

    @Override
    protected Class<PacketRule> getTClass() {
        return PacketRule.class;
    }
}
