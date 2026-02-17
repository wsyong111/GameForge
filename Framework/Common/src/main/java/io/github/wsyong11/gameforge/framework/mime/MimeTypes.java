package io.github.wsyong11.gameforge.framework.mime;

public interface MimeTypes {
	interface Application {
		MimeType OCTET_STREAM = MimeType.application("octet-stream");
		MimeType ZIP = MimeType.application("zip");
		MimeType JSON = MimeType.application("json");
		MimeType XML = MimeType.application("xml");
		MimeType JAVASCRIPT = MimeType.application("javascript");
	}

	interface Text {
		MimeType PLAIN = MimeType.text("plain");
		MimeType HTML = MimeType.text("html");
		MimeType CSS = MimeType.text("css");
		MimeType CSV = MimeType.text("csv");
		MimeType MARKDOWN = MimeType.text("markdown");
		MimeType JAVASCRIPT = MimeType.text("javascript");
		MimeType XML = MimeType.text("xml");
	}

	interface Audio {
		MimeType MPEG = MimeType.audio("mpeg");
		MimeType WAV = MimeType.audio("wav");
		MimeType OGG = MimeType.audio("ogg");
		MimeType AAC = MimeType.audio("aac");
	}

	interface Video {
		MimeType MP4 = MimeType.video("mp4");
		MimeType MPEG = MimeType.video("mpeg");
		MimeType OGG = MimeType.video("ogg");
		MimeType QUICKTIME = MimeType.video("quicktime");
		MimeType WEBM = MimeType.video("webm");
	}
}
