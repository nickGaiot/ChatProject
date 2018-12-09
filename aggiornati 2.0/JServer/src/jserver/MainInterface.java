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

    //attributi dell'interfaccia grafica
    private MyServer server;
    private long lastTime= 0;
    
    /**
     * Inizializza gli attributi
     * @param server 
     */
    public MainInterface(MyServer server) {
        this.server= server;
        initComponents();
        init();
    }
    
    /**
     * Inizializza altri altributi
     */
    private void init(){
        lockCheckBox.doClick();
        banButton.setEnabled(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Aggiunge il messaggio alla fine della textArea
     * @param message il messaggio da aggiugere
     */
    public void addMessage(String[] message){
        String[] wClient;
        String writer, recipient, addrWriter, time, text;
        writer= message[0];
        recipient= message[1];
        time= message[2];
        text= message[3];
        wClient= server.getClientByName(writer);
        if(addrCheckBox.isSelected() && addrCheckBox1.isSelected() && wClient != null) addrWriter= wClient[1];
        else addrWriter= "";
        if(destCheckBox.isSelected()) recipient= "[To " + recipient + "] ";
        else recipient= "";
        time= parseTime(Long.parseLong(time));
        chatTextArea.insert(time + recipient + writer + addrWriter + ": " + text + "\n", chatTextArea.getDocument().getLength());
    }
    
    /**
     * Ricarica i messaggi,
     * utilizzato quando le impostazioni della finestra cambiano.
     */
    public void reloadMessages(){
        String[][] messaggi= server.getMessages();
        chatTextArea.setText("");
        lastTime= 0;
        for(String[] message : messaggi){
            addMessage(message);
        }
    }
    
    /**
     * Ritorna il tempo formattato in stringa:
     * se l'ultimo messaggio è dello stesso giorno ritorna l'ora e i minuti,
     * altrimenti ritorna anche il giorno.
     * @param time tempo in millisecondi
     * @return -string
     */
    private String parseTime(long time){
        String text= "";
        long day= 86400000, hour= 3600000;
        long delayDay= day - ((lastTime + hour) % day)/*, delayHour= hour - lastTime % hour*/;
        Calendar date= Calendar.getInstance();
        date.setTimeInMillis(time);
        if(time > lastTime + delayDay) text+= "   " + date.get(date.DATE) + "/" + (date.get(date.MONTH) + 1) + "/" + date.get(date.YEAR) + "\n";
        /*if(time > lastTime + delayHour) */text+= date.get(date.HOUR_OF_DAY) + ":" + date.get(date.MINUTE) + " ";
        if(time > lastTime) lastTime= time;
        return text;
    }
    
    /**
     * Ricarica la lista dei clients.
     */
    public void reloadClients(){
        DefaultListModel model= new DefaultListModel();
        String[][] clients= server.getClients();
        String hostname, state;
        for(String[] client : clients){
            hostname= client[0];
            state= client[2];
            if(state.equals(MyServer.S_Ban)) state= "(banned)";
            else if(state.equals(MyServer.S_Dis)) state= "(offline)";
            else state= "";
            model.addElement(hostname + " " + state);
        }
        clientsList.setModel(model);
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
        jMenu1 = new javax.swing.JMenu();
        closeMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        autoSaveCheckBox = new javax.swing.JCheckBoxMenuItem();
        impostazioniMenu = new javax.swing.JMenu();
        addrCheckBox = new javax.swing.JCheckBoxMenuItem();
        schermataMenu = new javax.swing.JMenu();
        addrCheckBox1 = new javax.swing.JCheckBoxMenuItem();
        destCheckBox = new javax.swing.JCheckBoxMenuItem();
        lockCheckBox = new javax.swing.JCheckBoxMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Server");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Clients");

        clientsList.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        clientsList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                clientsListKeyPressed(evt);
            }
        });
        clientsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                clientsListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(clientsList);

        banButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        banButton.setText("BANNING");
        banButton.setNextFocusableComponent(messageTextField);
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

        messageTextField.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        messageTextField.setNextFocusableComponent(sendButton);
        messageTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                messageTextFieldKeyPressed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Send message");

        sendButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        sendButton.setText("SEND");
        sendButton.setNextFocusableComponent(messageTextField);
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        chatTextArea.setEditable(false);
        chatTextArea.setColumns(20);
        chatTextArea.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        chatTextArea.setRows(5);
        chatTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chatTextAreaKeyPressed(evt);
            }
        });
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

        jMenu1.setText("File");

        closeMenuItem.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        closeMenuItem.setText("Close Server");
        closeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(closeMenuItem);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        saveMenuItem.setText("Save All Data");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(saveMenuItem);
        jMenu1.add(jSeparator1);

        autoSaveCheckBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        autoSaveCheckBox.setSelected(true);
        autoSaveCheckBox.setText("Automatically Save at Closing");
        autoSaveCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoSaveCheckBoxActionPerformed(evt);
            }
        });
        jMenu1.add(autoSaveCheckBox);

        jMenuBar1.add(jMenu1);

        impostazioniMenu.setText("Options");

        addrCheckBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        addrCheckBox.setSelected(true);
        addrCheckBox.setText("Addresses Visible to Clients");
        addrCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addrCheckBoxActionPerformed(evt);
            }
        });
        impostazioniMenu.add(addrCheckBox);

        jMenuBar1.add(impostazioniMenu);

        schermataMenu.setText("Preference");

        addrCheckBox1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        addrCheckBox1.setText("Show Address");
        addrCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addrCheckBox1ActionPerformed(evt);
            }
        });
        schermataMenu.add(addrCheckBox1);

        destCheckBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        destCheckBox.setSelected(true);
        destCheckBox.setText("Show [ Recipient ]");
        destCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                destCheckBoxActionPerformed(evt);
            }
        });
        schermataMenu.add(destCheckBox);

        lockCheckBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lockCheckBox.setText("Lock to last Message");
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
    /**
     * Banna il client selezionato.
     */
    private void banButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_banButtonActionPerformed
        int index= clientsList.getSelectedIndex();
        server.banna(index);
    }//GEN-LAST:event_banButtonActionPerformed
    /**
     * Abilita/Disabilita il bottone banButton,
     * se è selezionato un client lo abilita,
     * altrimenti lo disabilita.
     */
    private void clientsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_clientsListValueChanged
        if(clientsList.isSelectionEmpty()) banButton.setEnabled(false);
        else banButton.setEnabled(true);
    }//GEN-LAST:event_clientsListValueChanged
    /**
     * Al premere di invio effettua un click al bottone sendButton.
     */
    private void messageTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_messageTextFieldKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) sendButton.doClick();
    }//GEN-LAST:event_messageTextFieldKeyPressed
    /**
     * Invia il messaggio.
     */
    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        String message= messageTextField.getText();
        if(message.trim().length() > 0) server.sendMessageAll(MyServer.KW_S, message);
        messageTextField.setText("");
    }//GEN-LAST:event_sendButtonActionPerformed
    /**
     * Rinvia i clients aggiornati ai clients e ricarica i messaggi.
     */
    private void addrCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addrCheckBoxActionPerformed
        server.setShowAddress(addrCheckBox.isSelected());
        reloadMessages();
    }//GEN-LAST:event_addrCheckBoxActionPerformed
    /**
     * Ricarica i messaggi.
     */
    private void destCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_destCheckBoxActionPerformed
        reloadMessages();
    }//GEN-LAST:event_destCheckBoxActionPerformed
    /**
     * Se la checkBox viene selezionata, alla modifica dei messaggi viene visualizzato quello in fondo,
     * altrimenti alla modifica non fa niente.
     */
    private void lockCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockCheckBoxActionPerformed
        DefaultCaret caret = (DefaultCaret)chatTextArea.getCaret();
        if(lockCheckBox.isSelected()) caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        else caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
    }//GEN-LAST:event_lockCheckBoxActionPerformed
    /**
     * Al premere un tasto dà il focus a messageTextFild.
     */
    private void chatTextAreaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chatTextAreaKeyPressed
        messageTextField.requestFocus();
    }//GEN-LAST:event_chatTextAreaKeyPressed
    /**
     * Al premere un tasto dà il focus a messageTextFild.
     */
    private void clientsListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_clientsListKeyPressed
        messageTextField.requestFocus();
    }//GEN-LAST:event_clientsListKeyPressed
    /**
     * Chiude salvando nei file le connessioni.
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        server.closeServer();
    }//GEN-LAST:event_formWindowClosing
    /**
     * Setta l'autosave e salva.
     */
    private void autoSaveCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSaveCheckBoxActionPerformed
        server.setAutoSave(autoSaveCheckBox.isSelected());
    }//GEN-LAST:event_autoSaveCheckBoxActionPerformed
    /**
     * Salva.
     */
    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        server.saveFiles();
    }//GEN-LAST:event_saveMenuItemActionPerformed

    /**
     * Chiude l'interfaccia grafica.
     */
    private void closeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeMenuItemActionPerformed
        server.closeServer();
        System.exit(0);
    }//GEN-LAST:event_closeMenuItemActionPerformed
    /**
     * ricarica i messaggi.
     */
    private void addrCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addrCheckBox1ActionPerformed
        reloadMessages();
    }//GEN-LAST:event_addrCheckBox1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBoxMenuItem addrCheckBox;
    private javax.swing.JCheckBoxMenuItem addrCheckBox1;
    private javax.swing.JCheckBoxMenuItem autoSaveCheckBox;
    private javax.swing.JButton banButton;
    private javax.swing.JPanel chatPanel;
    private javax.swing.JTextArea chatTextArea;
    private javax.swing.JList<String> clientsList;
    private javax.swing.JPanel clientsPanel;
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JCheckBoxMenuItem destCheckBox;
    private javax.swing.JMenu impostazioniMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JCheckBoxMenuItem lockCheckBox;
    private javax.swing.JLabel logLabel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField messageTextField;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenu schermataMenu;
    private javax.swing.JButton sendButton;
    // End of variables declaration//GEN-END:variables

}
