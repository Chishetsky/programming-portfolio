//Trieda na definovanie atributov a ich struktury
public class CIP_attribute_format
{
    private final short id;
    private final String name;
    private final int lenght;
    private final boolean dynamicLength;
    private final String type;
    private final boolean optional;

    public CIP_attribute_format(short id, String name, int lenght, boolean dynamicLength, String type, boolean optional)
    {
        this.id = id;
        this.name = name;
        this.lenght = lenght;
        this.dynamicLength = dynamicLength;
        this.type = type;
        this.optional = optional;
    }

    public short getId() { return id; }
    public String getName() { return name; }
    public int getLength() { return lenght; }
    public boolean isDynamicLength() { return dynamicLength; }
    public String getType() { return type; }
    //public boolean isOptional() { return optional; }


}
