import java.io.File;
import java.net.URL;

import com.aetrion.flickr.*;
import com.aetrion.flickr.auth.*;
import com.aetrion.flickr.people.User;
import com.aetrion.flickr.util.AuthStore;
import com.aetrion.flickr.util.FileAuthStore;

class FlickrFactory
{
	private File keyStore;
	private AuthStore authStore;
	private RequestContext requestContext = RequestContext.getRequestContext();

	private Flickr flickr = new Flickr("b52a50857746cbc692436a4dac97de52",
			"9d42cc61354218e3", new REST());
	private static FlickrFactory _this = null;

	private void authorize() throws Exception
	{
		AuthInterface authIf = flickr.getAuthInterface();
		String frob = authIf.getFrob();

		URL url = authIf.buildAuthenticationUrl(Permission.DELETE, frob);
		System.out.println(url.toExternalForm());
		System.out.println("Press Enter after you accessed the above URL");
		System.in.read();

		Auth token = authIf.getToken(frob);
		requestContext.setAuth(token);
		authStore.store(token);
	}

	FlickrFactory() throws Exception
	{
		keyStore = new File(System.getProperty("user.home")
				+ File.separatorChar + ".flickrAuth");
		authStore = new FileAuthStore(keyStore);
		Auth[] auths = authStore.retrieveAll();
		if (auths.length > 0) {
			requestContext.setAuth(auths[0]);
		} else {
			authorize();
		}
	}

	public static FlickrFactory getInstance() throws Exception
	{
		if (_this == null) {
			_this = new FlickrFactory();
		}
		return _this;
	}

	public Flickr getFlickr()
	{
		return flickr;
	}

	public User getUser()
	{
		return requestContext.getAuth().getUser();
	}

}
