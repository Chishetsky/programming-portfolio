import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Trieda zoznamu objektov podporovanych aplikaciou
public class CIP_object_library
{   //Definicia registra vsetkych objektov
    private static final Map<Short, CIP_object_format> objectRegister = new HashMap<>();

    static
    {   //Identity Object(0x01)
        objectRegister.put((short) 0x01, new CIP_object_format((short) 0x01, "Identity Object",
                List.of(
                        new CIP_attribute_format((short) 0x01, "Vendor ID", 2,false, "UINT", false),
                        new CIP_attribute_format((short) 0x02, "Device Type", 2, false, "UINT", false),
                        new CIP_attribute_format((short) 0x03, "Product Code", 2, false, "UINT", false),
                        new CIP_attribute_format((short) 0x04, "Revision", 2, false, "REVISION", false),
                        new CIP_attribute_format((short) 0x05, "Status", 2, false, "UINT", false),
                        new CIP_attribute_format((short) 0x06, "Serial Number", 4, false, "UDINT", false),
                        new CIP_attribute_format((short) 0x07, "Product Name", -1, true, "STRING", false),
                        //tieto atributy su volitelne - "optional==true"
                        new CIP_attribute_format((short) 0x08, "State", 1, false, "BYTE", true),
                        new CIP_attribute_format((short) 0x09, "Configuration Consistency Value", 2, false, "UINT", true),
                        new CIP_attribute_format((short) 0x0A, "Heartbeat Interval", 2, false, "UINT", true),
                        new CIP_attribute_format((short) 0x0B, "Active Language", 2, true, "STRUCT", true),
                        new CIP_attribute_format((short) 0x0C, "Supported Language List", -1, true, "ARRAY_UINT", true),
                        new CIP_attribute_format((short) 0x0D, "International Product Name", -1, true, "STRING", true),
                        new CIP_attribute_format((short) 0x0E, "Semaphore", -1, true, "STRUCT", true),
                        new CIP_attribute_format((short) 0x0F, "Assigned Name", -1, true, "STRING", true),
                        new CIP_attribute_format((short) 0x10, "Assigned Description", -1, true, "STRING", true),
                        new CIP_attribute_format((short) 0x11, "Geographic Location", -1, true, "STRING", true)
                )));
        //Message Router Object (0x02)
        objectRegister.put((short) 0x02, new CIP_object_format((short) 0x02, "Message Router Object",
                List.of(
                        new CIP_attribute_format((short) 0x01, "Object List", -1, true, "STRUCT", true),
                        new CIP_attribute_format((short) 0x02, "Number Avaliable", 2, false, "UINT", true),
                        new CIP_attribute_format((short) 0x03, "Number Active", 2, false, "UINT", true),
                        new CIP_attribute_format((short) 0x04, "Active Connections", -1, true, "ARRAY_UINT", true)
                )));
        //Connection Manager Object(0x06)
        objectRegister.put((short) 0x06, new CIP_object_format((short) 0x06, "Connection Manager Object",
                List.of(
                        new CIP_attribute_format((short) 0x01, "Open Requests", 2, false, "UINT", true),
                        new CIP_attribute_format((short) 0x02, "Open Format Rejects", 2, false, "UINT", true),
                        new CIP_attribute_format((short) 0x03, "Open Resource Rejects", 2, false, "UINT", true),
                        new CIP_attribute_format((short) 0x04, "Open Others Rejects", 2, false, "UINT", true),
                        new CIP_attribute_format((short) 0x05, "Close Requests", 2, false, "UINT", true),
                        new CIP_attribute_format((short) 0x06, "Close Format Requests", 2, false, "UINT", true),
                        new CIP_attribute_format((short) 0x07, "Close Others Requests", 2, false, "UINT", true),
                        new CIP_attribute_format((short) 0x08, "Connection Timeouts", 2, false, "UINT", true),
                        new CIP_attribute_format((short) 0x09, "Connection Entry List", -1, true, "STRUCT", true),
                        new CIP_attribute_format((short) 0x0A, "ConnOpenBits", -1, true, "ARRAY_BOOL", true),
                        new CIP_attribute_format((short) 0x0B, "CPU_Utilization", 2, false, "UINT", true),
                        new CIP_attribute_format((short) 0x0C, "MaxBuffSize", 4, false, "UDINT", true),
                        new CIP_attribute_format((short) 0x0D, "BuffSizeRemaining", 4, false, "UDINT", true)
                )));
        //TCP/IP Interface Object (0xF5)
        objectRegister.put((short) 0xF5, new CIP_object_format((short) 0xF5, "TCP/IP Interface Object",
                List.of(
                        new CIP_attribute_format((short) 0x01, "Status", 4, false, "DWORD", false),
                        new CIP_attribute_format((short) 0x02, "Configuration Capability", 4, false, "DWORD", false),
                        new CIP_attribute_format((short) 0x03, "Configuration Control", 4, false, "DWORD", false),
                        new CIP_attribute_format((short) 0x04, "Physical Link Object", -1, true, "PHYSICAL_LINK_PATH", false),
                        new CIP_attribute_format((short) 0x05, "Interface Configuration", -1, true, "INTERFACE_CONFIGURATION", true),
                        new CIP_attribute_format((short) 0x06, "Host Name", -1, true, "SHORT_STRING", false),
                        new CIP_attribute_format((short) 0x07, "Safety Network Number", 6, false, "BYTE_ARRAY", true),
                        new CIP_attribute_format((short) 0x08, "TTL Value", 1, false, "USINT", true),
                        new CIP_attribute_format((short) 0x09, "Mcast Config", 8, false, "MCAST_CONFIG", true)
                )));
        //Ethernet Link Object (0xF6)
        objectRegister.put((short) 0xF6, new CIP_object_format((short) 0xF6, "Erhernet Link Object",
                List.of(
                        new CIP_attribute_format((short) 0x01, "Interface Speed", 4, false, "UDINT", false),
                        new CIP_attribute_format((short) 0x02, "Interface Flags", 4, false, "DWORD", false),
                        new CIP_attribute_format((short) 0x03, "Physical Adress", 6, true, "ARRAY_USINT", false),
                        new CIP_attribute_format((short) 0x04, "Interface Counters", 44, false, "INTERFACE_COUNTERS", true),
                        new CIP_attribute_format((short) 0x05, "Media Counters", -1, true, "STRUCT", true),
                        new CIP_attribute_format((short) 0x06, "Interface Control", -1, true, "INTERFACE_CONTROL", true),
                        new CIP_attribute_format((short) 0x07, "Interface Type", 1, false, "USINT", true),
                        new CIP_attribute_format((short) 0x08, "Interface State", 1, false, "USINT", true),
                        new CIP_attribute_format((short) 0x09, "Admin State", 1, false, "USINT", true),
                        new CIP_attribute_format((short) 0x0A, "Interface Label", -1, true, "STRING", true)
                )));

    }

    public static CIP_object_format getObjectProfile(short classId)
    {
        return objectRegister.get(classId);
    }

    public static boolean hasObjectProfile(short classId)
    {
        return objectRegister.containsKey(classId);
    }
}
