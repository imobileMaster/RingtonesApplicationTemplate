package ringtones.codebhak.direct;


public class SongInfo {

	private int audioResource;
	private String name;
	private int imageResource;
	private boolean isFavorite;
	private boolean isPlaying;
	private String fileName;

	public SongInfo() {
		this.setAudioResource(0);
		this.setName("");
		this.setImageResource(0);
		this.setFavorite(false);
		this.setPlaying(false);
		this.setFileName("");
	}

	public SongInfo(int audioResource, String name, int imageResource, boolean isFavorite,
			boolean isPlaying, String fileName) {
		this.setAudioResource(audioResource);
		this.setName(name);
		this.setImageResource(imageResource);
		this.setFavorite(isFavorite);
		this.setPlaying(isPlaying);
		this.setFileName(fileName);
	}

	public SongInfo(SongInfo songInfo) {
		this.setAudioResource(songInfo.getAudioResource());
		this.setName(songInfo.getName());
		this.setImageResource(songInfo.getImageResource());
		this.setFavorite(songInfo.isFavorite());
		this.setPlaying(songInfo.isPlaying());
		this.setFileName(songInfo.getFileName());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getAudioResource() {
		return audioResource;
	}

	public void setAudioResource(int audioResource) {
		this.audioResource = audioResource;
	}

	public int getImageResource() {
		return imageResource;
	}

	public void setImageResource(int imageResource) {
		this.imageResource = imageResource;
	}

}
