/*IMPLEMENTAION OF ETHERNET/IP
* Semestral_Thesis
* Author: Tomas Cisecky, 230791
* VUT FEKT*/

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main
{
    public static InputStream inStr;
    public static OutputStream outStr;
    public static Socket soc;
    public static byte[] session_handle = new byte[4];
    public static ENIP_form form;
    public static String Gained_IPAddress = null;
    public static int connectionSerial;
    public static int vendorID;
    public static long originatorSerial;
    public static byte[] connectionPath;
    public static int O_T_connID;
    public static int T_O_connID;
    public static ScheduledExecutorService udpScheduler;


    public static void main(String[] args)
    {
        setup_GUI();
        setupListeners();
    }

//**************SEKCIA LOGIKY GUI A TLACIDIEL*************************************************************************

    //Spustenie a nastavenie GUI
    public static void setup_GUI()
    {
        JFrame frame = new JFrame("Ethernet/IP APP");
        form = new ENIP_form();
        frame.setContentPane(form.getPanel1());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    //Stlacenie tlacidla IDENTIFY
    public static void useIDENTIFY()
    {
        String IPAddress = IPAddress_reciever(form.getTextArea());
        if (IPAddress != null)
        {
            TCP_establish(44818, IPAddress, form.getTextArea());
            Register_Session_set(form.getTextArea());
            form.show_comm_options();
        }
        else
        {
            form.getTextArea().setText("Invalid IP Address. Please enter again\n");
        }
    }
    //Volba EXPLICIT
    public static void useEXPLICIT()
    {
        form.getExplicitButton().setVisible(false);
        form.getImplicitButton().setVisible(false);
        form.getServiceButton().setVisible(true);
    }
    //Volba IMPLICIT
    public static void useIMPLICIT()
    {
        form.getImplicitButton().setVisible(false);
        form.getExplicitButton().setVisible(false);
        form.show_IO_comm_options();
    }
    //Tlacidlo na Forward Open
    public static void useForwardOpen()
    {
        try
        {
            String outputStr = form.getOutputAssemblyField().getText().trim();
            String inputStr = form.getIntputAssemblyField().getText().trim();
            String configStr = form.getConfigAssemblyFieldA().getText().trim();
            String O_T_rpiStr = form.getO_T_rpiField().getText().trim();
            String T_O_rpiStr = form.getT_O_rpiField().getText().trim();

            byte output_value = (byte) IO_input_parse(outputStr);
            byte input_value = (byte) IO_input_parse(inputStr);
            byte config_value = configStr.isEmpty() ? 0x00 : (byte) IO_input_parse(configStr);
            int O_T_rpi = Integer.parseInt(O_T_rpiStr);
            int T_O_rpi = Integer.parseInt(T_O_rpiStr);

            ByteArrayOutputStream path = new ByteArrayOutputStream();
            if (!configStr.isEmpty())
            {
                path.write(new byte[]{0x20, 0x04, 0x24, config_value});
            }

            path.write(new byte[]{0x2C, output_value});
            path.write(new byte[] {0x2C, input_value});

            //predcasne otvorenie UDP streamu na zamedzenie straty prveho UDP paketu
            T_O_connID = 0xBE3F0004;
            UDP_establish(T_O_connID, form.getTextArea());
            Thread.sleep(100);

            Forward_Open_send(form.getTextArea(), O_T_rpi, 0x4086, T_O_rpi, 0x4082, path.toByteArray());
        }catch (Exception e)
        {
            form.getTextArea().append("\nERROR: Forward Open failed: " + e.getMessage());
        }
    }
    //Tlacidlo na Forward Close
    public static void useForwardClose()
    {
        try
        {
            String outputStr = form.getOutputAssemblyField().getText().trim();
            String configStr = form.getConfigAssemblyFieldA().getText().trim();

            byte outputValue = (byte) Integer.parseInt(outputStr.replace("0x", ""), 16);
            byte inputValue = (byte) Integer.parseInt(outputStr.replace("0x", ""), 16);
            byte configValue = configStr.isEmpty() ? 0x00 : (byte) Integer.parseInt(configStr.replace("0x", ""), 16);

            ByteArrayOutputStream path = new ByteArrayOutputStream();
            if (!configStr.isEmpty())
            {
                path.write(new byte[] {0x20, 0x04, 0x24, configValue});
            }

            path.write(new byte[] {0x2C, outputValue});
            path.write(new byte[] {0x2C, inputValue});

            Forward_Close_send(form.getTextArea());
            form.getForwardCloseButton().setVisible(false);

        } catch (Exception e)
        {
            form.getTextArea().append("\nERROR: Sending Forward_Close failed: "+ e.getMessage());
        }
    }
    //Tlacidlo QUIT
    public static void useQUIT()
    {
        try {
            Encapsulation_packet packet = new Encapsulation_packet(0, new byte[]{});

            Unregister_Session_set(packet, form.getTextArea());

            if (soc != null && !soc.isClosed())
            {
                soc.close();
                form.getTextArea().append("\nTCP connection terminated by user");
            }
        } catch (IOException e){
            form.getTextArea().append("\nError terminationg connection: " + e.getMessage());
        }
        System.exit(0);
    }
    //Vybrana sluzba
    public static void useServiceSelect()
    {
        String service = (String) form.getServiceButton().getSelectedItem();

        form.getAttributeListField().setVisible(false);
        form.getAttributeListLabel().setVisible(false);

        if ("Set_Attribute_Single".equals(service))
        {
            form.show_attribute_write();
            form.getAttributeValueLabel().setText("Value:");
        } else if ("Get_Attribute_Single".equals(service)) {
            form.show_attribute_read();
        } else if ("Get_Attribute_All".equals(service)){
            form.hide_attribute();
        } else if ("Get_Attribute_List".equals(service)) {
            form.hide_attribute();
            form.getAttributeListField().setVisible(true);
            form.getAttributeListLabel().setVisible(true);
        } else if ("Reset".equals(service)) {
            form.hide_attribute();
            form.getAttributeValueLabel().setText("Reset Type (0,1,2):");
            form.getAttributeValueLabel().setVisible(true);
            form.getAttributeValueField().setVisible(true);
        }
    }
    //Tlacidlo odoslania
    public static void useSendRequest()
    {
        String service = (String) form.getServiceButton().getSelectedItem();

        try {
            short classID = Short.parseShort(form.getClassIDField().getText());
            short instanceID = Short.parseShort(form.getInstanceIDField().getText());
            Short attributeID = null;
            List<Short> attributeList = null;

            if ("Get_Attribute_Single".equals(service) || "Set_Attribute_Single".equals(service))

            {
                attributeID = Short.parseShort(form.getAttributeIDField().getText());
            }

            if ("Get_Attribute_List".equals(service))
            {
                String raw = form.getAttributeListField().getText().trim();

                if (raw.isEmpty())
                {
                    form.getTextArea().append("\nERROR: Enter at least one attribute");
                    return;
                }

                String[] parts = raw.split(",");
                attributeList = new ArrayList<>();

                try
                {
                    for (String part : parts)
                    {
                        String cleaned = part.trim();
                        if (!cleaned.isEmpty())
                        {
                            attributeList.add(Short.parseShort(cleaned));
                        }
                    }

                } catch (NumberFormatException ex)
                {
                    form.getTextArea().append("\nERROR: Attribute list must be comma separated numbers");
                    return;
                }
            }

            assert service != null;
            SendRRData_set(service, form.getTextArea(), classID, instanceID, attributeID, attributeList);
        } catch (NumberFormatException e){
            form.getTextArea().append("\nERROR: Invalid input values Class/Instance/Attribute");
        } catch (Exception e) {
            form.getTextArea().append("\nERROR: "+ e.getMessage());
        }
    }

    //Riadiaca logika jednotlivych tlacidiel
    public static void setupListeners()
    {
        form.setIDENTIFYaction(Main::useIDENTIFY);
        form.getExplicitButton().addActionListener(_ -> useEXPLICIT());
        form.getImplicitButton().addActionListener(_ -> useIMPLICIT());
        form.getSet_IO_connectionButton().addActionListener(_ -> useForwardOpen());
        form.getForwardCloseButton().addActionListener(_ -> useForwardClose());
        form.getQuitButton().addActionListener(_ -> useQUIT());
        form.getServiceButton().addActionListener(_ -> useServiceSelect());
        form.getSendRequestButton().addActionListener(_ -> useSendRequest());
    }

//******************SEKCIA VYTVORENIA ENIP SPOJENIA***************************************************************

    //ziskanie IP adresy a osetrenie korektnosti
    public static String IPAddress_reciever(JTextArea textArea)
    {
        String[] lines = textArea.getText().split("\n");

        if (lines.length >0)
        {
            String IPAddress = lines[lines.length -1].trim();
            String[] octets = IPAddress.split("\\.");

            if (octets.length != 4)
            {
                return null;
            }

            for (int i = 0; i < octets.length; i++)
            {
                try
                {
                    int octet_num_val = Integer.parseInt(octets[i]);

                    if (octet_num_val <0 || octet_num_val > 255)
                    {
                        return null;
                    }

                    if (i ==0 && octet_num_val < 10)
                    {
                        return null;
                    }

                }catch (NumberFormatException e)
                {
                    return null;
                }
            }

            return IPAddress;
        }
        return null;
    }
    //nadviazanie TCP spojenia
    public static void TCP_establish(int port, String IPAddress, JTextArea textArea)
    {
        try
        {
            Socket soc = new Socket();
            soc.connect(new InetSocketAddress(IPAddress, port), 2000);
            soc.setSoTimeout(2000);

            inStr = soc.getInputStream();
            outStr = soc.getOutputStream();
            Gained_IPAddress = IPAddress;

            textArea.setText("\nConnected to: "+ IPAddress + ":"+ port);

        }
        catch (IOException e)
        {
            textArea.setText("\nTCP ERROR: "+ e.getMessage());
        }
    }

    //Vytvorenie ENIP relacie - vytvorenie a odoslanie zapuzdreneho paketu
    public static void Register_Session_set(JTextArea textArea)
    {
        try
        {
            Encapsulation_packet packet = new Encapsulation_packet(0, new byte[]{});
            packet.setCommand(new byte[]{0x65, 0x00});
            packet.setData(new byte[]{0x01, 0x00, 0x00, 0x00});
            byte[] bytesArray = packet.byte_array_conv();
            outStr.write(bytesArray);
            outStr.flush();
            textArea.append("\nRegister Session send");

            byte[] response = response_reading(inStr, textArea);
            if (response.length > 0)
            {
                System.arraycopy(response, 4, session_handle, 0, 4);
                packet.setSession_handle(session_handle);
            }
            else
            {
                textArea.setText("\nDevice not responding");
            }

        }catch (IOException e)
        {
             textArea.setText("\nERROR: " + e.getMessage());
        }
    }

    //spracovanie spravy SendRRData
    public static void SendRRData_set(String service, JTextArea textArea, short classID, short instanceID, Short attributeID, List<Short> attributeList)
    {

        try
        {
            //Vytvorenie ENIP paketu typu SendRRData
            Encapsulation_packet packet = new Encapsulation_packet(0, new byte[]{});
            packet.setCommand(new byte[]{0x6F, 0x00});
            packet.setSession_handle(Main.session_handle);

            //Zostavenie CIP segmentu
            byte [] cipSegment;
            switch (service)
            {
                case "Get_Attribute_Single":
                    cipSegment = Get_Attribute_Single_set(classID, instanceID, attributeID);
                    break;
                case "Get_Attribute_All":
                    cipSegment = Get_Attribute_All_set(classID, instanceID);
                    break;
                case "Set_Attribute_Single":
                    cipSegment = Set_Attribute_Single_set(classID, instanceID, attributeID);
                    if (cipSegment.length == 0)
                    {
                        form.getTextArea().append("\nRequest was not sent");
                        return;
                    }
                    break;
                case "Get_Attribute_List":
                    cipSegment = Get_Attribute_List_set(classID, instanceID, attributeList);
                    break;
                case "Reset":
                    byte resetType;
                    try
                    {
                        resetType = Byte.parseByte(form.getAttributeValueField().getText().trim());
                    }catch (NumberFormatException e)
                    {
                        textArea.append("\nERROR: Reset Type must be number from <0,2> interval");
                        return;
                    }
                    if (resetType <0 || resetType > 2)
                    {
                        textArea.append("\nERROR: Reset Type must be 0,1 or 2");
                        return;
                    }

                    cipSegment = Reset_set(classID, instanceID, resetType);
                    break;
                default:
                    textArea.append("\nUnknown or unsupported CIP service: " + service);
                    return;
            }

            //Vytvorenie CPF segmentu
            byte[] cpfSegment = CPF_build(cipSegment);
            packet.setData(cpfSegment);

            //Odoslanie Request paketu
            byte[] packetBytes = packet.byte_array_conv();
            outStr.write(packetBytes);
            outStr.flush();

            //Prijatie odpovede zo zariadenia
            byte[] response = response_reading(inStr, textArea);
            if (response.length == 0)
            {
                textArea.append("\nDevice not responding");
            }

            //Spracovanie odpovede podla typu sluzby
            switch (service)
            {
                case "Get_Attribute_Single":
                    String result = Get_Attribute_Single_extract(response, classID, attributeID);
                    textArea.append("\nResult: " + result);
                    break;
                case "Get_Attribute_All":
                    Get_Attribute_All_extract(response, textArea, classID);
                    break;
                case "Set_Attribute_Single":
                    Set_Attribute_Single_extract(response, textArea);
                    break;
                case "Get_Attribute_List":
                    Get_Attribute_List_extract(response, textArea,attributeList);
                    break;
                case "Reset":
                    int offset = CIP_Start_locate(response);
                    if (offset == -1)
                    {
                        textArea.append("\nCIP segment not found - device restart likely");
                        break;
                    }
                    int status = response[offset + 2 ] & 0xFF;
                    if (status == 0x00)
                    {
                        textArea.append("\nReset succesfull.");
                    }
                    else
                    {
                        textArea.append("\nReset failed: " + generalStatus_control(status));
                    }
                    break;
                default:
                    textArea.append("\nUnknown or unsupported answer");
            }
        }catch (IOException e)
        {
            textArea.setText("\nError while sending: "+ e.getMessage());
        }
    }

    //ukoncenie ENIP komunikacie
    public static void Unregister_Session_set(Encapsulation_packet packet, JTextArea textArea)
    {
        try
        {
            packet.setCommand(new byte[]{0x66, 0x00});
            packet.setData(new byte[0]);
            packet.setSession_handle(Main.session_handle);

            byte[] packetBytes = packet.byte_array_conv();
            outStr.write(packetBytes);
            outStr.flush();
            textArea.append("\nUnRegister Session send");

            byte[] unRegSessResponse = response_reading(inStr, textArea);
            if (unRegSessResponse.length > 0)
            {
                textArea.append("\nDevice response recieved.");
            }
            else
            {
                textArea.append("\nDevice is terminating connection");
            }
            outStr.close();
            inStr.close();
        }catch (IOException e)
        {
            textArea.setText("\nERROR: " + e.getMessage());
        }
    }

    //Vytvorenie UDP spojenia a jeho udrzba
    public static void UDP_establish(int connectionID, JTextArea textArea)
    {
        new Thread(() -> {
            try (
                    DatagramSocket socket = new DatagramSocket(2222)

            )
            {
                textArea.append("\nUDP active at 2222\n");

                String ip = Gained_IPAddress;
                if (ip == null)
                {
                    textArea.append("\nERROR: Wrong IP address");
                    return;
                }

                InetAddress target = InetAddress.getByName(ip);
                byte[] recieveBuffer = new byte[1024];
                int[] seqNumber = {0};

                Main.udpScheduler = Executors.newScheduledThreadPool(1);
                Main.udpScheduler.scheduleAtFixedRate(() -> {
                    try
                    {
                        ByteArrayOutputStream payload = new ByteArrayOutputStream();
                        payload.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(0x00000000).array());
                        payload.write(new byte[] { (byte) 0xAB, (byte) 0xCD});
                        byte[] ioPayload = payload.toByteArray();

                        byte[] udpSegment = connected_build(ioPayload, Main.O_T_connID, seqNumber[0]++);

                        DatagramPacket dp = new DatagramPacket(udpSegment, udpSegment.length, target, 2222);

                        socket.send(dp);
                        textArea.append("\nUDP data send");
                    } catch (Exception e)
                    {
                        textArea.append("\nError sending UDP: " + e.getMessage());
                    }

                },0, 500, TimeUnit.MILLISECONDS);

                while (true)
                {
                    DatagramPacket packet = new DatagramPacket(recieveBuffer, recieveBuffer.length);
                    socket.receive(packet);

                }

            }catch (IOException e)
            {
                textArea.append("\nError in UDP listener: " + e.getMessage());
            }

        }).start();
    }


    //**********************SEKCIA LOGIKY CIP VSEOBECNYCH SLUZIEB*************************************************************

    public static byte[] Get_Attribute_Single_set(short classID, short instanceID, Short attributeID)
    {
        if (attributeID == null)
        {
            throw new IllegalArgumentException("\nERROR: Missing attributeID");
        }

        return CIP_segment_parameters.CIP_segment_build(
                (short) 0x0E,
                classID,
                instanceID,
                attributeID);
    }

    //Spracovanie odpovede na Get_Attribute_Single
    public static String Get_Attribute_Single_extract(byte[] response, short classsID, short attributeID)
    {
        Main.form.getTextArea().setText("");
        int offset = CIP_Start_locate(response);
        if (offset == -1)
        {
            return "\nERROR: CIP section missing";
        }

        int serviceID = response[offset] & 0xFF;
        if (serviceID != 0x8E)
        {
            return "\nChyba: wrong answer on Get_Attribute_Single (expecting 0x8E)";
        }
        int generalStatus = response[offset + 2] & 0xFF;
        if (generalStatus != 0x00)
        {
            return generalStatus_control(generalStatus);
        }

        offset += 4;

        if (!CIP_object_library.hasObjectProfile(classsID))
        {
            return "\nUnknown object: (" + classsID +")";
        }

        CIP_object_format objectProfile = CIP_object_library.getObjectProfile(classsID);

        for (CIP_attribute_format attribute : objectProfile.getAttributes())
        {
            if (attribute.getId() == attributeID)
            {
                return attribute_interpret(response, offset, attribute);
            }
        }

        return "\nAtributte with ID " + attributeID + "not defined\n";

    }
    //Zostavenie CIP casti pre sluzbu Get_Attribute_All
    public static byte[] Get_Attribute_All_set(short classID, short instanceID)
    {
        return CIP_segment_parameters.CIP_segment_build((short) 0x01, classID,instanceID,null);
    }
    //Spracovanie odpovede sluzby Get_Atribute_All
    public static void Get_Attribute_All_extract(byte[] response, JTextArea textArea, short classID)
    {
        Main.form.getTextArea().setText("");
        int offset = CIP_Start_locate(response);
        if (offset == -1) {
            textArea.append("\nERROR: CIP segment missing");
            return;
        }

        int serviceID = response[offset] & 0xFF;
        if (serviceID != 0x81) {
            textArea.append(String.format("\nERROR: Invalid response to Get_Attributes_All -\n expected: 0x81, but recieved: 0x%02X", serviceID));
            return;
        }
        //Protichybova kontrola
        int generalStatus = response[offset + 2] & 0xFF;
        if (generalStatus != 0x00)
        {
            textArea.append("\n" + generalStatus_control(generalStatus));
            return;
        }
        //Preskocenie metadat CIP hlavicky
        offset += 4;


        if (!CIP_object_library.hasObjectProfile(classID))
        {
            textArea.append("\nERROR: Object ("+classID+") not supported by App");
            return;
        }
        //Nacitanie objektu z Registra
        CIP_object_format objectProfile = CIP_object_library.getObjectProfile(classID);
        textArea.append("\nAvailable attibutes of object " + objectProfile.getName() + ":\n");

        //Prechod atributami
        for (CIP_attribute_format attribute : objectProfile.getAttributes())
        {
            int length = attribute.getLength();
            int usedBytes;

            if (offset >= response.length)
            {
                break;
            }
            //Prevod odpovede na citatelny text
            String formattedResponse =  attribute_interpret(response, offset, attribute);

            if (formattedResponse.contains("data unavaliable") || formattedResponse.contains("invalid length"))
            {
                textArea.append("[!] " + formattedResponse + "\n");
            }
            else
            {
                textArea.append(formattedResponse + "\n");
            }
            //Typova kontrola a nastavenie dlzky
            switch (attribute.getType())
            {
                case "UINT" -> {
                    usedBytes = 2;
                    if ((offset + usedBytes) > response.length)
                    {
                        usedBytes = response.length - offset;
                    }
                }
                case "UDINT", "DWORD" -> {
                    usedBytes = 4;
                    if ((offset + usedBytes) > response.length)
                    {
                        usedBytes = response.length - offset;
                    }

                }
                case "USINT", "BYTE" -> {
                    usedBytes = 1;
                    if ((offset + usedBytes) > response.length)
                    {
                        usedBytes = response.length - offset;
                    }
                }
                case "REVISION" -> {
                    usedBytes = 2;
                    if ((offset + usedBytes) > response.length)
                    {
                        usedBytes = response.length - offset;
                    }
                }
                case "STRING" -> {
                    if ((offset +1) < response.length)
                    {
                        int strlen = response[offset] & 0xFF;
                        usedBytes = 1 + strlen;
                        if ((offset + usedBytes) > response.length)
                        {
                            usedBytes = response.length - offset;
                        }
                        if ((usedBytes % 2) != 0)
                        {
                            usedBytes++;
                        }
                    }
                    else
                        {
                            usedBytes = response.length - offset;
                        }
                }
                case "SHORT_STRING" -> {
                    if ((offset + 2 ) <= response.length) {
                        int strlen = (response[offset] & 0xFF) | ((response[offset + 1] & 0xFF) << 8);
                        usedBytes = 2 + strlen;
                        if ((offset + usedBytes) > response.length)
                        {
                            usedBytes = response.length - offset;
                        }
                        if ((usedBytes % 2) != 0)
                        {
                            usedBytes++;
                        }
                    }
                        else
                        {
                            usedBytes = response.length - offset;
                        }
                }
                case "MCAST_CONFIG" ->
                    usedBytes = 8;
                case "INTERFACE_CONFIGURATION" -> {
                    if (offset +21 >= response.length)
                    {
                        usedBytes = response.length - offset;
                        textArea.append("INTERFACE_CONFIGURATION: nedostupne data (nedostatok bajtov)\n");
                    }
                    else
                    {
                        int domainLength = (response[offset + 20] & 0xFF) | ((response[offset + 21] & 0xFF) << 8);
                        usedBytes = 22 + domainLength;
                        if (usedBytes % 2 != 0)
                        {
                            usedBytes++;
                        }
                    }
                }
                case "INTERFACE_COUNTERS" ->
                    usedBytes = 44;

                case "PHYSICAL_LINK_PATH" -> {
                    if ((offset + 2) <= response.length)
                    {
                        int pathSize = (response[offset] & 0xFF) | ((response[offset + 1] & 0xFF) << 8);
                        usedBytes = 2 + pathSize * 2;
                    }
                    else
                    {
                        usedBytes = response.length - offset;
                    }
                }
                case "ARRAY_UINT" ->
                    usedBytes = response.length - offset;
                case "ARRAY_USINT", "BYTE_ARRAY", "ARRAY_BOOL" ->
                    usedBytes = (length > 0 && (offset + length) <= response.length)
                                ? length : response.length - offset;
                case "STRUCT" ->
                    usedBytes = attribute.getLength();
                default ->
                    usedBytes = attribute.getLength();
            }
            offset += usedBytes;
        }
    }
    //Zostavenie Set_Attribute_single Request
    public static byte[] Set_Attribute_Single_set(short classID, short instanceID, Short attributeID)
    {
        if (attributeID == null)
        {
            throw new IllegalArgumentException("\nERROR: Attribute ID not entered");
        }

        byte[] request = CIP_segment_parameters.CIP_segment_build((short) 0x10, classID, instanceID, attributeID);

        if (classID == (short) 0xF5 && attributeID == (short) 0x05)
        {
            Main.form.getTextArea().append("\nWriting to Interface Configuration (attribute 5) is forbidden for security reasons");
            return new byte[0];
        }

        if (classID == (short) 0xF6 && attributeID == (short) 0x05)
        {
            Main.form.getTextArea().append("\nWriting to Media Counters (attribute 5) is forbidden.");
            return new byte[0];
        }

        byte[] valueBytes;

        try {
            valueBytes = input_parse(classID, attributeID);
        } catch (Exception e)
        {
           Main.form.getTextArea().append("\nError parsing input: " + e.getMessage());
            return new byte[0];
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(request);
            stream.write(valueBytes);
        }catch (IOException e)
        {
            Main.form.getTextArea().append("\nERROR: Set Attribute Request build failed: " + e.getMessage() + "\n");
            return new byte[0];
        }
        return stream.toByteArray();
    }

    //Spracovanie Set_Attribute_Single Response
    public static void Set_Attribute_Single_extract(byte[] response, JTextArea textArea)
    {
        textArea.setText("");
        int offset = CIP_Start_locate(response);
        if (offset  == -1)
        {
            textArea.append("\nERROR: CIP section missing");
            return;
        }

        int serviceID = response[offset] & 0xFF;

        if ((serviceID & 0x7F) != 0x10)
        {
            textArea.append("\nWrong answer to Set_Attribute_Single");
            return;
        }
        int generalStatus = response[offset + 2] & 0xFF;
        if (generalStatus == 0x00)
        {
            textArea.append("\nWriting successfull");
        }
        else
        {
            textArea.append(generalStatus_control(generalStatus));
        }

    }

    //Zostavenie Get_Attribute_List Request paketu
    public static byte[] Get_Attribute_List_set(short classID,short instanceID, List<Short> attributeIDs)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        stream.write(0x03);
        stream.write(0x02);
        stream.write(0x20); stream.write(classID & 0xFF);
        stream.write(0x24); stream.write(instanceID & 0xFF);
        stream.write(attributeIDs.size() & 0xFF);
        stream.write((attributeIDs.size() >>  8) & 0xFF);

        for (short id : attributeIDs)
        {
            stream.write(id & 0xFF);
            stream.write((id >> 8) & 0xFF);
        }

        return stream.toByteArray();
    }

    public static void Get_Attribute_List_extract(byte[] response, JTextArea textArea, List<Short> requestedIds)
    {
        textArea.setText("");
        try
        {
            int offset = CIP_Start_locate(response);
            if (offset == -1)
            {
                textArea.append("\nERROR: CIP section missing");
                return;
            }

            offset += 2;
            int generalStatus = response[offset] & 0xFF;
            offset++;
            int additionalStatusSize = response[offset] & 0xFF;
            offset++;

            offset += 2 * additionalStatusSize;

            if (generalStatus != 0x00)
            {
                textArea.append("\n" + generalStatus_control(generalStatus));
                return;
            }

            int attributeCount = ((response[offset + 1] & 0xFF) << 8) | (response[offset] & 0xFF);
            offset += 2;

            short classID = Short.parseShort(Main.form.getClassIDField().getText());

            if (!CIP_object_library.hasObjectProfile(classID))
            {
                textArea.append("\nUnknown object: ("+ classID+")");
                return;
            }

            CIP_object_format profile = CIP_object_library.getObjectProfile(classID);
            List<CIP_attribute_format> allAttributes = profile.getAttributes();

            for (int i = 0; i < attributeCount; i++)
            {
                int attrID = ((response[offset + 1] & 0xFF) << 8) | (response[offset] & 0xFF);
                offset += 2;

                int status = ((response[offset + 1] & 0xFF) << 8) | (response[offset] & 0xFF);
                offset += 2;

                if (status != 0x0000)
                {
                    textArea.append(String.format("\nAttribute ID %d:  Error - status 0x%04X", attrID, status));
                    continue;
                }

                CIP_attribute_format targetAttr = null;
                for (CIP_attribute_format attr : allAttributes)
                {
                    if (attr.getId() == attrID)
                    {
                        targetAttr = attr;
                        break;
                    }
                }

                if (targetAttr == null)
                {
                    textArea.append("\n Attribute wtih ID "+ attrID + "not defined in object_library");
                    continue;
                }

                String result = attribute_interpret(response, offset, targetAttr);
                textArea.append("\n" + result);

                int length  = targetAttr.getLength();
                int used = length;

                if (targetAttr.isDynamicLength())
                {
                    switch (targetAttr.getType())
                    {
                        case "STRING":
                            used = 1 + ((offset + 1 < response.length) ? response[offset] & 0xFF : 0);
                            break;
                        case "SHORT_STRING":
                            used = 2 +((offset + 1 < response.length) ? ((response[offset] & 0xFF) | ((response[offset + 1] & 0xFF) << 8)) : 0);
                            break;
                        default:
                            used = attr_length_guess(response, offset);
                    }
                }

                offset += used;
            }

        } catch (Exception e)
        {
            textArea.append("\nERROR: Get_Attributes_list failed: " + e.getMessage());
        }
    }
    //Zostavenie Reset poziadavky
    public static byte[] Reset_set(short classID, short instanceID, byte resetType)
    {
        byte[] rstSegment = CIP_segment_parameters.CIP_segment_build((short) 0x05, classID, instanceID, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try
        {
            out.write(rstSegment);
            out.write(resetType);
        }catch (IOException e)
        {
            Main.form.getTextArea().append("\nReset request Error: " + e.getMessage());
        }

        return out.toByteArray();
    }

    //******************SEKCIA PARSEROV A KONVERTOROV***********************************************************************

    //Odhad dlzky neznameho atributu
    public static int attr_length_guess(byte[] response, int offset)
    {
        if (offset >= response.length)
        {
            return 0;
        }

        int length = response[offset] & 0xFF;

        if (length > 0 && length < 64 && offset + length < response.length)
        {
            return 1 + length;
        }
        else if (offset + 2 <= response.length)
        {
            return 2;
        }
        else
        {
            return 1;
        }
    }

    public static int IO_input_parse(String input)
    {
        input = input.trim().toLowerCase();
        if (input.startsWith("0x"))
        {
            return Integer.parseInt(input.substring(2),16);
        }
        else
        {
            return Integer.parseInt(input);
        }
    }

    public static byte[] cipSegment_extract(byte[] response)
    {
        if (response.length < 30)
        {
            throw new IllegalArgumentException("\nERROR: Too short reply on Forward_Open ");
        }

        int offset = 24 + 4 + 2;
        int itemCount = ByteBuffer.wrap(response, offset, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xFFFF;
        offset += 2;

        for (int i = 0; i < itemCount; i++)
        {
            if (offset + 4 > response.length)
            {
                throw new IllegalArgumentException("\nERROR: Incomplete CPF segment");
            }

            int typeID = ByteBuffer.wrap(response, offset, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xFFFF;
            int length = ByteBuffer.wrap(response, offset + 2, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xFFFF;
            offset += 4;

            if (offset + length > response.length)
            {
                throw new IllegalArgumentException("ERROR: CPF maximum length exceeded \n");
            }

            if (typeID == 0x00B2 || typeID == 0x00B1)
            {
                byte[] cip = Arrays.copyOfRange(response, offset, offset + length);

                return cip;
            }
            offset += length;
        }

        throw new IllegalArgumentException("ERROR: CIP section missing\n");

    }

    //Konverzia bajtov odpovede na zrozumitelnu podobu
    public static String attribute_interpret(byte[] data, int offset, CIP_attribute_format attribute)
    {
        String name = attribute.getName();
        String type = attribute.getType();

        try
        {
            int length = attribute.getLength();
            int remainingBytes = data.length - offset;

            switch (type)
            {
                case "UINT":
                    if (remainingBytes >= 2)
                    {
                        int value = (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8);
                        return name + ": " + value;
                    }
                    break;

                case "UDINT":
                case "DWORD":
                    if (remainingBytes >= 4)
                    {
                        long value = ((data[offset] & 0xFFL))
                                | ((data[offset +1] &0xFFL) << 8)
                                | ((data[offset +2] &0xFFL) << 16)
                                | ((data[offset +3] &0xFFL) << 24);
                        return name + ": " + value;
                    }
                    break;

                case "USINT":
                case "BYTE":
                    if (remainingBytes >= 1)
                    {
                        int b = data[offset] & 0xFF;
                        return name + ": " + b;
                    }
                    break;

                case "REVISION":
                    if (remainingBytes >= 2)
                    {
                        int major = data[offset] & 0xFF;
                        int minor = data[offset + 1] & 0xFF;
                        return name + ": " + major + "." + minor;
                    }
                    break;

                case "STRING":
                    if (offset +1 < data.length)
                    {
                        int lengthStr = data[offset] & 0xFF;
                        if ((offset + 1 + lengthStr) > data.length)
                        {
                            return name + ": (invalid length of string)";
                        }
                            byte[] stringBytes = Arrays.copyOfRange(data, offset +1, offset + 1 + lengthStr);
                            return name + ": " + new String(stringBytes, StandardCharsets.US_ASCII);
                    }
                    else
                    {
                        return name + ": (data unavaliable)";
                    }




                case "INTERFACE_COUNTERS":
                    if (remainingBytes >= 44)
                    {
                        String[] labels = {
                                "In Octets", "In Ucast Packets", "In NUcast Packets", "In Discards", "In Errors",
                                "In Unknown Protos", "Out Octets", "Out Ucast Packets", "Out NUcast Packets", "Out Discards", "Out Errors"
                        };
                        StringBuilder counters = new StringBuilder(name + ":\n");
                        int position = offset;
                        for (String label : labels)
                        {
                            long value = ((data[position] & 0xFFL))
                                    | ((data[position + 1] & 0xFFL) << 8)
                                    | ((data[position + 2] & 0xFFL) << 16)
                                    | ((data[position + 3] & 0xFFL) << 24);
                            counters.append(" ").append(label).append(": ").append(value).append("\n");
                            position += 4;
                        }
                        return counters.toString();
                    }
                    break;

                case "MCAST_CONFIG":
                    if (remainingBytes >= 8)
                    {
                        int allocControl = data[offset] & 0xFF;
                        int reserved = data[offset + 1] & 0xFF;
                        int numMcast = (data[offset + 2] & 0xFF) | ((data[offset + 3] & 0xFF) << 8);
                        long mcastStart = ((data[offset + 4] & 0xFFL)) |
                                ((data[offset + 5] & 0xFFL) << 8) |
                                ((data[offset + 6] & 0xFFL) << 16) |
                                ((data[offset + 7] & 0xFFL) << 24);

                        String mcastIP = String.format("%d.%d.%d.%d", (mcastStart >> 24) & 0xFF,
                            (mcastStart >> 16) & 0xFF,
                            (mcastStart >> 8) & 0xFF,
                            (mcastStart) & 0xFF
                        );

                        return String.format("%s:\n Alloc Control: %d\n Reserved: %d\n Num Mcast: %d\n Mcast Start Addr: %s",
                                name, allocControl, reserved, numMcast, mcastIP);

                    }
                    break;

                case "INTERFACE_CONFIGURATION":
                    if (remainingBytes >= 24)
                    {
                        StringBuilder sb = new StringBuilder(name + ":\n");
                        int position = offset;

                        String[] labels = {"Ip Address", "Subnet Mask", "Gateway", "Name Server", "Name Server 2"};

                        for (int i = 0; i < 5; i++)
                        {
                            if (position + 3 >= data.length)
                            {
                                break;
                            }

                            int b4 = data[position] & 0xFF;
                            int b3 = data[position + 1] & 0xFF;
                            int b2 = data[position + 2] & 0xFF;
                            int b1 = data[position + 3] & 0xFF;
                            sb.append(" ").append(labels[i]).append(": ").append(b1).append(".").append(b2).append(".").append(b3).append(".").append(b4).append("\n");
                            position += 4;
                        }

                        if (position + 1 >= data.length)
                        {
                            break;
                        }
                        int domainLength = (data[position] & 0xFF) | ((data[position +1] & 0xFF) << 8);
                        position += 2;

                        StringBuilder doamain = new StringBuilder();
                        for (int i = 0; i < domainLength && (position + i) < data.length; i++)
                        {
                            doamain.append((char) data[position + i]);
                        }

                        sb.append("Domain Name: ").append(doamain).append("\n");

                        return sb.toString();
                    }
                    break;

                case "PHYSICAL_LINK_PATH":
                    if (remainingBytes >= 2)
                    {
                        int pathSize = ((data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8));
                        int position = offset +2;
                        StringBuilder sb = new StringBuilder(name + ":\n");
                        sb.append(" Path Size: ").append(pathSize).append(" words\n");
                        int byteSize = pathSize * 2;
                        int end = Math.min(position + byteSize, data.length);

                        for (int i = position; i < end; i++)
                        {
                            sb.append(String.format("0x%02X", data[i])).append("    ");
                        }
                        return sb.toString();
                    }
                    break;

                case "ARRAY_UINT":
                    if (remainingBytes >= 2)
                    {
                        int count = length > 0 ? length /2 : remainingBytes / 2;
                        StringBuilder field = new StringBuilder(name + ": ");
                        for (int i = 0; i < count && offset + i * 2 + 1 < data.length; i++)
                        {
                            int value = (data[offset + i * 2] & 0xFF)
                                    | ((data[offset + i * 2 + 1] & 0xFF) << 8);
                            field.append(value).append("    ");
                        }
                        return field.toString();
                    }
                    break;

                case "ARRAY_USINT":
                case "BYTE_ARRAY":
                    if (remainingBytes >= 1)
                    {
                        int count = length > 0 ? length : remainingBytes;
                        StringBuilder bytesField = new StringBuilder(name + ": ");
                        for (int i = 0; i < count && offset + i < data.length; i++)
                        {
                            bytesField.append(String.format("0x%02X", data[offset + i]));
                        }
                        return bytesField.toString();
                    }
                    break;

                case "SHORT_STRING":
                    if (offset + 2 <= data.length)
                    {
                        int strLength = (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8);
                        if ((offset + 2 + strLength) > data.length)
                        {
                            return name + ": (invalid length of string)";
                        }
                        byte[] strBytes = Arrays.copyOfRange(data, offset + 2, offset + 2 + strLength);
                        return name + ": " + new String(strBytes, StandardCharsets.US_ASCII);
                    }
                    break;

                case "ARRAY_BOOL":
                    if (remainingBytes >= 1)
                    {
                        int count = length > 0 ? length : remainingBytes;
                        StringBuilder bools = new StringBuilder(name + ": ");
                        for (int i = 0; i < count && offset + i < data.length; i++)
                        {
                            for (int bit = 0; bit < 8; bit++)
                            {
                                bools.append((data[offset + i] >> bit) & 1).append(" ");
                            }
                        }
                        return bools.toString();
                    }
                    break;

                case "STRUCT":
                    if (attribute.getName().equals("Mcast Config") && remainingBytes >= 8)
                    {
                        int allocCtrl = data[offset] & 0xFF;
                        int reserved = data[offset + 1] & 0xFF;
                        int numMcast = (data[offset + 2] & 0xFF) | ((data[offset + 3] & 0xFF) << 8);
                        long mcastStart = ((data[offset + 4] & 0xFFL)) |
                                ((data[offset + 5] & 0xFFL) << 8) |
                                ((data[offset + 6] & 0xFFL) << 16) |
                                ((data[offset + 7] & 0xFFL) << 24);
                        String mcastIP = String.format("%d.%d.%d.%d",
                                (mcastStart >> 24) & 0xFF,
                                (mcastStart >> 16) & 0xFF,
                                (mcastStart >> 8) & 0xFF,
                                (mcastStart) & 0xFF
                        );
                        return String.format("%s:\n ALLoc Control: %d\n Reserved: %d\n Num Mcast: %d\n Mcast Start Addr: %s",
                                name, allocCtrl, reserved, numMcast, mcastIP);
                    }

                    if (attribute.getName().equals("Media Counters") && remainingBytes >= 44)
                    {
                        String[] labels = {
                                "Alignment Errors", "FCS Errors", "Single Collisions", "Multiple Collisions", "SQE Test Errors", "Deferred Transmissions",
                                "Late Colissions", "Excessive Collisions", "MAC Transmit Errors", "Carrier Sense Errors", "MAC Receive Errors"};
                        StringBuilder sb = new StringBuilder(name + ":\n");
                        int position = offset;
                        for (String label: labels)
                        {
                            long value = ((data[position] & 0xFFl)) |
                                    ((data[position + 1] & 0xFFL) << 8) |
                                    ((data[position + 2] & 0xFFL) << 16) |
                                    ((data[position + 3] & 0xFFL) << 24);
                            sb.append(" ").append(label).append(": ").append(value).append("\n");
                            position += 4;
                        }
                        return sb.toString();
                    }

                    if (attribute.getName().equals("Interface Control") && remainingBytes >= 12)
                    {
                        int ctrlBits = (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8);
                        int forcedSpeed = (data[offset + 2] & 0xFF) | ((data[offset + 3] & 0xFF) << 8);
                        int ifaceFlags = (data[offset + 4] & 0xFF) | ((data[offset + 5] & 0xFF) << 8);
                        StringBuilder mac = new StringBuilder();
                        for (int i = 0; i < 6; i++)
                        {
                            mac.append(String.format("0x%02X", data[offset + 6 + i]));
                            if (i < 5)
                            {
                                mac.append("    ");
                            }
                        }

                        return String.format("%s:\n Control Bits: %d\n Forced Speed: %d Mbps\n Interface Flags: %d\n Physical Address: %s",
                                name, ctrlBits, forcedSpeed, ifaceFlags, mac);

                    }
                    return name + ": (unknown attribute type)";
            }

        } catch (Exception e) {
            return name + "ERROR: inerpretation failed: " + e.getMessage();
        }
        return name + ": (unknown/invalid type or insufficient data)";
    }

    public static byte[] input_parse(short classID, short attributeID) throws Exception
    {
        CIP_object_format objectProfile = CIP_object_library.getObjectProfile(classID);
        if (objectProfile == null)
        {
            throw new Exception("\nObject (" +classID+") doest not exist");
        }

        CIP_attribute_format attribute = null;
        for (CIP_attribute_format a : objectProfile.getAttributes())
        {
            if (a.getId() == attributeID)
            {
                attribute = a;
                break;
            }
        }

        if (attribute == null)
        {
            throw new Exception("ERROR: Attribute ("+ attributeID+") undefined\n");
        }

        String type = attribute.getType();
        if (type == null)
        {
            throw new Exception("Undefined attribute type");
        }

        String input = Main.form.attributeValueField.getText().trim();

        return inputBytes_convert(input, type);
    }

    //Konverzia vstupnej hodnoty na pole bajtov - potrebne pre spracovanie
    public static byte[] inputBytes_convert(String input, String cipType) throws Exception
    {
        switch (cipType)
        {
            case "UINT": {
                int value = Integer.parseInt(input);
                return new byte[]{(byte) (value & 0xFF), (byte) ((value >> 8) & 0xFF)};
            }
            case "USINT":
            case "BYTE":
            case "BOOL": {
                int value = (input.equalsIgnoreCase("true") || input.equals("1")) ? 1: 0;
                return new byte[] { (byte) value };
            }
            case "UDINT":
            case "DWORD": {
                long value = Long.parseLong(input);
                return new byte[] {
                        (byte) (value & 0xFF),
                        (byte) ((value >> 8) & 0xFF),
                        (byte) ((value >> 16) & 0xFF),
                        (byte) ((value >> 24) & 0xFF)
                };
            }
            case "STRING": {
                byte[] strBytes = input.getBytes("UTF-8");
                int length = strBytes.length;
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(length & 0xFF);
                out.write((length >> 8) & 0xFF);
                out.write(strBytes);
                return out.toByteArray();
            }
            case "SHORT_STRING": {
                byte[] strBytes = input.getBytes("UTF-8");
                if (strBytes.length > 32) throw new Exception("\nERROR: SHORT_STRING maximum is 32 chars");
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(strBytes.length & 0xFF);
                out.write((strBytes.length >> 8) & 0xFF);
                out.write(strBytes);
                return out.toByteArray();
            }
            case "REVISION": {
                String[] parts = input.split("\\.");
                if (parts.length != 2)
                {
                    throw new Exception("\nERROR: Invalid format - expected (Major.Minor)");
                }
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1]);
                return new byte[] {(byte) major, (byte) minor};
            }
            case "ARRAY_UINT": {
                String[] tokens = input.trim().split("\\s+");
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                for (String token : tokens) {
                    int value = Integer.parseInt(token);
                    out.write(value & 0xFF);
                    out.write((value >> 8) & 0xFF);
                }
                return out.toByteArray();
            }
            case "ARRAY_USINT":
            case "BYTE_ARRAY":
            case "ARRAY_BOOL": {
                String[] tokens = input.trim().split("\\s+");
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                for (String token : tokens)
                {
                    if (token.startsWith("0x") || token.startsWith("0X"))
                    {
                        out.write(Integer.parseInt(token.substring(2), 16));
                    }
                    else
                    {
                        out.write(Integer.parseInt(token));
                    }
                }
                return out.toByteArray();
            }
            case "PHYSICAL_LINK_PATH": {
                String[] tokens = input.trim().split("\\s+");
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int words = tokens.length / 2;
                out.write(words & 0xFF);
                out.write((words >> 8) & 0xFF);
                for (String token : tokens)
                {
                    if (token.startsWith("0x") || token.startsWith("0X"))
                    {
                        out.write(Integer.parseInt(token.substring(2), 16));
                    }
                    else
                    {
                        out.write(Integer.parseInt(token));
                    }
                }
                return out.toByteArray();
            }
            case "INTERFACE_CONTROL": {
                String[] tokens = input.trim().split("\\s+");
                if (tokens.length != 2)
                {
                    throw new Exception("\nExpected: <controlBits> <forcedSpeed>");
                }

                int controlBits = Integer.parseInt(tokens[0]);
                int forcedSpeed = Integer.parseInt(tokens[1]);

                if (controlBits < 0 || controlBits > 2)
                {
                    throw new Exception("\nAllowed values: 0,1,2");
                }
                if (controlBits == 1 && forcedSpeed != 0)
                {
                    throw new Exception("\nWhen auto-negotiation active, forced speed must be 0");
                }
                if ((controlBits == 0 || controlBits == 2) && forcedSpeed <= 0 )
                {
                    throw new Exception("\nForced speed must be set");
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream();

                out.write(controlBits & 0xFF);
                out.write((controlBits >> 8) & 0xFF);
                out.write(forcedSpeed & 0xFF);
                out.write((forcedSpeed >> 8) & 0xFF);

                return out.toByteArray();
            }
            case "MCAST_CONFIG": {
                String[] tokens = input.trim().split("\\s+");
                if (tokens.length != 4)
                {
                    throw new Exception("\nAllowed format: <allocCtrl> <reserved> <numMcast IP> ");
                }
                int allocCtrl = Integer.parseInt(tokens[0]);
                int reserved = Integer.parseInt(tokens[1]);
                int numMcast = Integer.parseInt(tokens[2]);
                String[] IPtokens = tokens[3].split("\\.");

                if (allocCtrl < 0 || allocCtrl > 1)
                {
                    throw new Exception("\nallocCtrl can be 0 a 1");
                }
                if (reserved != 0)
                {
                    throw new Exception("\nReserved must be 0");
                }
                if (IPtokens.length != 4)
                {
                    throw new Exception("\nInvalid format of IP address: (must be X.X.X.X)");
                }
                if (allocCtrl == 0)
                {
                    if (numMcast != 0)
                    {
                        throw new Exception("\nInvalid combination allocCtrl and numMcast");
                    }
                    for (String s : IPtokens)
                    {
                        if (!s.equals("0"))
                        {
                            throw new Exception("\nInvalid combination allocCtrl a IP");
                        }
                    }
                }
                if (allocCtrl == 1)
                {
                    int firstOctet = Integer.parseInt(IPtokens[0]);
                    if (firstOctet < 224 || firstOctet > 239)
                    {
                        throw new Exception("\nInvalid multicast adress range");
                    }
                    if (numMcast<= 0 || numMcast > 65535)
                    {
                        throw new Exception("\nAddress limit exceeded");
                    }
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(allocCtrl & 0xFF);
                out.write(reserved & 0xFF);
                out.write(numMcast & 0xFF);
                out.write((numMcast >> 8) & 0xFF);

                for (int i = 3; i >= 0; i--)
                {
                    int byteValue = Integer.parseInt(IPtokens[i]);
                    if (byteValue < 0 || byteValue > 255)
                    {
                        throw new Exception("Wrong part of IP address: " + IPtokens[i]);
                    }
                    out.write(byteValue);
                }
                return out.toByteArray();

            }
            default:
                throw new Exception("ERROR: Unsupported type: "+ cipType);
        }
    }

    //Citanie odpovede zo zariadenia
    public static byte[] response_reading(InputStream inStr, JTextArea textArea)
    {
        ByteArrayOutputStream response_str = new ByteArrayOutputStream();
        byte[] temp = new byte[1024];
        int bytesCount;
        try
        {
            while ((bytesCount = inStr.read(temp)) != -1)
            {
                response_str.write(temp, 0, bytesCount);
                if (inStr.available() ==0)
                {
                    break;
                }
            }

        }catch (IOException e)
        {
            textArea.append("\nResponse reading error: " + e.getMessage());
            return new byte[0];
        }
        return response_str.toByteArray();
    }


    public static int CIP_Start_locate(byte[] response)
    {
        for (int i = 24; i < response.length -2; i++)
        {
            if ((response[i] & 0xFF) == 0xB2 && (response[i + 1] & 0xFF) == 0x00)
            {
                return i + 4;
            }
        }
        return  -1;
    }

//**************************SEKCIA I/O KOMUNIKACIE*******************************************************
    public static byte[] Forward_Open_build(long oToTConnID,
                                            long tToOConnID,
                                            int connSerialNum,
                                            int vendorID,
                                            long originSerNum,
                                            int oToT_RPI,
                                            int oToT_ConnParams,
                                            int tToO_RPI,
                                            int tToO_ConnParams,
                                            byte transportTrig,
                                            byte[] connPath)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            stream.write(0x54);

            stream.write(2);

            stream.write(0x20); stream.write(0x06);

            stream.write(0x24); stream.write(0x01);

            stream.write(0x03);
            stream.write(0xFA);

            stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((int) oToTConnID).array());
            stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((int) tToOConnID).array());
            stream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) connSerialNum).array());
            stream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) vendorID).array());
            stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((int) originSerNum).array());

            stream.write(0x03);

            stream.write(new byte[]{0x00, 0x00, 0x00});

            stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(oToT_RPI).array());
            stream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) oToT_ConnParams).array());
            stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(tToO_RPI).array());
            stream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) tToO_ConnParams).array());

            stream.write(transportTrig);
            stream.write(connPath.length / 2);
            stream.write(connPath);

        }catch (IOException e){
            System.err.println("ERROR: " + e.getMessage());
        }

        return stream.toByteArray();
    }

    public static void Forward_Open_send(JTextArea textArea, int O_T_rpi, int O_T_params, int T_O_rpi, int T_O_params, byte[] connectionPath)
    {
        try
        {
            byte[] cipPayload = Forward_Open_build(
                    0x12345678,
                    T_O_connID,
                    0x1234,
                    0x04E3,
                    0x44B58017,
                    O_T_rpi, O_T_params,
                    T_O_rpi, T_O_params,
                    (byte) 0x01, connectionPath
            );

            connectionSerial = 0x1234;
            vendorID = 0x04E3;
            originatorSerial = 0x44B58017;
            Main.connectionPath = connectionPath;


            byte[] cpf = unconnected_build(cipPayload);

            Encapsulation_packet packet = new Encapsulation_packet(cpf);
            packet.setCommand(new byte[]{0x6F, 0x00});
            packet.setSession_handle(Main.session_handle);

            byte[] bytes = packet.byte_array_conv();
            outStr.write(bytes);
            outStr.flush();

            byte[] response = response_reading(inStr, textArea);
            form.getTextArea().append("\nresponse length: " + response.length);
          //  form.getTextArea().append("\nHEX content: " + Arrays.toString(response));
            if (response.length == 0)
            {
                textArea.append("\nERROR: Device not responding on Forward_Open");
                return;
            }

            int[] connIDs = Forward_Open_Response_parse(response, textArea);
            if (connIDs != null)
            {
                O_T_connID = connIDs[0];
                T_O_connID = connIDs[1];
                form.getForwardCloseButton().setVisible(true);
            }
        } catch (IOException e){
            textArea.append("\nError sending Forward_Open: " + e.getMessage());
        }
    }

    public static void Forward_Close_send(JTextArea textArea)
    {
        try
        {
            byte[] cipPayload = Forward_Close_build(textArea, Main.connectionSerial, Main.vendorID, Main.originatorSerial, Main.connectionPath);

            byte[] cpf = CPF_build(cipPayload);
            Encapsulation_packet packet = new Encapsulation_packet(cpf);
            packet.setCommand(new byte[]{0x6F, 0x00});
            packet.setSession_handle(Main.session_handle);

            byte[] bytes = packet.byte_array_conv();
            outStr.write(bytes);
            outStr.flush();

            byte[] response = response_reading(inStr, textArea);
            if (response.length == 0)
            {
                textArea.append("\nERROR: Device not responding on Forward_Close");
                return;
            }

            int offset = CIP_Start_locate(response);
            if (offset == -1)
            {
                textArea.append("\nERROR: CIP section missing");
            }

            byte serviceCode = response[offset];
            byte status = response[offset + 2];

            if (status == 0x00) {
                textArea.append("\nForward_Close send");


                if (Main.udpScheduler != null && !Main.udpScheduler.isShutdown()) {
                    Main.udpScheduler.shutdownNow();
                    textArea.append("\nUDP stream ended");
                }
            }
            else
            {
                textArea.append("\nERROR: Forward_Open send failed: " + generalStatus_control(status));
            }
        }
        catch (IOException e)
        {
            textArea.append("\nERROR: Forward_Clsoe send failed: " + e.getMessage());
        }
    }

   public static byte[] Forward_Close_build(JTextArea textArea, int connectionSerialNum, int vendorID, long originatorSerialNum, byte[] connectionPath)
   {
       ByteArrayOutputStream stream = new ByteArrayOutputStream();
       try
       {
           stream.write(0x4E);
           stream.write(0x02);
           stream.write(0x20); stream.write(0x06);
           stream.write(0x24); stream.write(0x01);

           stream.write(0x0F);
           stream.write(0x10);

           stream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) connectionSerialNum).array());
           stream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) vendorID).array());
           stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((int) originatorSerialNum).array());

           int pathSize = (connectionPath.length + 1) / 2;
           stream.write(pathSize);
           stream.write(0x00);
           stream.write(connectionPath);
       }
       catch (IOException e)
       {
           textArea.append("ERROR: Forward_Close build failed: " + e.getMessage());
       }
       return stream.toByteArray();
   }

    //Spracovanie odpovede na Forward_Open
    public static int[] Forward_Open_Response_parse(byte[] response, JTextArea textArea)
    {
        try {
            if (response == null || response.length < 30)
            {
                return null;
            }

            byte[] cip;

            cip = cipSegment_extract(response);

            if (cip.length < 16)
            {
                return null;
            }

            byte service = cip[0];

            if ((service & 0xFF) == 0xD2)
            {
                int pathSize = cip[1] & 0xFF;
                int pathLength = 2 + (pathSize * 2);

                if (cip.length <= pathLength)
                {
                    return null;
                }

                cip = Arrays.copyOfRange(cip, pathLength, cip.length);
                service = cip[0];
            }

            if ((cip[0] & 0xFF) != 0xD4)
            {
                return null;
            }

            byte generalStatus = cip[2];
            int addStatusCount = cip[3];

            if (generalStatus != 0x00)
            {
                int offset = 4;
                if (offset + 2 <= cip.length)
                {
                    offset += 2;
                }
                return null;

            }

            if (cip.length < 16)
            {
                return null;
            }

            int oToT_ID = ByteBuffer.wrap(cip, 4,4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            int tTo0_ID = ByteBuffer.wrap(cip, 8, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            return  new int[] {oToT_ID, tTo0_ID};

        } catch (Exception e){
            textArea.append("\nError processing Forward_Open: " + e.getMessage());
            return null;
        }
    }

//************************SEKCIA BUILDEROV****************************************************************************
   public static byte[] unconnected_build(byte[] payLoad)
   {
       ByteArrayOutputStream stream = new ByteArrayOutputStream();

       try
       {
           stream.write(new byte[]{0x00, 0x00, 0x00, 0x00});

           stream.write(new byte[]{0x00, 0x00});

           stream.write(new byte[]{0x02, 0x00});

           stream.write(new byte[]{0x00, 0x00});
           stream.write(new byte[]{0x00, 0x00});

           stream.write(new byte[]{(byte) 0xB2, 0x00});
           stream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
                   .putShort((short) payLoad.length).array());

           stream.write(payLoad);
       } catch (IOException e){
           System.err.println("ERROR: " + e.getMessage());
       }

       return  stream.toByteArray();

   }

    public static byte[] CPF_build(byte[] cipSegment)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            //Interface Handle
            stream.write(new byte[]{0x00, 0x00, 0x00, 0x00});
            //Timeout
            stream.write(new byte[]{0x02, 0x00});
            //Item Count
            stream.write(new byte[]{0x02, 0x00});
            //Item 1: Null Adress
            stream.write(new byte[]{0x00, 0x00, 0x00, 0x00});
            //Item 2: Unconnected Data
            stream.write(new byte[]{(byte) 0xB2, 0x00});
            //Dlzka CIP casti
            stream.write(new byte[]{
                    (byte) (cipSegment.length & 0xFF),
                    (byte) ((cipSegment.length >> 8) & 0xFF)
            });

            stream.write(cipSegment);
        } catch (IOException e) {
            throw new RuntimeException("CPF segment build Error: " + e.getMessage());
        }

        return stream.toByteArray();
    }

    public static byte[] connected_build(byte[] cipPayload, int connectionID, int sequenceNumber)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try
        {
            stream.write(new byte[]{0x02, 0x00});
            stream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) 0x8002).array());
            stream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) 8).array());
            stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(connectionID).array());
            stream.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(sequenceNumber).array());


            stream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) 0x00B1).array());
            stream.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) cipPayload.length).array());
            stream.write(cipPayload);

        }catch (IOException e)
        {
            Main.form.getTextArea().append("CPF segment build failed: "+ e.getMessage());
        }
        return  stream.toByteArray();
    }

    //Kontrola chybovych kodov
    public static String generalStatus_control(int status)
    {
        switch (status)
        {
            case 0x00: return "\nOK.";
            case 0x01: return "\nERROR: Connection failure";
            case 0x02: return "\nERROR: Resource unavailable";
            case 0x03: return "\nERROR: Invalid parameter value";
            case 0x04: return "\nERROR: Path segment error";
            case 0x05: return "\nERROR: Path destination unknown";
            case 0x06: return "\nERROR: Partial transfer";
            case 0x07: return "\nERROR: Connection lost";
            case 0x08: return "\nERROR: Service not supported";
            case 0x09: return "\nERROR: Invalid attribute value";
            case 0x0A: return "\nERROR: Attribute list error";
            case 0x0B: return "\nERROR: Already in requested mode/state";
            case 0x0C: return "\nERROR: Object state conflict";
            case 0x0D: return "\nERROR: Object already exists";
            case 0x0E: return "\nERROR: Attribute not settable";
            case 0x0F: return "\nERROR: Privilege violation";
            case 0x10: return "\nERROR: Device state conflict";
            case 0x11: return "\nERROR: Reply data too large";
            case 0x12: return "\nERROR: Fragmentation of a primitive value";
            case 0x13: return "\nERROR: Not enough data";
            case 0x14: return "\nERROR: Attribute not supported";
            case 0x15: return "\nERROR: Too much data";
            case 0x16: return "\nERROR: Object does not exist";
            case 0x17: return "\nERROR: Service fragmentation sequence not in progress";
            case 0x18: return "\nERROR: Not stored attribute data";
            case 0x19: return "\nERROR: Store operation failure";
            case 0x1A: return "\nERROR: Routing failure, request packet too large";
            case 0x1B: return "\nERROR: Routing failure, response packet too large";
            case 0x1C: return "\nERROR: Missing attribute list entry data";
            case 0x1D: return "\nERROR: Invalid attribute value list";
            case 0x1E: return "\nERROR: Embeded service error";
            case 0x1F: return "\nERROR: Vendor specific error";
            case 0x20: return "\nERROR: Invalid parameter";
            case 0x21: return "\nERROR: Write-once value or medium already written";
            case 0x22: return "\nERROR: Invalid reply recieved";
            case 0x23: return "\nERROR: Buffer Overflow";
            case 0x24: return "\nERROR: Message Format Error";
            case 0x25: return "\nERROR: Key failure in path";
            case 0x26: return "\nERROR: Path Size Invalid";
            case 0x27: return "\nERROR: Unexpected attribute in list";
            case 0x28: return "\nERROR: Invalid Member ID";
            case 0x29: return "\nERROR: Member not settable";
            default: return "\nUnknown state: 0x" + Integer.toHexString(status).toUpperCase();
        }
    }
}