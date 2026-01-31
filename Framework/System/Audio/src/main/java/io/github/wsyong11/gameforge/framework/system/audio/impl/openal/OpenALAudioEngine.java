package io.github.wsyong11.gameforge.framework.system.audio.impl.openal;

import io.github.wsyong11.gameforge.framework.system.audio.*;
import io.github.wsyong11.gameforge.framework.system.audio.ex.AudioDeviceException;
import io.github.wsyong11.gameforge.framework.system.audio.impl.simple.EmptyAudioDevice;
import io.github.wsyong11.gameforge.framework.system.audio.impl.simple.NamedAudioDeviceIdentity;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.joml.Vector3fc;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;
import static org.lwjgl.openal.AL11.AL_POSITION;
import static org.lwjgl.openal.AL11.alListener3f;
import static org.lwjgl.openal.ALC11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenALAudioEngine implements AudioEngine {
	private static final Logger LOGGER = Log.getLogger();

	private AudioDeviceIdentity defaultDeviceIdentity;
	private List<AudioDeviceIdentity> devicesIdentityList;

	private AudioDevice currentDevice;
	@Nullable
	private OpenALContext currentContext;
	private boolean contextChanged;

	private final Map<AudioDeviceIdentity, OpenALAudioDevice> devices;

	private final OpenALListener listener;

	private boolean inited;
	private boolean closed;

	public OpenALAudioEngine() {
		this.defaultDeviceIdentity = NamedAudioDeviceIdentity.EMPTY;

		this.devicesIdentityList = List.of();

		this.currentDevice = new EmptyAudioDevice();
		this.currentContext = null;
		this.contextChanged = false;

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

		try {
			this.setOutputDevice(this.defaultDeviceIdentity);
		} catch (AudioDeviceException e) {
			this.setOutputDevice(NamedAudioDeviceIdentity.EMPTY);
			LOGGER.warn("Cannot set the output device to {}, Set the empty device as placeholder", this.defaultDeviceIdentity, e);
		}
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
		return 20;
	}

	@Override
	public void loopTick() {
		if (this.currentContext == null) {
			if (this.contextChanged) {
				alcMakeContextCurrent(NULL);
				this.contextChanged = false;
			}
			return;
		}

		if (this.contextChanged) {
			this.currentContext.use();
			this.contextChanged = false;
		}

		this.updateListener();
	}

	protected void updateListener() {
		Vector3fc position = this.listener.getPosition();
		alListener3f(AL_POSITION, position.x(), position.y(), position.z());

		// TODO: 2026/2/1 listener
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	protected OpenALAudioDevice openDevice(@NotNull AudioDeviceIdentity identity) throws AudioDeviceException {
		Objects.requireNonNull(identity, "identity is null");

		this.checkAvailable();

		if (identity.isEmpty())
			throw new IllegalArgumentException("Device identity " + identity + " is empty");

		synchronized (this.devices) {
			OpenALAudioDevice cachedDevice = this.devices.get(identity);
			if (cachedDevice != null && !cachedDevice.isClosed())
				return cachedDevice;

			OpenALAudioDevice device = new OpenALAudioDevice(identity);
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
		return null;
	}

	@NotNull
	@Unmodifiable
	@Override
	public List<AudioPlayer> getAudioPlayers() {
		return List.of();
	}

	@Override
	public @NotNull AudioPlayer createPlayer(@NotNull Audio audio) {
		return null;
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
	public void setOutputDevice(@NotNull AudioDeviceIdentity device) throws AudioDeviceException {
		Objects.requireNonNull(device, "device is null");

		if (this.currentDevice.getIdentity().equals(device))
			return;

		LOGGER.debug("Current output device: {}", device);

		if (this.currentContext != null)
			this.currentContext.close();
		this.currentContext = null;

		if (device.isEmpty()) {
			this.currentDevice = new EmptyAudioDevice();
			return;
		}

		OpenALAudioDevice audioDevice = this.openDevice(device);
		this.currentDevice = audioDevice;
		this.currentContext = audioDevice.createContext();
		this.contextChanged = true;
	}

	@NotNull
	@Override
	public AudioDeviceIdentity getOutputDevice() {
		return this.currentDevice.getIdentity();
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
		return this.openDevice(identity);
	}

	@Override
	public void close() {
		if (this.closed)
			return;
		this.closed = true;

		alcMakeContextCurrent(NULL);

		if (this.currentContext != null)
			this.currentContext.close();
		this.currentContext = null;

		this.currentDevice.close();
		this.currentDevice = null;

		for (AudioDevice device : this.devices.values())
			device.close();
		this.devices.clear();
	}
}
