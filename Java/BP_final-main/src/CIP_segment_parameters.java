import java.io.ByteArrayOutputStream;

public class CIP_segment_parameters
{
    public static byte[] CIP_segment_build(short service, short classID, short instanceID, Short attributeID)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(service & 0xFF);
        int pathWords = (attributeID != null) ? 3 :2;
        stream.write(pathWords);
        stream.write(0x20);
        stream.write(classID & 0xFF);
        stream.write(0x24);
        stream.write(instanceID & 0xFF);

        if (attributeID != null)
        {
            stream.write(0x30);
            stream.write(attributeID & 0xFF);
        }
        
        return  stream.toByteArray();
    }
}
