package io.github.wsyong11.gameforge.framework.dataflow.element.base;

import org.jetbrains.annotations.NotNull;

public interface BaseNumberElement<THIS extends BaseNumberElement<THIS>> extends BaseElement, Comparable<THIS>{
	@NotNull
	Number getNumber();

	int getAsInt();

	long getAsLong();

	float getAsFloat();

	double getAsDouble();

	byte getAsByte();

	short getAsShort();
}
