package ftb.lib.api.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import ftb.lib.api.IClickable;
import ftb.lib.api.MouseButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.LinkedHashMap;

public class ConfigEntryEnum<E extends Enum<E>> extends ConfigEntry implements IClickable // EnumTypeAdapterFactory
{
	private final LinkedHashMap<String, E> enumMap;
	private E value;
	public final E defValue;
	
	public ConfigEntryEnum(String id, E[] val, E def, boolean addNull)
	{
		super(id);
		
		enumMap = new LinkedHashMap<>();
		
		for(E e : val)
		{
			enumMap.put(getName(e), e);
		}
		
		if(addNull)
		{
			enumMap.put("-", null);
		}
		
		set(def);
		defValue = def;
	}
	
	@Override
	public ConfigEntryType getConfigType()
	{ return ConfigEntryType.ENUM; }
	
	@Override
	public int getColor()
	{ return 0x0094FF; }
	
	public void set(Object o)
	{ value = (E) o; }
	
	public E get()
	{ return value; }
	
	public static String getName(Enum<?> e)
	{ return e == null ? "-" : e.name().toLowerCase(); }
	
	private E fromString(String s)
	{ return enumMap.get(s.toLowerCase()); }
	
	@Override
	public final void func_152753_a(JsonElement o)
	{ set(fromString(o.getAsString())); }
	
	@Override
	public final JsonElement getSerializableElement()
	{ return new JsonPrimitive(getName(get())); }
	
	@Override
	public void writeToNBT(NBTTagCompound tag, boolean extended)
	{
		super.writeToNBT(tag, extended);
		tag.setString("V", getName(get()));
		
		if(extended)
		{
			tag.setString("D", getName(defValue));
			
			NBTTagList list = new NBTTagList();
			
			for(String s : enumMap.keySet())
				list.appendTag(new NBTTagString(s));
			
			tag.setTag("VL", list);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag, boolean extended)
	{
		super.readFromNBT(tag, extended);
		set(fromString(tag.getString("V")));
	}
	
	@Override
	public void onClicked(MouseButton button)
	{
		if(button.isLeft())
		{
			set(getFromIndex((getIndex() + 1) % enumMap.size()));
		}
		else
		{
			set(getFromIndex((getIndex() - 1) & (enumMap.size() - 1)));
		}
	}
	
	@Override
	public String getAsString()
	{ return getName(get()); }
	
	@Override
	public boolean getAsBoolean()
	{ return get() != null; }
	
	private E getFromIndex(int index)
	{
		if(index < 0 || index >= enumMap.size())
		{
			throw new ArrayIndexOutOfBoundsException(index);
		}
		
		int idx0 = 0;
		for(E e : enumMap.values())
		{
			if(index == idx0) return e;
			idx0++;
		}
		
		return null;
	}
	
	private int getIndex()
	{
		int idx0 = 0;
		E e0 = get();
		for(E e : enumMap.values())
		{
			if(e == e0) return idx0;
			idx0++;
		}
		
		return -1;
	}
	
	private int getDefaultIndex()
	{
		int idx0 = 0;
		for(E e : enumMap.values())
		{
			if(e == defValue) return idx0;
			idx0++;
		}
		
		return -1;
	}
	
	@Override
	public int getAsInt()
	{ return enumMap.size(); }
	
	@Override
	public String getDefValueString()
	{ return getName(defValue); }
}