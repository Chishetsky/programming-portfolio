import java.util.List;

//Trieda na definovanie struktury objektu
public class CIP_object_format
{
    private final short classId;
    private final String name;
    private final List<CIP_attribute_format> attributes;

    public CIP_object_format(short classId, String name, List<CIP_attribute_format> attributes)
    {
        this.classId = classId;
        this.name = name;
        this.attributes = attributes;
    }

    public short getClassId() { return classId; }
    public String getName() { return name; }
    public List<CIP_attribute_format> getAttributes() { return  attributes; }


}
