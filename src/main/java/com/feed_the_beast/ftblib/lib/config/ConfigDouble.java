package com.feed_the_beast.ftblib.lib.config;

import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.util.List;
import java.util.function.DoubleSupplier;

/**
 * @author LatvianModder
 */
public class ConfigDouble extends ConfigValue implements DoubleSupplier
{
	public static final String ID = "double";

	private double value;
	private double min = Double.NEGATIVE_INFINITY;
	private double max = Double.POSITIVE_INFINITY;

	public ConfigDouble()
	{
	}

	public ConfigDouble(double v)
	{
		value = v;
	}

	public ConfigDouble(double v, double mn, double mx)
	{
		this(MathHelper.clamp(v, mn, mx));
		min = mn;
		max = mx;
	}

	@Override
	public String getName()
	{
		return ID;
	}

	@Override
	public double getDouble()
	{
		return value;
	}

	public void setDouble(double v)
	{
		value = v;
	}

	public ConfigDouble setMin(double v)
	{
		min = v;
		return this;
	}

	public ConfigDouble setMax(double v)
	{
		max = v;
		return this;
	}

	public double getMin()
	{
		return min;
	}

	public double getMax()
	{
		return max;
	}

	@Override
	public String getString()
	{
		return Double.toString(getDouble());
	}

	@Override
	public boolean getBoolean()
	{
		return getDouble() != 0D;
	}

	@Override
	public int getInt()
	{
		return (int) getDouble();
	}

	@Override
	public ConfigDouble copy()
	{
		return new ConfigDouble(getDouble());
	}

	@Override
	public boolean equalsValue(ConfigValue value)
	{
		return getDouble() == value.getDouble();
	}

	@Override
	public Color4I getColor()
	{
		return ConfigInt.COLOR;
	}

	@Override
	public void addInfo(ConfigValueInstance inst, List<String> list)
	{
		super.addInfo(inst, list);

		double m = getMin();

		if (m != Double.NEGATIVE_INFINITY)
		{
			list.add(TextFormatting.AQUA + "Min: " + StringUtils.formatDouble(m));
		}

		m = getMax();

		if (m != Double.POSITIVE_INFINITY)
		{
			list.add(TextFormatting.AQUA + "Max: " + StringUtils.formatDouble(m));
		}
	}

	@Override
	public boolean setValueFromString(String string, boolean simulate)
	{
		try
		{
			double val = Double.parseDouble(string);

			if (!simulate)
			{
				setDouble(val);
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
		value = getDouble();

		if (value != 0D)
		{
			nbt.setDouble(key, value);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, String key)
	{
		setDouble(nbt.getDouble(key));
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeDouble(getDouble());
		data.writeDouble(getMin());
		data.writeDouble(getMax());
	}

	@Override
	public void readData(DataIn data)
	{
		setDouble(data.readDouble());
		setMin(data.readDouble());
		setMax(data.readDouble());
	}

	@Override
	public double getAsDouble()
	{
		return getDouble();
	}

	@Override
	public void setValueFromOtherValue(ConfigValue value)
	{
		setDouble(value.getDouble());
	}
}