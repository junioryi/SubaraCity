import java.io.File;
import java.net.URI;
import java.net.URL;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

/**
 * 音訊播放器，支援WAV、AIFF、AU格式
 *
 * @author magiclen
 */
public class AudioPlayer {

    /**
     * 音訊播放器的狀態
     */
    public static enum Status {

	OPEN, START, STOP, CLOSE;
    }

    public static interface StatusChangedListener {

	public void statusChanged(Status status);
    }

    //-----物件變數-----
    private AudioInputStream audioInputStream;
    private AudioFormat audioFormat;
    private DataLine.Info dataLineInfo;
    private Clip clip;
    private int playCount = 1, playCountBuffer = 1;
    private int volume = 50, balance = 0;
    private Status status = null;
    private boolean autoClose = false;
    private StatusChangedListener statusListener;

    //-----建構子-----
    /**
     * 建構子，傳入檔案
     *
     * @param file 傳入聲音檔案
     */
    public AudioPlayer(final File file) {
	try {
	    final URL url = file.toURI().toURL();
	    init(url);
	} catch (final Exception ex) {
	    throw new RuntimeException(ex.getMessage());
	}

    }

    /**
     * 建構子，傳入URL
     *
     * @param url 傳入聲音URL
     */
    public AudioPlayer(final URL url) {
	try {
	    init(url);
	} catch (final Exception ex) {
	    throw new RuntimeException(ex.getMessage());
	}
    }

    /**
     * 建構子，傳入URL String
     *
     * @param str 傳入聲音URL String
     */
    public AudioPlayer(final String str) {
	try {
	    final URL url = URI.create(str).toURL();
	    init(url);
	} catch (final Exception ex) {
	    throw new RuntimeException(ex.getMessage());
	}
    }

    //-----初始化-----
    /**
     * 初始化AudioPlayer
     *
     * @param url 傳入聲音URL
     * @throws Exception 拋出例外
     */
    private void init(final URL url) throws Exception {
	//讀取音樂輸入串流
	try {
	    audioInputStream = AudioSystem.getAudioInputStream(url);
	} catch (final Exception ex) {
	    throw new RuntimeException(ex.getMessage());
	}
	//進行播放設定
	audioFormat = audioInputStream.getFormat();
	int bufferSize = (int) Math.min(audioInputStream.getFrameLength() * audioFormat.getFrameSize(), Integer.MAX_VALUE); //緩衝大小，如果音訊檔案不大，可以全部存入緩衝空間。這個數值應該要按照用途來決定
	dataLineInfo = new DataLine.Info(Clip.class, audioFormat, bufferSize);
	clip = (Clip) AudioSystem.getLine(dataLineInfo);
	clip.addLineListener(e -> {
	    LineEvent.Type type = e.getType();
	    if (type.equals(LineEvent.Type.START)) {
		status = Status.START;
	    } else if (type.equals(LineEvent.Type.STOP)) {
		status = Status.STOP;
		if (clip.getFramePosition() == clip.getFrameLength()) {
		    clip.setFramePosition(0);
		}
		if (playCount == 0 || (playCount > 0 && playCountBuffer < playCount)) {
		    playCountBuffer++;
		    clip.start();
		} else {
		    playCountBuffer = 1;
		    if (autoClose) {
			clip.close();
		    }
		}
	    } else if (type.equals(LineEvent.Type.OPEN)) {
		status = Status.OPEN;
	    } else if (type.equals(LineEvent.Type.CLOSE)) {
		status = Status.CLOSE;
	    } else {
		return;
	    }
	    if (statusListener != null) {
		statusListener.statusChanged(status);
	    }
	});
	clip.open(audioInputStream);
	setVolume();
	setBalance();
    }

    /**
     * 開始播放音訊，可以回復暫停時的狀態
     */
    public void play() {
	clip.start();
    }

    /**
     * 暫停播放音訊
     */
    public void pause() {
	clip.stop();
    }

    /**
     * 停止播放音訊，下次播放將會重頭開始
     */
    public void stop() {
	clip.stop();
	clip.setFramePosition(0);
    }

    /**
     * 設定播放次數，0為無限次播放
     *
     * @param playCount 傳入播放次數
     */
    public void setPlayCount(final int playCount) {
	if (playCount < 0) {
	    throw new RuntimeException("PlayCount must be at least 0!");
	}
	this.playCount = playCount;
    }

    /**
     * 設定靜音
     */
    public void mute() {
	setVolume(0);
    }

    /**
     * 是否靜音
     *
     * @return 傳回是否靜音
     */
    public boolean isMute() {
	return volume == 0;
    }

    /**
     * 設定音量，範圍是0~100，數值愈大愈大聲
     *
     * @param volume 傳入音量
     */
    public void setVolume(final int volume) {
	if (volume < 0 || volume > 100) {
	    throw new RuntimeException("Volumn must be at least 0 and at most 100!");
	}
	this.volume = volume;
	setVolume();
    }

    /**
     * 取得音量
     *
     * @return 傳回音量
     */
    public int getVolume() {
	return volume;
    }

    /**
     * 設定音量
     */
    private void setVolume() {
	final FloatControl floatControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	final float db = (float) (Math.log10(volume * 0.039) * 10);
	floatControl.setValue(db);
    }

    /**
     * 取得目前音訊播放器的狀態
     *
     * @return 傳回狀態
     */
    public Status getStatus() {
	return status;
    }

    /**
     * 只開啟右聲道
     */
    public void onlyRight() {
	setBalance(100);
    }

    /**
     * 是否只開啟右聲道
     *
     * @return 傳回是否只開啟右聲道
     */
    public boolean isOnlyRight() {
	return balance == 100;
    }

    /**
     * 只開啟左聲道
     */
    public void onlyLeft() {
	setBalance(-100);
    }

    /**
     * 是否只開啟左聲道
     *
     * @return 傳回是否只開啟左聲道
     */
    public boolean isOnlyLeft() {
	return balance == -100;
    }

    /**
     * 設定聲道音量為平衡狀態
     */
    public void balance() {
	setBalance(0);
    }

    /**
     * 聲道音量是否為平衡狀態
     *
     * @return 傳回聲道音量是否為平衡狀態
     */
    public boolean isBalance() {
	return balance == 0;
    }

    /**
     * 設定聲道音量的平衡，範圍-100~100，數值愈大愈靠近右邊，0為平衡狀態
     *
     * @param balance 傳入聲道音量的平衡值
     */
    public void setBalance(final int balance) {
	if (volume < 0 || volume > 100) {
	    throw new RuntimeException("Balance must be at least -100 and at most 100!");
	}
	this.balance = balance;
	setBalance();
    }

    /**
     * 取得聲道音量的平衡值
     *
     * @return 傳回聲道音量的平衡值
     */
    public int getBalance() {
	return balance;
    }

    /**
     * 設定聲道音量的平衡值
     */
    private void setBalance() {
	try {
	    final FloatControl floatControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);
	    final float pan = balance / 100.0f;
	    floatControl.setValue(pan);
	} catch (final Exception ex) {
	    //可能是單聲道音訊檔造成的例外
	}
    }

    /**
     * 取得音訊的長度(微秒)
     *
     * @return 傳回音訊的長度
     */
    public long getAudioLength() {
	return clip.getMicrosecondLength();
    }

    /**
     * 取得音訊目前的位置(微秒)
     *
     * @return 傳回音訊目前的位置
     */
    public long getAudioPosition() {
	return clip.getMicrosecondPosition();
    }

    /**
     * 設定音訊的位置(微秒)
     *
     * @param position 傳入音訊的位置
     *
     */
    public void setAudioPosition(long position) {
	clip.setMicrosecondPosition(position);
    }

    /**
     * 關閉音訊
     */
    public void close() {
	clip.close();
    }

    /**
     * 設定播放結束後是否自動關閉
     *
     * @param autoClose 傳入播放結束後是否自動關閉
     */
    public void setAutoClose(final boolean autoClose) {
	this.autoClose = autoClose;
    }

    /**
     * 取得播放結束後是否自動關閉
     *
     * @return 傳回播放結束後是否自動關閉
     */
    public boolean isAutoClose() {
	return autoClose;
    }

    /**
     * 設定狀態改變後的監聽事件
     *
     * @param listener 傳入狀態改變的監聽事件
     */
    public void setStatusChangedListener(StatusChangedListener listener) {
	this.statusListener = listener;
    }

    /**
     * 取得狀態改變後的監聽事件
     *
     * @return 傳回狀態改變後的監聽事件
     */
    public StatusChangedListener getStatusChangedListener() {
	return statusListener;
    }
}
