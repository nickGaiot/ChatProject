/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jserver;

import java.util.Calendar;
import javax.swing.DefaultListModel;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Stefano
 */
public class MainInterface extends javax.swing.JFrame{

    private MyServer server;
    private long lastTime= 0;
    
    /**
     * Creates new form MainInterface
     */
    public MainInterface(MyServer server) {
        this.server= server;
        initComponents();
        init();
    }
    
    private void init(){
        lockCheckBoxActionPerformed(null);
        banButton.setEnabled(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void addMessage(String[] message){
        String[] wClient;
        String writer, recipient= "", addrWriter= "", time, text;
        writer= message[0];
        recipient= message[1];
        time= message[2];
        text= message[3];
        wClient= server.getClientByName(writer);
        if(addrCheckBox.isSelected() && wClient != null) addrWriter= wClient[1];
        if(destCheckBox.isSelected()) recipient= "[" + recipient + "]";
        else recipient= "";
        time= getTime(Long.parseLong(time));
        chatTextArea.insert(time + recipient + writer + addrWriter + ": " + text + "\n", chatTextArea.getDocument().getLength());
    }
    
    private String getTime(long time){
        String text= "";
        long day= 86400000, hour= 3600000;
        long delayDay= lastTime % day, delayHour= lastTime % hour;
        Calendar date= Calendar.getInstance();
        date.setTimeInMillis(time);
        if(time > lastTime + delayDay) text+= "   " + date.get(date.DATE) + "/" + (date.get(date.MONTH) + 1) + "/" + date.get(date.YEAR);
        if(time > lastTime + delayHour) text+= "   " + date.get(date.HOUR_OF_DAY) + ":" + date.get(date.MINUTE) + "\n";
        if(time > lastTime) lastTime= time;
        return text;
    }
    
    public void reloadClients(){
        DefaultListModel model= new DefaultListModel();
        String[][] clients= server.getClients();
        String hostname, state;
        for(String[] client : clients){
            hostname= client[0];
            state= client[2];
            if(state.equals(MyServer.S_Ban)) state= "(bannato)";
            else if(state.equals(MyServer.S_Dis)) state= "(offline)";
            else state= "";
            model.addElement(hostname + " " + state);
        }
        clientsList.setModel(model);
    }
    
    private void reloadMessages(){
        String[][] messaggi= server.getMessaggi();
        chatTextArea.setText("");
        lastTime= 0;
        for(String[] message : messaggi){
            addMessage(message);
        }
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        clientsPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        clientsList = new javax.swing.JList<>();
        banButton = new javax.swing.JButton();
        chatPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        logLabel = new javax.swing.JLabel();
        messageTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        sendButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        chatTextArea = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        impostazioniMenu = new javax.swing.JMenu();
        addrCheckBox = new javax.swing.JCheckBoxMenuItem();
        schermataMenu = new javax.swing.JMenu();
        destCheckBox = new javax.swing.JCheckBoxMenuItem();
        lockCheckBox = new javax.swing.JCheckBoxMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Server");
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Clients");

        clientsList.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        clientsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                clientsListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(clientsList);

        banButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        banButton.setText("BANNING");
        banButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                banButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout clientsPanelLayout = new javax.swing.GroupLayout(clientsPanel);
        clientsPanel.setLayout(clientsPanelLayout);
        clientsPanelLayout.setHorizontalGroup(
            clientsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(clientsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(clientsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(banButton, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE))
                .addContainerGap())
        );
        clientsPanelLayout.setVerticalGroup(
            clientsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(banButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setText("Global Chat");

        logLabel.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        logLabel.setForeground(java.awt.Color.red);
        logLabel.setText(" ");

        messageTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        messageTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                messageTextFieldKeyPressed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Send message");

        sendButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        sendButton.setText("SEND");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        chatTextArea.setEditable(false);
        chatTextArea.setColumns(20);
        chatTextArea.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        chatTextArea.setRows(5);
        jScrollPane3.setViewportView(chatTextArea);

        javax.swing.GroupLayout chatPanelLayout = new javax.swing.GroupLayout(chatPanel);
        chatPanel.setLayout(chatPanelLayout);
        chatPanelLayout.setHorizontalGroup(
            chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(chatPanelLayout.createSequentialGroup()
                        .addGroup(chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(chatPanelLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(logLabel))
                            .addComponent(jLabel3))
                        .addGap(0, 484, Short.MAX_VALUE))
                    .addGroup(chatPanelLayout.createSequentialGroup()
                        .addComponent(messageTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        chatPanelLayout.setVerticalGroup(
            chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(logLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(messageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(clientsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chatPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(clientsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(chatPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        impostazioniMenu.setText("Impostazioni");

        addrCheckBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        addrCheckBox.setSelected(true);
        addrCheckBox.setText("Mostra/Nascondi l'ADDRESS a TUTTI gli host");
        addrCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addrCheckBoxActionPerformed(evt);
            }
        });
        impostazioniMenu.add(addrCheckBox);

        jMenuBar1.add(impostazioniMenu);

        schermataMenu.setText("Schermata");

        destCheckBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        destCheckBox.setSelected(true);
        destCheckBox.setText("Visualizza il DESTINATARIO");
        destCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                destCheckBoxActionPerformed(evt);
            }
        });
        schermataMenu.add(destCheckBox);

        lockCheckBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lockCheckBox.setSelected(true);
        lockCheckBox.setText("Visualizza sempre l'ultimo messaggio");
        lockCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lockCheckBoxActionPerformed(evt);
            }
        });
        schermataMenu.add(lockCheckBox);

        jMenuBar1.add(schermataMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void banButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_banButtonActionPerformed
        String hostname= clientsList.getSelectedValue().split(" ")[0];
        server.banna(hostname);
    }//GEN-LAST:event_banButtonActionPerformed
    private void clientsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_clientsListValueChanged
        int index= clientsList.getSelectedIndex();
        banButton.setEnabled(false);
        if(index != -1) banButton.setEnabled(true);
    }//GEN-LAST:event_clientsListValueChanged
    private void messageTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_messageTextFieldKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) sendButtonActionPerformed(null);
    }//GEN-LAST:event_messageTextFieldKeyPressed
    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        String message= messageTextField.getText();
        if(message.trim().length() > 0) server.sendMessageAll(MyServer.KW_S, message);
        messageTextField.setText("");
    }//GEN-LAST:event_sendButtonActionPerformed
    private void addrCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addrCheckBoxActionPerformed
        server.setShowAddress(addrCheckBox.isSelected());
        reloadMessages();
    }//GEN-LAST:event_addrCheckBoxActionPerformed
    private void destCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_destCheckBoxActionPerformed
        reloadMessages();
    }//GEN-LAST:event_destCheckBoxActionPerformed
    private void lockCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockCheckBoxActionPerformed
        DefaultCaret caret = (DefaultCaret)chatTextArea.getCaret();
        if(lockCheckBox.isSelected()) caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        else caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
    }//GEN-LAST:event_lockCheckBoxActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBoxMenuItem addrCheckBox;
    private javax.swing.JButton banButton;
    private javax.swing.JPanel chatPanel;
    private javax.swing.JTextArea chatTextArea;
    private javax.swing.JList<String> clientsList;
    private javax.swing.JPanel clientsPanel;
    private javax.swing.JCheckBoxMenuItem destCheckBox;
    private javax.swing.JMenu impostazioniMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JCheckBoxMenuItem lockCheckBox;
    private javax.swing.JLabel logLabel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField messageTextField;
    private javax.swing.JMenu schermataMenu;
    private javax.swing.JButton sendButton;
    // End of variables declaration//GEN-END:variables

}
