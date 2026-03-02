import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ENIP_form
{
    public Object getAttributeIDLabel;   //Definicia jednotlivych tlacidiel GUI
    private JPanel panel1;
    private JTextArea textArea;
    private JButton CEButton;
    private JButton IDENTIFYButton;
    private JButton a4Button;
    private JButton a2Button;
    private JButton a3Button;
    private JButton a7Button;
    private JButton a5Button;
    private JButton a6Button;
    private JButton a8Button;
    private JButton a0Button;
    private JButton buttonDOT;
    private JButton a9Button;
    private JButton a1Button;

    private final JComboBox<String> serviceButton;
    private final JButton explicitButton;
    private final JButton implicitButton;
    private final JButton quitButton;
    private final JLabel classIDLabel;
    private final JLabel instanceIDLabel;
    private final JLabel attributeIDLabel;
    private final JTextField classIDField;
    private final JTextField instanceIDField;
    private final JTextField attributeIDField;
    private final JButton sendRequesstButton;
    private JTextField attributeListField;
    private JLabel attributeListLabel;
    public JLabel attributeValueLabel = new JLabel("Hodnota:");
    public JTextField attributeValueField = new JTextField();
    private JLabel IO_setupLabel;
    private JTextField outputAssemblyField;
    private JTextField inputAssemblyField;
    private JTextField configAssemblyField;
    private JTextField O_T_rpiField;
    private JTextField T_O_rpiField;
    private JButton set_IO_connectionButton;
    private JButton forwardCloseButton;
    private JPanel IO_panel;


    public ENIP_form()
    {
        textArea.setText("Enter IP address of device: \n");
        ActionListener keyListener = e ->
        {
            JButton selectButton = (JButton) e.getSource();
            textArea.setText(textArea.getText() + selectButton.getText());
        };
        //Umoznuje pouzzivanie jednotlivych tlacidiel GUI - zadavanie do textoveho pola
        a1Button.addActionListener(keyListener);
        a2Button.addActionListener(keyListener);
        a3Button.addActionListener(keyListener);
        a4Button.addActionListener(keyListener);
        a5Button.addActionListener(keyListener);
        a6Button.addActionListener(keyListener);
        a7Button.addActionListener(keyListener);
        a8Button.addActionListener(keyListener);
        a9Button.addActionListener(keyListener);
        a0Button.addActionListener(keyListener);
        buttonDOT.addActionListener(keyListener);
        //Nastavenie tlacidla na vymazavanie textu
        CEButton.addActionListener(_ -> textArea.setText(""));
        //Nastavenie tlacidla na identifikaciu
        IDENTIFYButton.addActionListener(_ ->
        {
            if (IDENTIFYaction != null)
            {
                IDENTIFYaction.run();
            }
        } );
        //Obmedzenie uzivatelskeho vstupu na cislice a bodku
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                if (!Character.isDigit(c) && c != '.')
                {
                    e.consume();
                }
            }
        });

        //Vyber CIP sluzby
        serviceButton = new JComboBox<>();
        serviceButton.addItem("Get_Attribute_Single");
        serviceButton.addItem("Get_Attribute_All");
        serviceButton.addItem("Set_Attribute_Single");
        serviceButton.addItem("Get_Attribute_List");
        serviceButton.addItem("Reset");
        serviceButton.setVisible(false);
        panel1.add(serviceButton);

        explicitButton = new JButton("EXPLICIT");
        implicitButton = new JButton("IMPLICIT");
        explicitButton.setVisible(false);
        implicitButton.setVisible(false);
        panel1.add(explicitButton);
        panel1.add(implicitButton);

        quitButton = new JButton("QUIT");
        quitButton.setVisible(false);
        panel1.add(quitButton);

        classIDLabel = new JLabel("Class ID:");
        instanceIDLabel = new JLabel("Instance ID:");
        attributeIDLabel = new JLabel("Attribute ID:");

        classIDField = new JTextField(5);
        instanceIDField = new JTextField(5);
        attributeIDField = new JTextField(5);

        attributeValueLabel = new JLabel("Value:");
        attributeValueField = new JTextField(10);

        attributeListLabel = new JLabel("Attributes:");
        attributeListField = new JTextField(10);

        sendRequesstButton = new JButton("Send Request");

        IO_setupLabel = new JLabel("Enter I/O connection parameters:");
        outputAssemblyField = new JTextField("0x96", 5);
        inputAssemblyField = new JTextField("0x64", 5);
        configAssemblyField = new JTextField("0x97",5);
        O_T_rpiField = new JTextField("500000", 7);
        T_O_rpiField = new JTextField("500000",7);
        set_IO_connectionButton = new JButton("Forward Open");
        forwardCloseButton = new JButton("Forward Close");
        forwardCloseButton.setVisible(false);

        classIDLabel.setVisible(false);
        instanceIDLabel.setVisible(false);
        attributeIDLabel.setVisible(false);
        classIDField.setVisible(false);
        instanceIDField.setVisible(false);
        attributeIDField.setVisible(false);
        sendRequesstButton.setVisible(false);
        attributeValueLabel.setVisible(false);
        attributeValueField.setVisible(false);
        attributeListLabel.setVisible(false);
        attributeListField.setVisible(false);

        IO_setupLabel.setVisible(false);
        outputAssemblyField.setVisible(false);
        inputAssemblyField.setVisible(false);
        configAssemblyField.setVisible(false);
        O_T_rpiField.setVisible(false);
        T_O_rpiField.setVisible(false);
        set_IO_connectionButton.setVisible(false);

        panel1.add(classIDLabel);
        panel1.add(classIDField);
        panel1.add(instanceIDLabel);
        panel1.add(instanceIDField);
        panel1.add(attributeIDLabel);
        panel1.add(attributeIDField);
        panel1.add(attributeValueLabel);
        panel1.add(attributeValueField);
        panel1.add(sendRequesstButton);
        panel1.add(attributeListLabel);
        panel1.add(attributeListField);

        IO_panel = new JPanel();
        IO_panel.setLayout(new BoxLayout(IO_panel, BoxLayout.Y_AXIS));
        IO_panel.setVisible(false);
        IO_panel.add(forwardCloseButton);

        IO_panel.add(IO_setupLabel);
        IO_panel.add(new JLabel("OUTPUT:"));
        IO_panel.add(outputAssemblyField);
        IO_panel.add(new JLabel("INPUT:"));
        IO_panel.add(inputAssemblyField);
        IO_panel.add(new JLabel("CONFIG:"));
        IO_panel.add(configAssemblyField);
        IO_panel.add(new JLabel("O->T RPI:"));
        IO_panel.add(O_T_rpiField);
        IO_panel.add(new JLabel("T->O RPI:"));
        IO_panel.add(T_O_rpiField);
        IO_panel.add(set_IO_connectionButton);
        panel1.add(IO_panel);
    }


    //Pristup k textovemu polu GUI
    public JTextArea getTextArea()
    {
        return textArea;
    }
    //Nastavenie vykonavania po stlaceni IDENTIFY tlacidla
    private Runnable IDENTIFYaction;
    public void setIDENTIFYaction(Runnable action)
    {
        this.IDENTIFYaction = action;
    }
    //Pristup k panelu GUI
    public JPanel getPanel1()
    {
        return panel1;
    }

    public void show_comm_options()
    {
        IDENTIFYButton.setVisible(false);
        a0Button.setVisible(false);
        a1Button.setVisible(false);
        a2Button.setVisible(false);
        a3Button.setVisible(false);
        a4Button.setVisible(false);
        a5Button.setVisible(false);
        a6Button.setVisible(false);
        a7Button.setVisible(false);
        a8Button.setVisible(false);
        a9Button.setVisible(false);
        CEButton.setVisible(false);
        buttonDOT.setVisible(false);

        explicitButton.setVisible(true);
        implicitButton.setVisible(true);
        quitButton.setVisible(true);
        textArea.append("\nSelect communication type: ");
    }

    //Skryje v GUI zadavanie atributu
    public void hide_attribute()
    {
        classIDLabel.setVisible(true);
        classIDField.setVisible(true);
        instanceIDLabel.setVisible(true);
        instanceIDField.setVisible(true);
        attributeIDLabel.setVisible(false);
        attributeIDField.setVisible(false);
        sendRequesstButton.setVisible(true);
        attributeValueLabel.setVisible(false);
        attributeValueField.setVisible(false);
    }

    public void show_attribute_write()
    {
        classIDLabel.setVisible(true);
        instanceIDLabel.setVisible(true);
        attributeIDLabel.setVisible(true);
        classIDField.setVisible(true);
        instanceIDField.setVisible(true);
        attributeIDField.setVisible(true);
        sendRequesstButton.setVisible(true);
        attributeValueLabel.setVisible(true);
        attributeValueField.setVisible(true);
    }

    public void show_attribute_read()
    {
        classIDLabel.setVisible(true);
        instanceIDLabel.setVisible(true);
        attributeIDLabel.setVisible(true);
        classIDField.setVisible(true);
        instanceIDField.setVisible(true);
        attributeIDField.setVisible(true);
        sendRequesstButton.setVisible(true);
        attributeValueLabel.setVisible(false);
        attributeValueField.setVisible(false);
    }

    public void show_IO_comm_options()
    {
        IO_panel.setVisible(true);
        outputAssemblyField.setVisible(true);
        inputAssemblyField.setVisible(true);
        configAssemblyField.setVisible(true);
        O_T_rpiField.setVisible(true);
        T_O_rpiField.setVisible(true);
        set_IO_connectionButton.setVisible(true);
        IO_setupLabel.setVisible(true);
    }


    //Gettery a settery
    public JComboBox<String> getServiceButton()
    {
        return serviceButton;
    }

    public JButton getExplicitButton()
    {
        return explicitButton;
    }

    public JButton getImplicitButton()
    {
        return implicitButton;
    }

    public JButton getQuitButton()
    {
        return quitButton;
    }

    public JButton getSendRequestButton()
    {
        return sendRequesstButton;
    }

    public JTextField getClassIDField()
    {
        return classIDField;
    }

    public JTextField getAttributeIDField()
    {
        return attributeIDField;
    }

    public JTextField getInstanceIDField()
    {
        return instanceIDField;
    }

    public  JTextField getAttributeListField() { return attributeListField; }



    public JLabel getAttributeListLabel() { return  attributeListLabel; }

    public JTextField getOutputAssemblyField() { return outputAssemblyField; }

    public JTextField getIntputAssemblyField() { return inputAssemblyField; }

    public JLabel getAttributeValueLabel() {return  attributeValueLabel;}

    public JTextField getAttributeValueField() {return  attributeValueField; }

    public JTextField getConfigAssemblyFieldA() { return configAssemblyField; }

    public JTextField getO_T_rpiField() { return O_T_rpiField; }

    public JTextField getT_O_rpiField() { return T_O_rpiField; }

    public JButton getSet_IO_connectionButton() { return set_IO_connectionButton; }

    public JButton getForwardCloseButton() { return forwardCloseButton; }
}
