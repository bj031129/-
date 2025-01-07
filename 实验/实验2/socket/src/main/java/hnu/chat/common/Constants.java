package hnu.chat.common;

public class Constants {
    public static final int SERVER_PORT = 5566;
    public static final int MAX_IMAGE_SIZE = 2 * 1024 * 1024;  // 2MB
    public static final int MAX_BASE64_SIZE = 1024 * 1024;     // 1MB
    public static final String IMG_START_TAG = "[IMG]";
    public static final String IMG_END_TAG = "[/IMG]";
    public static final int MAX_FILE_SIZE = 10 * 1024 * 1024;  // 10MB
    public static final String FILE_START_TAG = "[FILE]";
    public static final String FILE_END_TAG = "[/FILE]";
    public static final String NAME_START_TAG = "[NAME]";
    public static final String NAME_END_TAG = "[/NAME]";
    
    // GUI常量
    public static final int WINDOW_WIDTH = 400;
    public static final int WINDOW_HEIGHT = 400;
    public static final int DISPLAY_IMAGE_WIDTH = 100;
    public static final int DISPLAY_IMAGE_HEIGHT = 100;
    public static final int COMPRESS_IMAGE_WIDTH = 800;
    public static final int COMPRESS_IMAGE_HEIGHT = 600;
    
    public static final String AUDIO_START_TAG = "[AUDIO]";
    public static final String AUDIO_END_TAG = "[/AUDIO]";
    public static final String VIDEO_START_TAG = "[VIDEO]";
    public static final String VIDEO_END_TAG = "[/VIDEO]";
    
    public static final int MAX_MEDIA_SIZE = 50 * 1024 * 1024;  // 50MB
    
    // 支持的媒体格式
    public static final String[] SUPPORTED_AUDIO_FORMATS = {".mp3", ".wav", ".aac", ".m4a"};
    public static final String[] SUPPORTED_VIDEO_FORMATS = {".mp4", ".avi", ".mkv", ".mov"};
} 