package com.tebreca.eod.server;

import com.tebreca.eod.common.Player;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ServerUI {

    List<Player> players = new ArrayList<>();

    JFrame jFrame;
    JTextPane console;
    JButton start;
    JTextPane playerlist;

    public ServerUI() {
        jFrame = new JFrame();
        jFrame.setSize(800, 800);
        jFrame.setLayout(null);
        console = new JTextPane();
        console.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setBounds(0, 0, 800, 400);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setAutoscrolls(true);
        jFrame.add(scrollPane);
        playerlist = new JTextPane();
        renderPlayerList();
        playerlist.setEditable(false);
        start = new JButton();
        start.setText("Start Lobby");
        jFrame.add(playerlist);
        jFrame.add(start);
        playerlist.setBounds(0, 400, 400, 400);
        start.setBounds(450, 575, 300, 50);
        jFrame.setTitle("Neo-Coliseum Server UI");

    }

    void addPlayer(Player player){
        players.add(player);
        renderPlayerList();
    }
    void removePlayer(Player player){
        players.remove(player);
        renderPlayerList();
    }

    void renderPlayerList() {
        StringBuilder builder = new StringBuilder();
        builder.append("Players connected (");
        builder.append(players.size()).append('/').append(NeoServer.lobbySize).append(");\n\n");
        players.stream().map(Player::username).forEach(s -> {
            builder.append(s);
            builder.append('\n');
        });
        playerlist.setText(builder.toString());
    }

    public void show() {
        jFrame.setVisible(true);
    }

    public void append(String event) {
        console.setText(console.getText() + event);
    }

    public void subscribeToClick(ActionListener listener){
        start.addActionListener(listener);
    }

}
