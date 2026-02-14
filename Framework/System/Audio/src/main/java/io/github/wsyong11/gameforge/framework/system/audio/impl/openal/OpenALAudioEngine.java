package io.github.wsyong11.gameforge.framework.system.audio.impl.openal;

import io.github.wsyong11.gameforge.framework.system.audio.AudioDevice;
import io.github.wsyong11.gameforge.framework.system.audio.AudioDeviceIdentity;
import io.github.wsyong11.gameforge.framework.system.audio.AudioListener;
import io.github.wsyong11.gameforge.framework.system.audio.AudioPlayer;
import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioManager;
import io.github.wsyong11.gameforge.framework.system.audio.engine.AudioEngineContext;
import io.github.wsyong11.gameforge.framework.system.audio.ex.AudioDeviceException;
import io.github.wsyong11.gameforge.framework.system.audio.ex.AudioDeviceOpenException;
import io.github.wsyong11.gameforge.framework.system.audio.impl.openal.context.OpenALContext;
import io.github.wsyong11.gameforge.framework.system.audio.impl.openal.device.NoopAudioDevice;
import io.github.wsyong11.gameforge.framework.system.audio.impl.openal.device.OpenALAudioDevice;
import io.github.wsyong11.gameforge.framework.system.audio.impl.openal.device.OpenALAudioDeviceImpl;
import io.github.wsyong11.gameforge.framework.system.audio.impl.simple.AbstractAudioEngine;
import io.github.wsyong11.gameforge.framework.system.audio.impl.simple.NamedAudioDeviceIdentity;
import io.github.wsyong11.gameforge.framework.system.audio.impl.simple.audio.DefaultAudioManager;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.joml.Vector3fc;

import java.nio.FloatBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;
import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.openal.ALC11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenALAudioEngine extends AbstractAudioEngine {
	private static final Logger LOGGER = Log.getLogger();

	private final DefaultAudioManager audioManager;

	private AudioDeviceIdentity defaultDeviceIdentity;
	private List<AudioDeviceIdentity> devicesIdentityList;

	private OpenALAudioDevice activeDevice;
	private DeviceStatus activeDeviceStatus;
	@Nullable
	private OpenALContext activeContext;
	private AudioDeviceIdentity activeDeviceIdentity;

	private final Map<AudioDeviceIdentity, OpenALAudioDevice> devices;

	private final OpenALListener listener;

	private boolean inited;
	private boolean closed;

	public OpenALAudioEngine(@NotNull AudioEngineContext context) {
		super(context);

		this.audioManager = new DefaultAudioManager();

		this.defaultDeviceIdentity = NamedAudioDeviceIdentity.EMPTY;

		this.devicesIdentityList = List.of();

		this.activeDevice = new NoopAudioDevice(NamedAudioDeviceIdentity.EMPTY);
		this.activeDeviceStatus = DeviceStatus.OPENED;
		this.activeContext = null;
		this.activeDeviceIdentity = NamedAudioDeviceIdentity.EMPTY;

		this.devices = new ConcurrentHashMap<>();

		this.listener = new OpenALListener();

		this.inited = false;
		this.closed = false;
	}

	@NotNull
	@Override
	public String getName() {
		return "OpenAL";
	}

	@Override
	public void init() {
		if (this.inited)
			return;
		this.inited = true;

		this.scanDevices();
	}

	protected void checkAvailable() {
		if (!this.inited)
			throw new IllegalStateException("Audio engine not init");

		if (this.closed)
			throw new IllegalStateException("Audio engine closed");
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public int getLoopPreSec() {
		return 20;  // TODO: 2026/2/1
	}

	@Override
	public void loopTick() {
		super.loopTick();

		this.updateDevice();

		if (this.activeDeviceStatus != DeviceStatus.OPENED)
			return;

		this.updateListener();
	}

	@NotNull
	@Override
	public AudioManager getAudioManager() {
		return this.audioManager;
	}

	protected void updateDevice() {
		if (this.activeDeviceIdentity.equals(this.activeDevice.getIdentity()) && this.activeDeviceStatus != DeviceStatus.PENDING)
			return;

		this.activeDeviceStatus = DeviceStatus.PENDING;

		alcMakeContextCurrent(NULL);

		if (this.activeContext != null)
			this.activeContext.close();
		this.activeContext = null;

		OpenALAudioDevice device;
		OpenALContext context;

		try {
			device = this.openDevice(this.activeDeviceIdentity);
			context = device.createContext();
			this.activeDeviceStatus = DeviceStatus.OPENED;
		} catch (AudioDeviceException e) {
			LOGGER.warn("Failed to open the device {}", this.activeDeviceIdentity, e);
			device = new NoopAudioDevice(this.activeDeviceIdentity);
			context = device.createContext();
			this.activeDeviceStatus = DeviceStatus.ERROR;
		}

		this.activeDevice = device;
		this.activeContext = context;

		context.use();

		LOGGER.trace("Changed the active device to {} ({}), active context {}",
			lazy(device),
			this.activeDeviceIdentity,
			lazy(context));
	}

	protected void updateListener() {
		if (!this.listener.isChanged())
			return;

		Vector3fc position = this.listener.getPositionRaw();
		alListener3f(AL_POSITION, position.x(), position.y(), position.z());

		Vector3fc velocity = this.listener.getVelocityRaw();
		alListener3f(AL_VELOCITY, velocity.x(), velocity.y(), velocity.z());

		FloatBuffer orientationBuffer = this.listener.getOrientationBuffer();
		alListenerfv(AL_ORIENTATION, orientationBuffer);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	protected OpenALAudioDevice openDevice(@NotNull AudioDeviceIdentity identity) throws AudioDeviceException {
		Objects.requireNonNull(identity, "identity is null");

		this.checkAvailable();

		if (identity.isEmpty())
			return new NoopAudioDevice(identity);

		synchronized (this.devices) {
			OpenALAudioDevice cachedDevice = this.devices.get(identity);
			if (cachedDevice != null && !cachedDevice.isClosed())
				return cachedDevice;

			OpenALAudioDevice device = new OpenALAudioDeviceImpl(identity);
			this.devices.put(identity, device);
			return device;
		}
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private void scanDefaultDevice() {
		String specifier = alcGetString(NULL, ALC_DEFAULT_DEVICE_SPECIFIER);
		if (specifier == null || specifier.isEmpty()) {
			LOGGER.warn("Cannot get default device specifier");
			this.defaultDeviceIdentity = NamedAudioDeviceIdentity.EMPTY;
			return;
		}

		this.defaultDeviceIdentity = new NamedAudioDeviceIdentity(specifier);
	}

	protected void scanDevices() {
		this.scanDefaultDevice();

		if (!alcIsExtensionPresent(NULL, "ALC_ENUMERATION_EXT")) {
			LOGGER.warn("Cannot enumeration devices: Extension not available");
			this.devicesIdentityList = List.of(this.defaultDeviceIdentity);
			return;
		}

		String devicesStringList = alcGetString(NULL, ALC_ALL_DEVICES_SPECIFIER);
		if (devicesStringList == null || devicesStringList.isEmpty()) {
			LOGGER.warn("Cannot enumeration devices: Device list is empty");
			this.devicesIdentityList = List.of(this.defaultDeviceIdentity);
			return;
		}

		Set<AudioDeviceIdentity> identities = new LinkedHashSet<>();
		identities.add(this.defaultDeviceIdentity);

		for (String specifier : devicesStringList.split("\0")) {
			if (specifier.isEmpty())
				continue;

			identities.add(new NamedAudioDeviceIdentity(specifier));
		}

		this.devicesIdentityList = List.copyOf(identities);

		LOGGER.debug("Found {} devices:\n{}",
			this.devicesIdentityList.size(),
			lazy(() -> this.devicesIdentityList
				.stream()
				.map(AudioDeviceIdentity::toString)
				.map(name -> "    " + name)
				.collect(Collectors.joining("\n"))));
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	@Override
	public AudioListener getListener() {
		return this.listener;
	}

	@NotNull
	@Override
	public AudioPlayer createPlayer(@NotNull Audio audio) {
		return null;
	}

	@NotNull
	@Unmodifiable
	@Override
	public List<AudioPlayer> getAudioPlayers() {
		return List.of();
	}

	@Override
	public float getGlobalVolume() {
		return 0;
	}

	@Override
	public void setGlobalVolume(float volume) {

	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	@Override
	public List<AudioDeviceIdentity> getDevices() {
		return this.devicesIdentityList;
	}

	@Override
	public void setActiveDevice(@NotNull AudioDeviceIdentity device) {
		Objects.requireNonNull(device, "device is null");

		this.checkAvailable();

		this.activeDeviceStatus = DeviceStatus.PENDING;

		if (this.activeDeviceIdentity.equals(device))
			return;

		LOGGER.debug("Set active device {}, old {}", device, this.activeDeviceIdentity);
		this.activeDeviceIdentity = device;
	}

	@NotNull
	@Override
	public AudioDeviceIdentity getActiveDevice() {
		return this.activeDevice.getIdentity();
	}

	@NotNull
	@Override
	public AudioDeviceIdentity getDefaultDevice() {
		return this.defaultDeviceIdentity;
	}

	@Nullable
	@Override
	public AudioDevice getDevice(@NotNull AudioDeviceIdentity identity) throws AudioDeviceException {
		Objects.requireNonNull(identity, "identity is null");

		this.checkAvailable();
		try {
			return this.taskHandler.runBlocking(() -> this.openDevice(identity));
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof AudioDeviceException ex)
				throw ex;

			throw new AudioDeviceOpenException("Failed to get the device", cause);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new AudioDeviceOpenException("Failed to get the device", e);
		}
	}

	@Override
	public void close() {
		if (this.closed)
			return;
		this.closed = true;

		try {
			this.audioManager.close();

			alcMakeContextCurrent(NULL);

			this.listener.close();

			if (this.activeContext != null)
				this.activeContext.close();
			this.activeContext = null;

			this.activeDevice.close();
			this.activeDevice = null;

			for (AudioDevice device : this.devices.values())
				device.close();
			this.devices.clear();
		} finally {
			super.close();
		}
	}

	private enum DeviceStatus {
		PENDING,
		OPENED,
		ERROR
	}
}
