/**
 * Applic_Head_Food_Client
 *
 * Copyright (C) 2012 Sh1fT
 *
 * This file is part of Applic_Head_Food_Client.
 *
 * Applic_Head_Food_Client is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * Applic_Head_Food_Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Applic_Head_Food_Client; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package applic_head_food_client;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import org.xml.sax.InputSource;
import utils.PropertiesLauncher;

/**
 * Manage an {@link Applic_Head_Food_Client}
 * @author Sh1fT
 */
public class Applic_Head_Food_Client extends JDialog {
    private DefaultListModel ingredientsEntreeListModel;
    private DefaultListModel ingredientsPlatListModel;
    private DefaultListModel ingredientsDessertListModel;
    private DOMCreator DOMCreator;
    private PropertiesLauncher propertiesLauncher;
    private Boolean logged;

    /**
     * Create a new {@link Applic_Head_Food_Client} instance
     * @param parent
     * @param modal 
     */
    public Applic_Head_Food_Client(Frame parent, boolean modal) {
        super(parent, modal);
        this.initComponents();
        this.setIngredientsEntreeListModel(new DefaultListModel());
        this.setIngredientsPlatListModel(new DefaultListModel());
        this.setIngredientsDessertListModel(new DefaultListModel());
        this.ingredientsEntreeList.setModel(this.getIngredientsEntreeListModel());
        this.ingredientsPlatList.setModel(this.getIngredientsPlatListModel());
        this.ingredientsDessertList.setModel(this.getIngredientsDessertListModel());
        this.setDOMCreator(null);
        this.setPropertiesLauncher(new PropertiesLauncher(
                System.getProperty("file.separator") + "properties" +
                System.getProperty("file.separator") + "Applic_Head_Food_Client.properties"));
        this.setLogged(false);
        this.init();
    }

    /**
     * Initialize the form
     */
    public void init() {
        if (this.doLogin()) {
            this.retrieveIngredients(protocols.ProtocolVSMEAP.TYPE_ENTREE,
                    (DefaultComboBoxModel) this.ingredientsEntreeComboBox.getModel());
            this.retrieveIngredients(protocols.ProtocolVSMEAP.TYPE_PLAT,
                    (DefaultComboBoxModel) this.ingredientsPlatComboBox.getModel());
            this.retrieveIngredients(protocols.ProtocolVSMEAP.TYPE_DESSERT,
                    (DefaultComboBoxModel) this.ingredientsDessertComboBox.getModel());
            this.setLogged(true);
        }
    }

    /**
     * Retrieve the ingredients list from the server
     * @param type
     * @param dcbm 
     */
    public void retrieveIngredients(Integer type, DefaultComboBoxModel dcbm) {
        try {
            Socket socket = new Socket(this.getServerAddress(), this.getServerPort());
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            InputSource is = new InputSource(new InputStreamReader(socket.getInputStream()));
            BufferedReader br = new BufferedReader(is.getCharacterStream());
            pw.println("DOWNPROD:" + type);
            String ingredient = null;
            dcbm.removeAllElements();
            while ((ingredient = br.readLine()) != null)
                dcbm.addElement(ingredient);
            br.close();
            pw.close();
            socket.close();
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            System.exit(1);
        }
    }

    /**
     * Do the login
     * @return 
     */
    public Boolean doLogin() {
        try {
            Socket socket = new Socket(this.getServerAddress(), this.getServerPort());
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            InputSource is = new InputSource(new InputStreamReader(socket.getInputStream()));
            BufferedReader br = new BufferedReader(is.getCharacterStream());
            pw.println("LOGINHEAD:"+this.getAuthUsername()+":"+this.getAuthPassword());
            String result = br.readLine();
            if ((result != null) && (result.compareToIgnoreCase("OK") == 0))
                return true;
            return false;
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            System.exit(1);
        }
        return false;
    }

    public DefaultListModel getIngredientsEntreeListModel() {
        return ingredientsEntreeListModel;
    }

    public void setIngredientsEntreeListModel(DefaultListModel ingredientsEntreeListModel) {
        this.ingredientsEntreeListModel = ingredientsEntreeListModel;
    }

    public DefaultListModel getIngredientsPlatListModel() {
        return ingredientsPlatListModel;
    }

    public void setIngredientsPlatListModel(DefaultListModel ingredientsPlatListModel) {
        this.ingredientsPlatListModel = ingredientsPlatListModel;
    }

    public DefaultListModel getIngredientsDessertListModel() {
        return ingredientsDessertListModel;
    }

    public void setIngredientsDessertListModel(DefaultListModel ingredientsDessertListModel) {
        this.ingredientsDessertListModel = ingredientsDessertListModel;
    }

    public DOMCreator getDOMCreator() {
        return DOMCreator;
    }

    public void setDOMCreator(DOMCreator DOMCreator) {
        this.DOMCreator = DOMCreator;
    }

    public PropertiesLauncher getPropertiesLauncher() {
        return propertiesLauncher;
    }

    public void setPropertiesLauncher(PropertiesLauncher propertiesLauncher) {
        this.propertiesLauncher = propertiesLauncher;
    }

    public Boolean getLogged() {
        return logged;
    }

    public void setLogged(Boolean logged) {
        this.logged = logged;
    }

    public Properties getProperties() {
        return this.getPropertiesLauncher().getProperties();
    }

    public String getInputXMLFilename() {
        return this.getProperties().getProperty("inputXMLFilename");
    }

    public String getInputDTDFilename() {
        return this.getProperties().getProperty("inputDTDFilename");
    }

    public String getServerAddress() {
        return this.getProperties().getProperty("serverAddress");
    }

    public Integer getServerPort() {
        return Integer.parseInt(this.getProperties().getProperty("serverPort"));
    }

    public String getAuthUsername() {
        return this.getProperties().getProperty("authUsername");
    }

    public String getAuthPassword() {
        return this.getProperties().getProperty("authPassword");
    }

    public String getNomMenu() {
        return this.nomMenuTextField.getText();
    }

    public String getVedetteDuJour() {
        return this.vedetteDuJourTextField.getText();
    }

    public String getNomEntree() {
        return this.nomEntreeTextField.getText();
    }

    public String getTypeEntree() {
        return this.froidEntreeRadioButton.isSelected() ?
                this.froidEntreeRadioButton.getText() : this.chaudEntreeRadioButton.getText();
    }

    public String getNomPlat() {
        return this.nomPlatTextField.getText();
    }

    public String getTypePlat() {
        return this.froidPlatRadioButton.isSelected() ?
                this.froidPlatRadioButton.getText() : this.chaudPlatRadioButton.getText();
    }

    public String getNomDessert() {
        return this.nomDessertTextField.getText();
    }

    public String getTypeDessert() {
        return this.froidDessertRadioButton.isSelected() ?
                this.froidDessertRadioButton.getText() : this.chaudDessertRadioButton.getText();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        typeEntreeButtonGroup = new javax.swing.ButtonGroup();
        typePlatButtonGroup = new javax.swing.ButtonGroup();
        typeDessertButtonGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nomMenuTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        vedetteDuJourTextField = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        nomEntreeTextField = new javax.swing.JTextField();
        chaudEntreeRadioButton = new javax.swing.JRadioButton();
        froidEntreeRadioButton = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        ingredientsEntreeComboBox = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        quantiteEntreeSpinner = new javax.swing.JSpinner();
        jScrollPane2 = new javax.swing.JScrollPane();
        ingredientsEntreeList = new javax.swing.JList();
        addIngredientEntreeButton = new javax.swing.JButton();
        removeIngredientEntreeButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        nomPlatTextField = new javax.swing.JTextField();
        chaudPlatRadioButton = new javax.swing.JRadioButton();
        froidPlatRadioButton = new javax.swing.JRadioButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        ingredientsPlatComboBox = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        quantitePlatSpinner = new javax.swing.JSpinner();
        jScrollPane3 = new javax.swing.JScrollPane();
        ingredientsPlatList = new javax.swing.JList();
        addIngredientPlatButton = new javax.swing.JButton();
        removeIngredientPlatButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        nomDessertTextField = new javax.swing.JTextField();
        chaudDessertRadioButton = new javax.swing.JRadioButton();
        froidDessertRadioButton = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        ingredientsDessertComboBox = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        quantiteDessertSpinner = new javax.swing.JSpinner();
        jScrollPane1 = new javax.swing.JScrollPane();
        ingredientsDessertList = new javax.swing.JList();
        addIngredientDessertButton = new javax.swing.JButton();
        removeIngredientDessertButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        validateButton = new javax.swing.JButton();

        typeEntreeButtonGroup.add(froidEntreeRadioButton);
        typeEntreeButtonGroup.add(chaudEntreeRadioButton);

        typePlatButtonGroup.add(froidPlatRadioButton);
        typePlatButtonGroup.add(chaudPlatRadioButton);

        typeDessertButtonGroup.add(froidDessertRadioButton);
        typeDessertButtonGroup.add(chaudDessertRadioButton);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Applic_Head_Food_Client");
        setFont(new java.awt.Font("Verdana", 0, 10)); // NOI18N
        setModal(true);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Menu", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 10), java.awt.Color.darkGray)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel1.setText("Nom :");

        nomMenuTextField.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        nomMenuTextField.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel2.setText("Vedette du jour :");

        vedetteDuJourTextField.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        vedetteDuJourTextField.setText("Jean-Claude Dusse");
        vedetteDuJourTextField.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Entrée", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 10), java.awt.Color.darkGray)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel7.setText("Nom :");

        nomEntreeTextField.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        nomEntreeTextField.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chaudEntreeRadioButton.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        chaudEntreeRadioButton.setText("Chaud");
        chaudEntreeRadioButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        froidEntreeRadioButton.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        froidEntreeRadioButton.setText("Froid");
        froidEntreeRadioButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel8.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel8.setText("Type :");

        jLabel9.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel9.setText("Ingrédients :");

        ingredientsEntreeComboBox.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        ingredientsEntreeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Gambas", "Champignons", "Choux", "Courges", "Légumes fruits", "Légumes secs", "Légumes verts", "Piments", "Pommes de terre", "Racines et tubercules", "Salades"}));
        ingredientsEntreeComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel10.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel10.setText("Quantité :");

        quantiteEntreeSpinner.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        quantiteEntreeSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 999, 1));
        quantiteEntreeSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jScrollPane2.setBorder(null);

        ingredientsEntreeList.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        ingredientsEntreeList.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        ingredientsEntreeList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "3 Gambas", "5 Champigons", "3 Courges" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        ingredientsEntreeList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(ingredientsEntreeList);

        addIngredientEntreeButton.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        addIngredientEntreeButton.setText(">>");
        addIngredientEntreeButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        addIngredientEntreeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addIngredientEntreeButtonActionPerformed(evt);
            }
        });

        removeIngredientEntreeButton.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        removeIngredientEntreeButton.setText("<<");
        removeIngredientEntreeButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        removeIngredientEntreeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeIngredientEntreeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(quantiteEntreeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(ingredientsEntreeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(13, 13, 13)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(addIngredientEntreeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(removeIngredientEntreeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(nomEntreeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chaudEntreeRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(froidEntreeRadioButton))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(nomEntreeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(chaudEntreeRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(froidEntreeRadioButton)))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(ingredientsEntreeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addIngredientEntreeButton))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(quantiteEntreeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(removeIngredientEntreeButton)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Plat", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 10), java.awt.Color.darkGray)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel11.setText("Nom :");

        nomPlatTextField.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        nomPlatTextField.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chaudPlatRadioButton.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        chaudPlatRadioButton.setText("Chaud");
        chaudPlatRadioButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        froidPlatRadioButton.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        froidPlatRadioButton.setText("Froid");
        froidPlatRadioButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel12.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel12.setText("Type :");

        jLabel13.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel13.setText("Ingrédients :");

        ingredientsPlatComboBox.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        ingredientsPlatComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Gambas", "Champignons", "Choux", "Courges", "Légumes fruits", "Légumes secs", "Légumes verts", "Piments", "Pommes de terre", "Racines et tubercules", "Salades"}));
        ingredientsPlatComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel14.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel14.setText("Quantité :");

        quantitePlatSpinner.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        quantitePlatSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 999, 1));
        quantitePlatSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jScrollPane3.setBorder(null);

        ingredientsPlatList.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        ingredientsPlatList.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        ingredientsPlatList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "3 Gambas", "5 Champigons", "3 Courges" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        ingredientsPlatList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(ingredientsPlatList);

        addIngredientPlatButton.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        addIngredientPlatButton.setText(">>");
        addIngredientPlatButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        addIngredientPlatButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addIngredientPlatButtonActionPerformed(evt);
            }
        });

        removeIngredientPlatButton.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        removeIngredientPlatButton.setText("<<");
        removeIngredientPlatButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        removeIngredientPlatButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeIngredientPlatButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(18, 18, 18)
                                .addComponent(quantitePlatSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(18, 18, 18)
                                .addComponent(ingredientsPlatComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(13, 13, 13)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(addIngredientPlatButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(removeIngredientPlatButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(18, 18, 18)
                        .addComponent(nomPlatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chaudPlatRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(froidPlatRadioButton))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(nomPlatTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(chaudPlatRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(froidPlatRadioButton)))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(ingredientsPlatComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addIngredientPlatButton))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(quantitePlatSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(removeIngredientPlatButton)))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Dessert", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Verdana", 0, 10), java.awt.Color.darkGray)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel3.setText("Nom :");

        nomDessertTextField.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        nomDessertTextField.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        chaudDessertRadioButton.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        chaudDessertRadioButton.setText("Chaud");
        chaudDessertRadioButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        froidDessertRadioButton.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        froidDessertRadioButton.setText("Froid");
        froidDessertRadioButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel4.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel4.setText("Type :");

        jLabel5.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel5.setText("Ingrédients :");

        ingredientsDessertComboBox.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        ingredientsDessertComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Gambas", "Champignons", "Choux", "Courges", "Légumes fruits", "Légumes secs", "Légumes verts", "Piments", "Pommes de terre", "Racines et tubercules", "Salades"}));
        ingredientsDessertComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel6.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel6.setText("Quantité :");

        quantiteDessertSpinner.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        quantiteDessertSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 999, 1));
        quantiteDessertSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jScrollPane1.setBorder(null);

        ingredientsDessertList.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        ingredientsDessertList.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        ingredientsDessertList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "3 Gambas", "5 Champigons", "3 Courges" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        ingredientsDessertList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(ingredientsDessertList);

        addIngredientDessertButton.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        addIngredientDessertButton.setText(">>");
        addIngredientDessertButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        addIngredientDessertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addIngredientDessertButtonActionPerformed(evt);
            }
        });

        removeIngredientDessertButton.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        removeIngredientDessertButton.setText("<<");
        removeIngredientDessertButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        removeIngredientDessertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeIngredientDessertButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(quantiteDessertSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(ingredientsDessertComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(13, 13, 13)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(addIngredientDessertButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(removeIngredientDessertButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(nomDessertTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chaudDessertRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(froidDessertRadioButton))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(nomDessertTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(chaudDessertRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(froidDessertRadioButton)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(ingredientsDessertComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addIngredientDessertButton))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(quantiteDessertSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(removeIngredientDessertButton)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(nomMenuTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(vedetteDuJourTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nomMenuTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(vedetteDuJourTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        validateButton.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        validateButton.setText("Valider");
        validateButton.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        validateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(validateButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(validateButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addIngredientDessertButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addIngredientDessertButtonActionPerformed
        if (!this.getIngredientsDessertListModel().contains(
                this.quantiteDessertSpinner.getValue().toString()+ " " +
                this.ingredientsDessertComboBox.getSelectedItem().toString())) {
            this.getIngredientsDessertListModel().addElement(
                    this.quantiteDessertSpinner.getValue().toString()+ " " +
                    this.ingredientsDessertComboBox.getSelectedItem().toString());
        }
    }//GEN-LAST:event_addIngredientDessertButtonActionPerformed

    private void removeIngredientDessertButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeIngredientDessertButtonActionPerformed
        if (this.ingredientsDessertList.getSelectedValue() != null) {
            this.getIngredientsDessertListModel().removeElement(
                    this.ingredientsDessertList.getSelectedValue().toString());
        }
    }//GEN-LAST:event_removeIngredientDessertButtonActionPerformed

    private void validateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validateButtonActionPerformed
        try {
            if (this.getLogged()) {
                this.setDOMCreator(new DOMCreator(this.getInputXMLFilename(), this));
                Socket socket = new Socket(this.getServerAddress(), this.getServerPort());
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                BufferedReader br = new BufferedReader(new FileReader(this.getInputXMLFilename()));
                pw.println("UPMENU");
                String ligne = null;
                while ((ligne = br.readLine()) != null)
                    pw.println(ligne);
                br.close();
                pw.close();
                socket.close();
            }
        } catch (UnknownHostException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            System.exit(1);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            System.exit(1);
        }
    }//GEN-LAST:event_validateButtonActionPerformed

    private void removeIngredientEntreeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeIngredientEntreeButtonActionPerformed
        if (this.ingredientsEntreeList.getSelectedValue() != null) {
            this.getIngredientsEntreeListModel().removeElement(
                    this.ingredientsEntreeList.getSelectedValue().toString());
        }
    }//GEN-LAST:event_removeIngredientEntreeButtonActionPerformed

    private void addIngredientPlatButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addIngredientPlatButtonActionPerformed
        if (!this.getIngredientsPlatListModel().contains(
                this.quantitePlatSpinner.getValue().toString()+ " " +
                this.ingredientsPlatComboBox.getSelectedItem().toString())) {
            this.getIngredientsPlatListModel().addElement(
                    this.quantitePlatSpinner.getValue().toString()+ " " +
                    this.ingredientsPlatComboBox.getSelectedItem().toString());
        }
    }//GEN-LAST:event_addIngredientPlatButtonActionPerformed

    private void removeIngredientPlatButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeIngredientPlatButtonActionPerformed
        if (this.ingredientsPlatList.getSelectedValue() != null) {
            this.getIngredientsPlatListModel().removeElement(
                    this.ingredientsPlatList.getSelectedValue().toString());
        }
    }//GEN-LAST:event_removeIngredientPlatButtonActionPerformed

    private void addIngredientEntreeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addIngredientEntreeButtonActionPerformed
        if (!this.getIngredientsEntreeListModel().contains(
                this.quantiteEntreeSpinner.getValue().toString()+ " " +
                this.ingredientsEntreeComboBox.getSelectedItem().toString())) {
            this.getIngredientsEntreeListModel().addElement(
                    this.quantiteEntreeSpinner.getValue().toString()+ " " +
                    this.ingredientsEntreeComboBox.getSelectedItem().toString());
        }
    }//GEN-LAST:event_addIngredientEntreeButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.out.println("Error: " + ex.getLocalizedMessage());
            System.exit(1);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Applic_Head_Food_Client dialog = new Applic_Head_Food_Client(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addIngredientDessertButton;
    private javax.swing.JButton addIngredientEntreeButton;
    private javax.swing.JButton addIngredientPlatButton;
    private javax.swing.JRadioButton chaudDessertRadioButton;
    private javax.swing.JRadioButton chaudEntreeRadioButton;
    private javax.swing.JRadioButton chaudPlatRadioButton;
    private javax.swing.JRadioButton froidDessertRadioButton;
    private javax.swing.JRadioButton froidEntreeRadioButton;
    private javax.swing.JRadioButton froidPlatRadioButton;
    private javax.swing.JComboBox ingredientsDessertComboBox;
    private javax.swing.JList ingredientsDessertList;
    private javax.swing.JComboBox ingredientsEntreeComboBox;
    private javax.swing.JList ingredientsEntreeList;
    private javax.swing.JComboBox ingredientsPlatComboBox;
    private javax.swing.JList ingredientsPlatList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField nomDessertTextField;
    private javax.swing.JTextField nomEntreeTextField;
    private javax.swing.JTextField nomMenuTextField;
    private javax.swing.JTextField nomPlatTextField;
    private javax.swing.JSpinner quantiteDessertSpinner;
    private javax.swing.JSpinner quantiteEntreeSpinner;
    private javax.swing.JSpinner quantitePlatSpinner;
    private javax.swing.JButton removeIngredientDessertButton;
    private javax.swing.JButton removeIngredientEntreeButton;
    private javax.swing.JButton removeIngredientPlatButton;
    private javax.swing.ButtonGroup typeDessertButtonGroup;
    private javax.swing.ButtonGroup typeEntreeButtonGroup;
    private javax.swing.ButtonGroup typePlatButtonGroup;
    private javax.swing.JButton validateButton;
    private javax.swing.JTextField vedetteDuJourTextField;
    // End of variables declaration//GEN-END:variables
}