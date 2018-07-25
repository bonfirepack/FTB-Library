package com.feed_the_beast.ftblib.lib.config;

import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.util.List;
import java.util.function.IntSupplier;

/**
 * @author LatvianModder
 */
public class ConfigInt extends ConfigValue implements IntSupplier
{
	public static final String ID = "int";
	public static final Color4I COLOR = Color4I.rgb(0xAA5AE8);

	private int value;
	private int minValue = Integer.MIN_VALUE;
	private int maxValue = Integer.MAX_VALUE;

	public ConfigInt()
	{
	}

	public ConfigInt(int v)
	{
		value = v;
	}

	public ConfigInt(int v, int min, int max)
	{
		this(MathHelper.clamp(v, min, max));
		minValue = min;
		maxValue = max;
	}

	@Override
	public String getName()
	{
		return ID;
	}

	public ConfigInt setMin(int v)
	{
		minValue = v;
		return this;
	}

	public ConfigInt setMax(int v)
	{
		maxValue = v;
		return this;
	}

	public int getMin()
	{
		return minValue;
	}

	public int getMax()
	{
		return maxValue;
	}

	public void setInt(int v)
	{
		value = MathHelper.clamp(v, getMin(), getMax());
	}

	@Override
	public String getString()
	{
		return Integer.toString(getInt());
	}

	@Override
	public boolean getBoolean()
	{
		return getInt() != 0;
	}

	@Override
	public int getInt()
	{
		return value;
	}

	@Override
	public ConfigInt copy()
	{
		return new ConfigInt(getInt(), getMin(), getMax());
	}

	@Override
	public boolean equalsValue(ConfigValue value)
	{
		return getInt() == value.getInt();
	}

	@Override
	public Color4I getColor()
	{
		return COLOR;
	}

	@Override
	public void addInfo(ConfigValueInstance inst, List<String> list)
	{
		super.addInfo(inst, list);

		int m = getMin();

		if (m != Integer.MIN_VALUE)
		{
			list.add(TextFormatting.AQUA + "Min: " + TextFormatting.RESET + m);
		}

		m = getMax();

		if (m != Integer.MAX_VALUE)
		{
			list.add(TextFormatting.AQUA + "Max: " + TextFormatting.RESET + m);
		}
	}

	@Override
	public boolean setValueFromString(String string, boolean simulate)
	{
		try
		{
			int val = Integer.parseInt(string);

			if (!simulate)
			{
				setInt(val);
			}

			return true;
		}
		catch (Exception ex)
		{
			return false;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, String key)
	{
		value = getInt();

		if (value != 0)
		{
			nbt.setDouble(key, value);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, String key)
	{
		setInt(nbt.getInteger(key));
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeInt(getInt());
		data.writeInt(getMin());
		data.writeInt(getMax());
	}

	@Override
	public void readData(DataIn data)
	{
		setInt(data.readInt());
		setMin(data.readInt());
		setMax(data.readInt());
	}

	@Override
	public int getAsInt()
	{
		return getInt();
	}
}