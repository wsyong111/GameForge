package io.github.wsyong11.gameforge.util.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.io.PrintStream;

public class PrintStreamWrapper extends PrintStream {
	private volatile PrintStream impl;

	public PrintStreamWrapper() {
		super(OutputStream.nullOutputStream());
	}

	public PrintStreamWrapper(@Nullable PrintStream impl) {
		this();
		this.setImpl(impl);
	}

	public void setImpl(@Nullable PrintStream impl) {
		this.impl = impl;
	}

	@Nullable
	public PrintStream getImpl() {
		return this.impl;
	}

	@Override
	public void flush() {
		PrintStream p = this.impl;
		if (p != null) p.flush();
	}

	@Override
	public void close() {
		PrintStream p = this.impl;
		if (p != null) p.close();
	}

	@Override
	public boolean checkError() {
		PrintStream p = this.impl;
		return p != null && p.checkError();
	}

	@Override
	public void write(int b) {
		PrintStream p = this.impl;
		if (p != null) p.write(b);
	}

	@Override
	public void write(byte @NotNull [] buf, int off, int len) {
		PrintStream p = this.impl;
		if (p != null) p.write(buf, off, len);
	}
}
