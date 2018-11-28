/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

/**
 *
 * @author Stefano
 */
public class MainInterface extends javax.swing.JFrame {

    private MyClient client;
    private String hostname, password;
    /**
     * Creates new form MainInterface
     */
    public MainInterface(MyClient client) {
        this.client= client;
        initComponents();
        init();
    }
    
    private void init(){
        sendButton.setEnabled(false);
        messageTextField.setEnabled(false);
        passwordMenuItem.setEnabled(false);
        logoutMenuItem.setEnabled(false);
        addConnection("LOCALHOST", 4000);
        addConnectionFrame.setSize(addConnectionFrame.getPreferredSize());
        getHostnameFrame.setSize(getHostnameFrame.getPreferredSize());
        getPasswordFrame.setSize(getPasswordFrame.getPreferredSize());
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    private void correttoreTextFildInt(JTextField textfield){
        String intero= "", stringa= textfield.getText().trim();
        for(Character c : stringa.toCharArray()) if(48 <= c.hashCode() && c.hashCode() <= 57) intero+= c;
        textfield.setText(intero);
    }
    
    private void correttoreTextFildAddress(JTextField textfield){
        String address= "", stringa= textfield.getText().trim();
        for(Character c : stringa.toCharArray()) if(c.hashCode() == 46 || (48 <= c.hashCode() && c.hashCode() <= 57)) address+= c;
        textfield.setText(address);
    }
    
    private void addConnection(String address, int port){
        JMenuItem conn= new JMenuItem(address + " " + port);
        conn.setFont(aggiungiMenuItem.getFont());
        conn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                new Thread(){
                    @Override
                    public void run() {
                        client.disconnettiti();
                        logLabel.setText("In connessione... ");
                        try{sleep(10); //per aspettare che i thread concorrenti chiudano la connessione prima che questo lo apra
                        }catch(InterruptedException ex){}
                        client.setAddress(address);
                        client.setPort(port);
                        client.connettiti();
                        if(client.isConnected()) connesso();
                        else logLabel.setText("Connessione fallita ");
                    }
                }.start();
            }
        });
        connessioniMenu.add(conn);
    }
    
    private void connesso(){
        logLabel.setText("Connesso: /" + client.getAddress() + " " + client.getPort() + " ");
        passwordMenuItem.setText("Imposta password per " + client.getAddress() + " ");
        passwordMenuItem.setEnabled(true);
        logoutMenuItem.setEnabled(true);
        messageTextField.setEnabled(true);
        sendButton.setEnabled(true);
    }
    
    public void disconnesso(){
        sendButton.setEnabled(false);
        messageTextField.setEnabled(false);
        logoutMenuItem.setEnabled(false);
        passwordMenuItem.setEnabled(false);
        passwordMenuItem.setText("Imposta password per... ");
        setClientsList(null);
        logLabel.setText("Connessione chiusa ");
    }
    
    public String requestHostname(boolean isValido){
        hostname= null;
        this.setEnabled(false);
        if(isValido) nameLogLabel.setText(" ");
        else nameLogLabel.setText("*Nome non valido ");
        requestNameLabel.setText("Hostname richiesta (" + client.getAddress() + " " + client.getPort() + ")");
        nameTextField.setText("Nome");
        getHostnameFrame.setLocationRelativeTo(this);
        getHostnameFrame.setVisible(true);
        while(!this.isEnabled()){
            try{Thread.sleep(100);
            }catch(InterruptedException ex){}
        }
        return hostname;
    }
    
    public String requestPassword(boolean isValido){
        password= null;
        this.setEnabled(false);
        if(isValido) passLogLabel.setText(" ");
        else passLogLabel.setText("*Password errata ");
        requestPassLabel.setText("Password richiesta (" + client.getAddress() + " " + client.getPort() + ")");
        PassPasswordField.setText("");
        getPasswordFrame.setLocationRelativeTo(this);
        getPasswordFrame.setVisible(true);
        while(!this.isEnabled()){
            try{Thread.sleep(100);
            }catch(InterruptedException ex){}
        }
        return password;
    }
    
    public void setClientsList(String[] clients){
        DefaultListModel model= new DefaultListModel();
        if(clients != null) for(String client : clients) model.addElement(client);
        clientsList.setModel(model);
    }
    
    public void addMessage(String message){
        globalChatTextPane.setText(globalChatTextPane.getText() + message + "\n");
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
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        globalChatTextPane = new javax.swing.JTextPane();
        logLabel = new javax.swing.JLabel();
        messageTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        sendButton = new javax.swing.JButton();
        ClientPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        clientsList = new javax.swing.JList<>();
        jMenuBar1 = new javax.swing.JMenuBar();
        impostazioniMenu = new javax.swing.JMenu();
        connessioniMenu = new javax.swing.JMenu();
        aggiungiMenuItem = new javax.swing.JMenuItem();
        passwordMenuItem = new javax.swing.JMenuItem();
        logoutMenuItem = new javax.swing.JMenuItem();

        addConnectionFrame.setTitle("Impostazioni");
        addConnectionFrame.setResizable(false);
        addConnectionFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                addConnectionFrameComponentHidden(evt);
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

        confermaNameButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        confermaNameButton.setText("Conferma");
        confermaNameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confermaNameButtonActionPerformed(evt);
            }
        });

        nameLogLabel.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        nameLogLabel.setForeground(java.awt.Color.red);
        nameLogLabel.setText(" ");

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
                        .addGap(0, 145, Short.MAX_VALUE)))
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
        passLogLabel.setText(" ");

        PassPasswordField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        PassPasswordField.setAlignmentX(1.0F);

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
                        .addGap(0, 149, Short.MAX_VALUE))
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

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Global Chat");

        globalChatTextPane.setEditable(false);
        globalChatTextPane.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jScrollPane1.setViewportView(globalChatTextPane);

        logLabel.setFont(new java.awt.Font("Tahoma", 2, 18)); // NOI18N
        logLabel.setForeground(java.awt.Color.red);
        logLabel.setText("/ ");

        javax.swing.GroupLayout chatPanelLayout = new javax.swing.GroupLayout(chatPanel);
        chatPanel.setLayout(chatPanelLayout);
        chatPanelLayout.setHorizontalGroup(
            chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(chatPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
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
                    .addComponent(jLabel1)
                    .addComponent(logLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                .addContainerGap())
        );

        messageTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        messageTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                messageTextFieldKeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Invia Messaggio");

        sendButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        sendButton.setText("SEND");
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
                    .addComponent(jLabel2)
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
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(messageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel3.setText("Clients:");

        clientsList.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jScrollPane2.setViewportView(clientsList);

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
                        .addGap(0, 109, Short.MAX_VALUE)))
                .addContainerGap())
        );
        ClientPanelLayout.setVerticalGroup(
            ClientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ClientPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );

        impostazioniMenu.setText("Impostazioni");

        connessioniMenu.setText("Connessioni");

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
    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        String message= messageTextField.getText();
        if(message.trim().length() > 0) client.sendMessage("messaggio:" + message);
        messageTextField.setText("");
        new Thread(){
            @Override
            public void run(){
                sendButton.setEnabled(false);
                try{
                    sleep(500);
                }catch(InterruptedException ex){}
                if(messageTextField.isEnabled()) sendButton.setEnabled(true);
            }
        }.start();
    }//GEN-LAST:event_sendButtonActionPerformed
    private void aggiungiButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aggiungiButtonActionPerformed
        addConnection(addressTextField.getText(), Integer.parseInt(portTextField.getText()));
        addConnectionFrame.setVisible(false);
    }//GEN-LAST:event_aggiungiButtonActionPerformed
    private void annullaConnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annullaConnButtonActionPerformed
        addConnectionFrame.setVisible(false);
    }//GEN-LAST:event_annullaConnButtonActionPerformed
    private void addConnectionFrameComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_addConnectionFrameComponentHidden
        this.setEnabled(true);
        this.requestFocus();
    }//GEN-LAST:event_addConnectionFrameComponentHidden
    private void portTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_portTextFieldFocusLost
        correttoreTextFildInt(portTextField);
    }//GEN-LAST:event_portTextFieldFocusLost
    private void addressTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_addressTextFieldFocusLost
        correttoreTextFildAddress(addressTextField);
    }//GEN-LAST:event_addressTextFieldFocusLost
    private void addressTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_addressTextFieldFocusGained
        addressTextField.selectAll();
    }//GEN-LAST:event_addressTextFieldFocusGained
    private void portTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_portTextFieldFocusGained
        portTextField.selectAll();
    }//GEN-LAST:event_portTextFieldFocusGained
    private void aggiungiMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aggiungiMenuItemActionPerformed
        this.setEnabled(false);
        addConnectionFrame.setLocationRelativeTo(this);
        addConnectionFrame.setVisible(true);
    }//GEN-LAST:event_aggiungiMenuItemActionPerformed
    private void nameTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameTextFieldFocusGained
        nameTextField.selectAll();
    }//GEN-LAST:event_nameTextFieldFocusGained
    private void confermaNameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confermaNameButtonActionPerformed
        hostname= nameTextField.getText();
        getHostnameFrame.setVisible(false);
    }//GEN-LAST:event_confermaNameButtonActionPerformed
    private void getHostnameFrameComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_getHostnameFrameComponentHidden
        this.setEnabled(true);
        this.requestFocus();
    }//GEN-LAST:event_getHostnameFrameComponentHidden
    private void nameTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_nameTextFieldCaretUpdate
        confermaNameButton.setEnabled(false);
        nameLogLabel.setText(" ");
        if(nameTextField.getText().trim().length() > 3) confermaNameButton.setEnabled(true);
        else nameLogLabel.setText("*Almeno 4 caratteri ");
    }//GEN-LAST:event_nameTextFieldCaretUpdate
    private void passwordMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordMenuItemActionPerformed
        new Thread(){
            @Override
            public void run() {
                requestPassword(true);
                if(password != null) client.sendMessage("password:" + password);
            }
        }.start();
    }//GEN-LAST:event_passwordMenuItemActionPerformed
    private void annullaPassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annullaPassButtonActionPerformed
        getPasswordFrame.setVisible(false);
    }//GEN-LAST:event_annullaPassButtonActionPerformed
    private void getPasswordFrameComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_getPasswordFrameComponentHidden
        this.setEnabled(true);
        this.requestFocus();
    }//GEN-LAST:event_getPasswordFrameComponentHidden
    private void confermaPassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confermaPassButtonActionPerformed
        password= String.valueOf(PassPasswordField.getPassword());
        getPasswordFrame.setVisible(false);
    }//GEN-LAST:event_confermaPassButtonActionPerformed
    private void annullaNameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annullaNameButtonActionPerformed
        getHostnameFrame.setVisible(false);
    }//GEN-LAST:event_annullaNameButtonActionPerformed
    private void messageTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_messageTextFieldKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) sendButtonActionPerformed(null);
    }//GEN-LAST:event_messageTextFieldKeyPressed
    private void logoutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutMenuItemActionPerformed
        client.disconnettiti();
    }//GEN-LAST:event_logoutMenuItemActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ClientPanel;
    private javax.swing.JPasswordField PassPasswordField;
    private javax.swing.JFrame addConnectionFrame;
    private javax.swing.JTextField addressTextField;
    private javax.swing.JButton aggiungiButton;
    private javax.swing.JMenuItem aggiungiMenuItem;
    private javax.swing.JButton annullaConnButton;
    private javax.swing.JButton annullaNameButton;
    private javax.swing.JButton annullaPassButton;
    private javax.swing.JPanel chatPanel;
    private javax.swing.JList<String> clientsList;
    private javax.swing.JButton confermaNameButton;
    private javax.swing.JButton confermaPassButton;
    private javax.swing.JMenu connessioniMenu;
    private javax.swing.JFrame getHostnameFrame;
    private javax.swing.JFrame getPasswordFrame;
    private javax.swing.JTextPane globalChatTextPane;
    private javax.swing.JMenu impostazioniMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel logLabel;
    private javax.swing.JMenuItem logoutMenuItem;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField messageTextField;
    private javax.swing.JLabel nameLogLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel passLogLabel;
    private javax.swing.JMenuItem passwordMenuItem;
    private javax.swing.JTextField portTextField;
    private javax.swing.JLabel requestNameLabel;
    private javax.swing.JLabel requestPassLabel;
    private javax.swing.JButton sendButton;
    // End of variables declaration//GEN-END:variables
}
