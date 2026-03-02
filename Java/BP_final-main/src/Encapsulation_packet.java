import javax.swing.*;
import java.io.*;
public class Encapsulation_packet
{   //Definicia poloziek hlavicky zapuzdreneho ENIP paketu
    private byte[] command;
    private byte[] data;
    private byte[] length;
    private byte[] session_handle;
    private final byte[] status;
    private final byte[] sender_context;
    private final byte[] options;

    //Nastavenie hodnot jednotlivych poloziek
    public Encapsulation_packet(int dataLength, byte[] data)
    {
        this.command = new byte[]{0x65, 0x00};
        this.data = new byte[]{0x00, 0x00, 0x00, 0x00};
        this.length = new byte[] {(byte) (data.length & 0xFF), (byte) ((data.length >>8) & 0xFF)};
        this.session_handle = new byte[4];
        this.status = new byte[4];
        this.sender_context = new byte[8];
        this.options = new byte[4];
        this.data = data_segment_construct(dataLength);
    }

    public Encapsulation_packet(byte[] data)
    {
        this.command = new byte[] {0x65, 0x00};
        this.session_handle = new byte[4];
        this.status = new byte[4];
        this.sender_context = new byte[8];
        this.options = new byte[4];
        setData(data);
    }


    //Zostavenie datoveho segmentu
    private byte[] data_segment_construct(int val)
    {
        return new byte[] {
                (byte) (val & 0xFF),
                (byte) ((val >> 8) & 0xFF),
                (byte) ((val >> 16) & 0xFF),
                (byte) ((val >> 24) & 0xFF)
        };
    }
    //Konverzia na pole bajtov - potrebne pri posielani paketu
    public byte[] byte_array_conv()
    {
        JTextArea textArea = new JTextArea();
        ByteArrayOutputStream packetStream = new ByteArrayOutputStream();
        try
        {
            packetStream.write(command);
            packetStream.write(length);
            packetStream.write(session_handle);
            packetStream.write(status);
            packetStream.write(sender_context);
            packetStream.write(options);
            if (data != null)
            {
                packetStream.write(data);
            }
        }catch (IOException e)
        {
            textArea.setText("Chyba: " + e.getMessage());
        }
        return packetStream.toByteArray();
    }
    //Nastavenie Session Handle
    public void setSession_handle(byte[] session_handle)
    {
        if (session_handle == null || session_handle.length != 4)
        {
            throw new IllegalArgumentException("Nespravna velkost Session Handle");
        }
        this.session_handle = session_handle;
    }
    //Nastavenie datovej casti
    public  void setData(byte[] data)
    {
        this.data = data;
        this.length = new byte[] {(byte) (data.length & 0xFF), (byte)((data.length >> 8) & 0xFF)};
    }

    public byte[] getLength()
    {
        return this.length;
    }


    public byte[] getData()
    {
        return data;
    }

    //Nastavenie prikazu
    public void setCommand(byte[] command)
    {
        if (command == null || command.length != 2)
        {
            throw new IllegalArgumentException("Command musi mat 2 bajty.");
        }
        this.command = command;
    }


}
