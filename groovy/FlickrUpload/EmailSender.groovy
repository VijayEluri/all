abstract class EmailSender {
  protected def TO = "let39return@photos.flickr.com";
	public abstract void send(String subject, File attachment) throws Exception;
}