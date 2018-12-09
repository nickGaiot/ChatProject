/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Stefano
 */
public class MainInterface extends javax.swing.JFrame {

    //attributi dell'interfaccia grafica
    private MyClient client;
    private String hostname, password, chat;
    private long lastTime= 0;
    
    /**
     * Inizializza gli attributi
     * @param client 
     */
    public MainInterface(MyClient client) {
        this.client= client;
        initComponents();
        init();
    }
    
    /**
     * Inizializza altri altributi
     */
    private void init(){
        lockCheckBox.doClick();
        addConnection("LOCALHOST", 4000);
        addConnectionFrame.setSize(addConnectionFrame.getPreferredSize());
        getHostnameFrame.setSize(getHostnameFrame.getPreferredSize());
        getPasswordFrame.setSize(getPasswordFrame.getPreferredSize());
        selectButton.setEnabled(false);
        logoutMenuItem.setEnabled(false);
        disconnesso();
        logLabel.setText("NESSUNA CONNESSIONE SELEZIONATA");
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    /**
     * Aggiunge una connessione al MenuItem,
     * quando verrà selezionata si connetterà
     * @param address l'address della connessione a cui si dovrà connettere
     * @param port la porta della connessione a cui si dovrà connettere
     */
    private void addConnection(String address, int port){
        JMenuItem conn= new JMenuItem(address + " " + port);
        conn.setFont(aggiungiMenuItem.getFont());
        conn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                new Thread(){
                    @Override
                    public void run(){
                        logoutMenuItem.doClick();
                        logLabel.setText("In connessione... ");
                        client.setAddress(address);
                        client.setPort(port);
                        client.connettiti();
                        logoutMenuItem.setEnabled(true);
                        if(!client.isOpen()){
                            logLabel.setText(address + " " + port + ": OFFLINE ");
                        }
                    }
                }.start();
            }
        });
        connessioniMenu.add(conn);
    }
    
    /**
     * Setta gli attributi in modalità connesso
     */
    public void connesso(){
        hostname= client.getHostname();
        setSelectedChat(MyClient.KW_G);
        sendButton.setEnabled(true);
        messageTextField.setEditable(true);
        passwordMenuItem.setEnabled(true);
        logoutMenuItem.setEnabled(true);
        passwordMenuItem.setText("Imposta password per " + client.getAddress() + " ");
        logLabel.setText(client.getAddress() + " " + client.getPort() + ": CONNECTED ");
    }
    
    /**
     * Setta gli attributi in modalità disconnesso
     */
    public void disconnesso(){
        sendButton.setEnabled(false);
        messageTextField.setEditable(false);
        passwordMenuItem.setEnabled(false);
        setSelectedChat(MyClient.KW_G);
        messageTextField.setText("");
        passwordMenuItem.setText("Imposta password per... ");
        logLabel.setText(client.getAddress() + " " + client.getPort() + ": OFFLINE ");
    }
    
    /**
     * Toglie tutti i caratteri che non sono numri
     * @param textfield 
     */
    private void correttoreTextFildInt(JTextField textfield){
        String intero= "", stringa= textfield.getText().trim();
        for(Character c : stringa.toCharArray()) if(48 <= c.hashCode() && c.hashCode() <= 57) intero+= c;
        textfield.setText(intero);
    }
    
    /**
     * Toglie tutti i caratteri che non servono negli address
     * @param textfield 
     */
    private void correttoreTextFildAddress(JTextField textfield){
        String address= "", stringa= textfield.getText().trim();
        for(Character c : stringa.toCharArray()) if(c.hashCode() == 46 || (48 <= c.hashCode() && c.hashCode() <= 57)) address+= c;
        textfield.setText(address);
    }
    
    /**
     * Richide all'utente di inserire un username
     * @param show se mostrare il messaggio <i>'*nome non valido'</i>
     * @return -String se accetta<br>
     * -null se annulla o chiude la finestra
     */
    public String requestHostname(boolean show){
        hostname= null;
        getHostnameFrame.setVisible(true);
        nameLogLabel.setVisible(show);
        while(getHostnameFrame.isVisible()){
            try{Thread.sleep(100);
            }catch(InterruptedException ex){}
        }
        return hostname;
    }
    
    /**
     * Richide all'utente di inserire la passwrd
     * @param show se mostrare il messaggio <i>'*password errata'</i>
     * @return -String se accetta<br>
     * -null se annulla o chiude la finestra
     */
    public String requestPassword(boolean show){
        password= null;
        getPasswordFrame.setVisible(true);
        passLogLabel.setVisible(show);
        while(getPasswordFrame.isVisible()){
            try{Thread.sleep(100);
            }catch(InterruptedException ex){}
        }
        return password;
    }
    
    /**
     * Aggiunge il messaggio alla fine della textArea;
     * lo aggiunge se il messaggio include nella chat selezionata,
     * o se l'utente ha selezionato <i>allChatsCheckBox</i>
     * @param message il messaggio da aggiugere
     */
    public void addMessage(String[] message){
        String[] wClient;
        String writer, recipient, addrWriter= "", time, text;
        writer= message[0];
        recipient= message[1];
        time= message[2];
        text= message[3];
        if(isChatMessage(writer, recipient)){
            wClient= client.getClientByName(writer);
            if(addrCheckBox.isSelected() && wClient != null) addrWriter= wClient[1];
            if(destCheckBox.isSelected()) recipient= "[" + recipient + "] ";
            else recipient= "";
            time= parseTime(Long.parseLong(time));
            chatTextArea.insert(time + recipient + writer + addrWriter + ": " + text + "\n", chatTextArea.getDocument().getLength());
        }
    }
    
    /**
     * Ricarica i messaggi,
     * utilizzato quando un client viene aggiornato,
     * o quando le impostazioni della finestra cambiano,
     * o quando cambia la chat selezionata
     */
    public void reloadMessages(){
        String[][] messaggi= client.getMessaggi();
        chatTextArea.setText("");
        lastTime= 0;
        for(String[] message : messaggi){
            addMessage(message);
        }
    }

    /**
     * Ritorna il tempo formattato in stringa:
     * se l'ultimo messaggio è dello stesso giorno ritorna l'ora e i minuti,
     * altrimenti ritorna anche il giorno
     * @param time tempo in millisecondi
     * @return -string
     */
    private String parseTime(long time){
        String text= "";
        long day= 86400000, hour= 3600000;
        long delayDay= day - ((lastTime + hour) % day)/*, delayHour= lastTime % hour*/;
        Calendar date= Calendar.getInstance();
        date.setTimeInMillis(time);
        if(time > lastTime + delayDay) text+= "   " + date.get(date.DATE) + "/" + (date.get(date.MONTH) + 1) + "/" + date.get(date.YEAR) + "\n";
        /*if(time > lastTime + delayHour) */text+= date.get(date.HOUR_OF_DAY) + ":" + date.get(date.MINUTE) + " ";
        if(time > lastTime) lastTime= time;
        return text;
    }
    
    /**
     * Ricarica la lista dei clients
     */
    public void reloadClients(){
        DefaultListModel model= new DefaultListModel();
        clientsList.setModel(model);
        String[][] clients= client.getClients();
        String hostname, state;
        for(String[] client : clients){
            hostname= client[0];
            state= client[2];
            if(state.equals(MyClient.S_Ban)) state= "(bannato)";
            else if(state.equals(MyClient.S_Dis)) state= "(offline)";
            else state= "";
            model.addElement(hostname + " " + state);
        }
    }
    
    /**
     * Setta la chat, aggiorna le label, ricarica i messaggi
     */
    private void setSelectedChat(String chat){
        this.chat= chat;
        if(allChatsCheckBox.isSelected()) chatLabel.setText("All Chats");
        else if(chat.equals(MyClient.KW_G)) chatLabel.setText("Global Chat");
        else if(chat.equals(MyClient.KW_L)) chatLabel.setText("Local Chat");
        else chatLabel.setText("Private Chat (" + chat + ")");
        messageLabel.setText("Send Message (destinazione " + chat + ")");
        reloadMessages();
    }
    
    /**
     * @param writer il writer del messaggio
     * @param recipient il recipient del messaggio
     * @return -true se il messaggio fa parte della chat selezionata<br>
     * -false altrimenti
     */
    private boolean isChatMessage(String writer, String recipient){
        boolean isValido= false;
        if(allChatsCheckBox.isSelected()) isValido= true;
        else if(isKeyWord(chat) && (writer.equals(chat) || recipient.equals(chat))) isValido= true;
        else if(writer.equals(chat) && recipient.equals(hostname) || writer.equals(hostname) && recipient.equals(chat)) isValido= true;
        return isValido;
    }
    
    /**
     * @param hostname
     * @return -true se i caratteri sono compresi tra 4 e 20,
     * e se non è una parola chiave<br>
     * -false altrumenti
     */
    private boolean isHostnameValido(String hostname){
        if(hostname.trim().length() < 4 || hostname.length() > 20) return false;
        if(isKeyWord(hostname)) return false;
        return true;
    }
    
    /**
     * @param string
     * @return -true se è una parola chiave<br>
     * -false altrimenti
     */
    private boolean isKeyWord(String string){
        if(string.equalsIgnoreCase(MyClient.KW_S) || string.equalsIgnoreCase(MyClient.KW_C)
                || string.equalsIgnoreCase(MyClient.KW_L) || string.equalsIgnoreCase(MyClient.KW_G)) return true;
        return false;
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addConnectionFrame = new javax.swing.JFrame();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        addressTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        portTextField = new javax.swing.JTextField();
        aggiungiButton = new javax.swing.JButton();
        annullaConnButton = new javax.swing.JButton();
        getHostnameFrame = new javax.swing.JFrame();
        requestNameLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        confermaNameButton = new javax.swing.JButton();
        nameLogLabel = new javax.swing.JLabel();
        annullaNameButton = new javax.swing.JButton();
        getPasswordFrame = new javax.swing.JFrame();
        jLabel7 = new javax.swing.JLabel();
        confermaPassButton = new javax.swing.JButton();
        annullaPassButton = new javax.swing.JButton();
        requestPassLabel = new javax.swing.JLabel();
        passLogLabel = new javax.swing.JLabel();
        PassPasswordField = new javax.swing.JPasswordField();
        mainPanel = new javax.swing.JPanel();
        chatPanel = new javax.swing.JPanel();
        chatLabel = new javax.swing.JLabel();
        logLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatTextArea = new javax.swing.JTextArea();
        messageTextField = new javax.swing.JTextField();
        messageLabel = new javax.swing.JLabel();
        sendButton = new javax.swing.JButton();
        ClientPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        clientsList = new javax.swing.JList<>();
        selectButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        impostazioniMenu = new javax.swing.JMenu();
        connessioniMenu = new javax.swing.JMenu();
        aggiungiMenuItem = new javax.swing.JMenuItem();
        passwordMenuItem = new javax.swing.JMenuItem();
        logoutMenuItem = new javax.swing.JMenuItem();
        shermataMenu = new javax.swing.JMenu();
        addrCheckBox = new javax.swing.JCheckBoxMenuItem();
        destCheckBox = new javax.swing.JCheckBoxMenuItem();
        allChatsCheckBox = new javax.swing.JCheckBoxMenuItem();
        lockCheckBox = new javax.swing.JCheckBoxMenuItem();

        addConnectionFrame.setTitle("Impostazioni");
        addConnectionFrame.setResizable(false);
        addConnectionFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                addConnectionFrameComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                addConnectionFrameComponentShown(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel4.setText("Attributi Connessione");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText("Address:");

        addressTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        addressTextField.setText("192.168.1.254");
        addressTextField.setAlignmentX(1.0F);
        addressTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                addressTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                addressTextFieldFocusLost(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Porta:");

        portTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        portTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        portTextField.setText("4000");
        portTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                portTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                portTextFieldFocusLost(evt);
            }
        });

        aggiungiButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        aggiungiButton.setText("Aggiungi");
        aggiungiButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aggiungiButtonActionPerformed(evt);
            }
        });

        annullaConnButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        annullaConnButton.setText("annulla");
        annullaConnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annullaConnButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout addConnectionFrameLayout = new javax.swing.GroupLayout(addConnectionFrame.getContentPane());
        addConnectionFrame.getContentPane().setLayout(addConnectionFrameLayout);
        addConnectionFrameLayout.setHorizontalGroup(
            addConnectionFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addConnectionFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(addConnectionFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(addConnectionFrameLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(addConnectionFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(addConnectionFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addressTextField)
                            .addGroup(addConnectionFrameLayout.createSequentialGroup()
                                .addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(addConnectionFrameLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 132, Short.MAX_VALUE))
                    .addGroup(addConnectionFrameLayout.createSequentialGroup()
                        .addComponent(annullaConnButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(aggiungiButton)))
                .addContainerGap())
        );
        addConnectionFrameLayout.setVerticalGroup(
            addConnectionFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addConnectionFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(addConnectionFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(addressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(addConnectionFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addGroup(addConnectionFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aggiungiButton)
                    .addComponent(annullaConnButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        getHostnameFrame.setTitle("Hostname request");
        getHostnameFrame.setResizable(false);
        getHostnameFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                getHostnameFrameComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                getHostnameFrameComponentShown(evt);
            }
        });

        requestNameLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        requestNameLabel.setText("Titolo");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("Hostname:");

        nameTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        nameTextField.setText("Nome");
        nameTextField.setAlignmentX(1.0F);
        nameTextField.setAutoscrolls(false);
        nameTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                nameTextFieldCaretUpdate(evt);
            }
        });
        nameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nameTextFieldFocusGained(evt);
            }
        });
        nameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nameTextFieldKeyPressed(evt);
            }
        });

        confermaNameButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        confermaNameButton.setText("Conferma");
        confermaNameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confermaNameButtonActionPerformed(evt);
            }
        });

        nameLogLabel.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        nameLogLabel.setForeground(java.awt.Color.red);
        nameLogLabel.setText("*Nome non valido ");

        annullaNameButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        annullaNameButton.setText("annulla");
        annullaNameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annullaNameButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout getHostnameFrameLayout = new javax.swing.GroupLayout(getHostnameFrame.getContentPane());
        getHostnameFrame.getContentPane().setLayout(getHostnameFrameLayout);
        getHostnameFrameLayout.setHorizontalGroup(
            getHostnameFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(getHostnameFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(getHostnameFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(getHostnameFrameLayout.createSequentialGroup()
                        .addComponent(annullaNameButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(confermaNameButton))
                    .addGroup(getHostnameFrameLayout.createSequentialGroup()
                        .addGroup(getHostnameFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(requestNameLabel)
                            .addGroup(getHostnameFrameLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nameLogLabel)))
                        .addGap(0, 34, Short.MAX_VALUE)))
                .addContainerGap())
        );
        getHostnameFrameLayout.setVerticalGroup(
            getHostnameFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(getHostnameFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(requestNameLabel)
                .addGap(18, 18, 18)
                .addGroup(getHostnameFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameLogLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addGroup(getHostnameFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(annullaNameButton)
                    .addComponent(confermaNameButton))
                .addContainerGap())
        );

        nameTextField.getAccessibleContext().setAccessibleName("");

        getPasswordFrame.setTitle("Password request");
        getPasswordFrame.setResizable(false);
        getPasswordFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                getPasswordFrameComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                getPasswordFrameComponentShown(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Password:");

        confermaPassButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        confermaPassButton.setText("Conferma");
        confermaPassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confermaPassButtonActionPerformed(evt);
            }
        });

        annullaPassButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        annullaPassButton.setText("annulla");
        annullaPassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annullaPassButtonActionPerformed(evt);
            }
        });

        requestPassLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        requestPassLabel.setText("Titolo");

        passLogLabel.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        passLogLabel.setForeground(java.awt.Color.red);
        passLogLabel.setText("*Password errata ");

        PassPasswordField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        PassPasswordField.setAlignmentX(1.0F);
        PassPasswordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PassPasswordFieldKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout getPasswordFrameLayout = new javax.swing.GroupLayout(getPasswordFrame.getContentPane());
        getPasswordFrame.getContentPane().setLayout(getPasswordFrameLayout);
        getPasswordFrameLayout.setHorizontalGroup(
            getPasswordFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(getPasswordFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(getPasswordFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(getPasswordFrameLayout.createSequentialGroup()
                        .addGroup(getPasswordFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(getPasswordFrameLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(PassPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(passLogLabel))
                            .addComponent(requestPassLabel))
                        .addGap(0, 43, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, getPasswordFrameLayout.createSequentialGroup()
                        .addComponent(annullaPassButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(confermaPassButton)))
                .addContainerGap())
        );
        getPasswordFrameLayout.setVerticalGroup(
            getPasswordFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(getPasswordFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(requestPassLabel)
                .addGap(18, 18, 18)
                .addGroup(getPasswordFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(getPasswordFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(PassPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(passLogLabel))
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                .addGroup(getPasswordFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(confermaPassButton)
                    .addComponent(annullaPassButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Client");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        chatLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        chatLabel.setText("Global Chat");

        logLabel.setFont(new java.awt.Font("Tahoma", 2, 18)); // NOI18N
        logLabel.setForeground(java.awt.Color.red);
        logLabel.setText("/ ");

        chatTextArea.setEditable(false);
        chatTextArea.setColumns(20);
        chatTextArea.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        chatTextArea.setRows(5);
        chatTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                chatTextAreaKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(chatTextArea);

        javax.swing.GroupLayout chatPanelLayout = new javax.swing.GroupLayout(chatPanel);
        chatPanel.setLayout(chatPanelLayout);
        chatPanelLayout.setHorizontalGroup(
            chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(chatPanelLayout.createSequentialGroup()
                        .addComponent(chatLabel)
                        .addGap(18, 18, 18)
                        .addComponent(logLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        chatPanelLayout.setVerticalGroup(
            chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chatLabel)
                    .addComponent(logLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                .addContainerGap())
        );

        messageTextField.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        messageTextField.setNextFocusableComponent(sendButton);
        messageTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                messageTextFieldKeyPressed(evt);
            }
        });

        messageLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        messageLabel.setText("Send message");

        sendButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        sendButton.setText("SEND");
        sendButton.setNextFocusableComponent(messageTextField);
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chatPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(messageLabel)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(messageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 521, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(chatPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(messageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(messageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel3.setText("Clients");

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
        jScrollPane2.setViewportView(clientsList);

        selectButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        selectButton.setText("SELECT/DESELECT CHAT");
        selectButton.setNextFocusableComponent(messageTextField);
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ClientPanelLayout = new javax.swing.GroupLayout(ClientPanel);
        ClientPanel.setLayout(ClientPanelLayout);
        ClientPanelLayout.setHorizontalGroup(
            ClientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ClientPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ClientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(ClientPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(selectButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        ClientPanelLayout.setVerticalGroup(
            ClientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ClientPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        impostazioniMenu.setText("Impostazioni");

        connessioniMenu.setText("Seleziona Connessione");

        aggiungiMenuItem.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        aggiungiMenuItem.setText("+ aggiungi");
        aggiungiMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aggiungiMenuItemActionPerformed(evt);
            }
        });
        connessioniMenu.add(aggiungiMenuItem);

        impostazioniMenu.add(connessioniMenu);

        passwordMenuItem.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        passwordMenuItem.setText("Imposta password per...");
        passwordMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordMenuItemActionPerformed(evt);
            }
        });
        impostazioniMenu.add(passwordMenuItem);

        logoutMenuItem.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        logoutMenuItem.setText("Logout");
        logoutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutMenuItemActionPerformed(evt);
            }
        });
        impostazioniMenu.add(logoutMenuItem);

        jMenuBar1.add(impostazioniMenu);

        shermataMenu.setText("Schermata");

        addrCheckBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        addrCheckBox.setText("Visualizza l'ADDRESS");
        addrCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addrCheckBoxActionPerformed(evt);
            }
        });
        shermataMenu.add(addrCheckBox);

        destCheckBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        destCheckBox.setSelected(true);
        destCheckBox.setText("Visualizza il [DESTINATARIO]");
        destCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                destCheckBoxActionPerformed(evt);
            }
        });
        shermataMenu.add(destCheckBox);

        allChatsCheckBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        allChatsCheckBox.setText("Visualizza TUTTE le CHATS");
        allChatsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allChatsCheckBoxActionPerformed(evt);
            }
        });
        shermataMenu.add(allChatsCheckBox);

        lockCheckBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lockCheckBox.setText("Visualizza sempre l'ultimo messaggio");
        lockCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lockCheckBoxActionPerformed(evt);
            }
        });
        shermataMenu.add(lockCheckBox);

        jMenuBar1.add(shermataMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ClientPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(ClientPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Invia il messagio
     */
    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        String text= messageTextField.getText();
        if(text.trim().length() > 0){
            client.sendMessage(client.getHostname(), chat, text);
        }
        messageTextField.setText("");
    }//GEN-LAST:event_sendButtonActionPerformed
    /**
     * Aggiunge la nuova connessione
     */
    private void aggiungiButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aggiungiButtonActionPerformed
        addConnection(addressTextField.getText(), Integer.parseInt(portTextField.getText()));
        connessioniMenu.getItem(connessioniMenu.getItemCount() - 1).doClick();
        addConnectionFrame.setVisible(false);
    }//GEN-LAST:event_aggiungiButtonActionPerformed
    /**
     * Annulla l'inserimento della nuova connessione
     */
    private void annullaConnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annullaConnButtonActionPerformed
        addConnectionFrame.setVisible(false);
    }//GEN-LAST:event_annullaConnButtonActionPerformed
    /**
     * Riabilita l'interfaccia grafica principale
     */
    private void addConnectionFrameComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_addConnectionFrameComponentHidden
        this.setEnabled(true);
        this.requestFocus();
    }//GEN-LAST:event_addConnectionFrameComponentHidden
    /**
     * Corregge la porta
     */
    private void portTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_portTextFieldFocusLost
        correttoreTextFildInt(portTextField);
    }//GEN-LAST:event_portTextFieldFocusLost
    /**
     * Corregge l'address
     */
    private void addressTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_addressTextFieldFocusLost
        correttoreTextFildAddress(addressTextField);
    }//GEN-LAST:event_addressTextFieldFocusLost
    /**
     * Seleziona il campo
     */
    private void addressTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_addressTextFieldFocusGained
        addressTextField.selectAll();
    }//GEN-LAST:event_addressTextFieldFocusGained
    /**
     * Seleziona il campo
     */
    private void portTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_portTextFieldFocusGained
        portTextField.selectAll();
    }//GEN-LAST:event_portTextFieldFocusGained
    /**
     * Apre la finestra <i>'addConnectionFrame'</i>
     */
    private void aggiungiMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aggiungiMenuItemActionPerformed
        addConnectionFrame.setLocationRelativeTo(this);
        addConnectionFrame.setVisible(true);
    }//GEN-LAST:event_aggiungiMenuItemActionPerformed
    /**
     * Seleziona il campo
     */
    private void nameTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameTextFieldFocusGained
        nameTextField.selectAll();
    }//GEN-LAST:event_nameTextFieldFocusGained
    /**
     * Setta l'hostname con l'hostname inserito dall'utente
     */
    private void confermaNameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confermaNameButtonActionPerformed
        if(isHostnameValido(nameTextField.getText())){
            hostname= nameTextField.getText();
            getHostnameFrame.setVisible(false);
        }
    }//GEN-LAST:event_confermaNameButtonActionPerformed
    /**
     * Riabilita l'interfaccia grafica principale
     */
    private void getHostnameFrameComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_getHostnameFrameComponentHidden
        this.setEnabled(true);
        this.requestFocus();
    }//GEN-LAST:event_getHostnameFrameComponentHidden
    /**
     * Richiede la nuova password e lo invia al server
     */
    private void passwordMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordMenuItemActionPerformed
        new Thread(){
            @Override
            public void run() {
                requestPassword(false);
                if(password != null){
                    client.sendPassword(password);
                }
            }
        }.start();
    }//GEN-LAST:event_passwordMenuItemActionPerformed
    /**
     * Annulla l'inserimento della password
     */
    private void annullaPassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annullaPassButtonActionPerformed
        getPasswordFrame.setVisible(false);
    }//GEN-LAST:event_annullaPassButtonActionPerformed
    /**
     * Riabilita l'interfaccia grafica principale
     */
    private void getPasswordFrameComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_getPasswordFrameComponentHidden
        this.setEnabled(true);
        this.requestFocus();
    }//GEN-LAST:event_getPasswordFrameComponentHidden
    /**
     * Setta la password con la password inserita dall'utente.
     */
    private void confermaPassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confermaPassButtonActionPerformed
        password= String.valueOf(PassPasswordField.getPassword());
        getPasswordFrame.setVisible(false);
    }//GEN-LAST:event_confermaPassButtonActionPerformed
    /**
     * Annulla l'inserimento del hostname.
     */
    private void annullaNameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annullaNameButtonActionPerformed
        getHostnameFrame.setVisible(false);
    }//GEN-LAST:event_annullaNameButtonActionPerformed
    /**
     * Disconnette il client dal server e deseleziona la connessione.
     */
    private void logoutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutMenuItemActionPerformed
        client.disconnettiti();
        client.setAddress(null);
        client.setPort(0);
        client.loadFile();
        reloadMessages();
        reloadClients();
        logoutMenuItem.setEnabled(false);
        logLabel.setText("NESSUNA CONNESSIONE SELEZIONATA");
    }//GEN-LAST:event_logoutMenuItemActionPerformed
    /**
     * Ricarica i messaggi.
     */
    private void addrCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addrCheckBoxActionPerformed
        reloadMessages();
    }//GEN-LAST:event_addrCheckBoxActionPerformed
    /**
     * Controlla se l'hostname è valido,
     * se non lo è mostra il messaggio <i>'*hostname non valido'</i>.
     */
    private void nameTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_nameTextFieldCaretUpdate
        if(isHostnameValido(nameTextField.getText())){
            confermaNameButton.setEnabled(true);
            nameLogLabel.setVisible(false);
        }else{
            confermaNameButton.setEnabled(false);
            nameLogLabel.setVisible(true);
        }
    }//GEN-LAST:event_nameTextFieldCaretUpdate
    /**
     * Al premere di invio fa click al bottone conferma
     */
    private void nameTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nameTextFieldKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) confermaNameButton.doClick();
    }//GEN-LAST:event_nameTextFieldKeyPressed
    /**
     * Al premere di invio effettua un click al bottone conferma
     */
    private void PassPasswordFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PassPasswordFieldKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) confermaPassButton.doClick();
    }//GEN-LAST:event_PassPasswordFieldKeyPressed
    /**
     * Aggiorna i messaggi
     */
    private void destCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_destCheckBoxActionPerformed
        reloadMessages();
    }//GEN-LAST:event_destCheckBoxActionPerformed
    /**
     * Se la chat selezionata è la stessa seleziona la global chat,
     * Chiama la funzione setSelectedChat come parametro la chat selezionata
     */
    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed
        String recipient= clientsList.getSelectedValue().split(" ")[0];
        if(chat.equals(recipient)){
            recipient= MyClient.KW_G;
            clientsList.clearSelection();
        }
        setSelectedChat(recipient);
    }//GEN-LAST:event_selectButtonActionPerformed
    /**
     * Abilita/Disabilita il bottone selectButton,
     * se è selezionato un client lo abilita,
     * altrimenti lo disabilita
     */
    private void clientsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_clientsListValueChanged
        if(clientsList.isSelectionEmpty()) selectButton.setEnabled(false);
        else selectButton.setEnabled(true);
    }//GEN-LAST:event_clientsListValueChanged
    /**
     * Se la checkBox viene selezionata, alla modifica dei messaggi viene visualizzato quello in fondo,
     * altrimenti alla modifica non fa niente
     */
    private void lockCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockCheckBoxActionPerformed
        DefaultCaret caret = (DefaultCaret)chatTextArea.getCaret();
        if(lockCheckBox.isSelected()) caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        else caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
    }//GEN-LAST:event_lockCheckBoxActionPerformed
    /**
     * Chiama la funzione setSelectdChat e come parametro passa la chat selezionata.
     * Utilizzata per aggiornare le label e per ricaricare i messaggi
     */
    private void allChatsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allChatsCheckBoxActionPerformed
        setSelectedChat(chat);
    }//GEN-LAST:event_allChatsCheckBoxActionPerformed
    /**
     * Disattiva il frame principale
     */
    private void addConnectionFrameComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_addConnectionFrameComponentShown
        this.setEnabled(false);
    }//GEN-LAST:event_addConnectionFrameComponentShown
    /**
     * Setta gli attributi a default
     */
    private void getHostnameFrameComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_getHostnameFrameComponentShown
        this.setEnabled(false);
        getHostnameFrame.setLocationRelativeTo(this);
        nameTextField.setText("Nome");
        requestNameLabel.setText("Hostname richiesta (" + client.getAddress() + " " + client.getPort() + ")");
    }//GEN-LAST:event_getHostnameFrameComponentShown
    /**
     * Setta gli attributi a default
     */
    private void getPasswordFrameComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_getPasswordFrameComponentShown
        this.setEnabled(false);
        getPasswordFrame.setLocationRelativeTo(this);
        PassPasswordField.setText("");
        requestPassLabel.setText("Password richiesta (" + client.getAddress() + " " + client.getPort() + ")");
    }//GEN-LAST:event_getPasswordFrameComponentShown
    /**
     * Al premere di invio effettua un click al bottone sendButton
     */
    private void messageTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_messageTextFieldKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) sendButton.doClick();
    }//GEN-LAST:event_messageTextFieldKeyPressed
    /**
     * Al premere un tasto dà il focus a messageTextFild
     */
    private void chatTextAreaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_chatTextAreaKeyPressed
        messageTextField.requestFocus();
    }//GEN-LAST:event_chatTextAreaKeyPressed
    /**
     * Al premere un tasto dà il focus a messageTextFild
     */
    private void clientsListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_clientsListKeyPressed
        messageTextField.requestFocus();
    }//GEN-LAST:event_clientsListKeyPressed
    /**
     * Al chiudere della finestra principale disconnette il client dal server
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        client.disconnettiti();
    }//GEN-LAST:event_formWindowClosing
/*
     */    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ClientPanel;
    private javax.swing.JPasswordField PassPasswordField;
    private javax.swing.JFrame addConnectionFrame;
    private javax.swing.JCheckBoxMenuItem addrCheckBox;
    private javax.swing.JTextField addressTextField;
    private javax.swing.JButton aggiungiButton;
    private javax.swing.JMenuItem aggiungiMenuItem;
    private javax.swing.JCheckBoxMenuItem allChatsCheckBox;
    private javax.swing.JButton annullaConnButton;
    private javax.swing.JButton annullaNameButton;
    private javax.swing.JButton annullaPassButton;
    private javax.swing.JLabel chatLabel;
    private javax.swing.JPanel chatPanel;
    private javax.swing.JTextArea chatTextArea;
    private javax.swing.JList<String> clientsList;
    private javax.swing.JButton confermaNameButton;
    private javax.swing.JButton confermaPassButton;
    private javax.swing.JMenu connessioniMenu;
    private javax.swing.JCheckBoxMenuItem destCheckBox;
    private javax.swing.JFrame getHostnameFrame;
    private javax.swing.JFrame getPasswordFrame;
    private javax.swing.JMenu impostazioniMenu;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JCheckBoxMenuItem lockCheckBox;
    private javax.swing.JLabel logLabel;
    private javax.swing.JMenuItem logoutMenuItem;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JTextField messageTextField;
    private javax.swing.JLabel nameLogLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel passLogLabel;
    private javax.swing.JMenuItem passwordMenuItem;
    private javax.swing.JTextField portTextField;
    private javax.swing.JLabel requestNameLabel;
    private javax.swing.JLabel requestPassLabel;
    private javax.swing.JButton selectButton;
    private javax.swing.JButton sendButton;
    private javax.swing.JMenu shermataMenu;
    // End of variables declaration//GEN-END:variables
}
