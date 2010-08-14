package net.everythingandroid.smspopup.filter;

import java.text.DateFormat;
import java.util.Date;

public class FilteredMessage
{
	private String number;
	private long received;
	private String body;
	private static DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

	public String getNumber()
	{
		return number;
	}

	public void setNumber(String number)
	{
		this.number = number;
	}

	public long getReceived()
	{
		return received;
	}
	
	public String getFormatedReceived()
	{
		return dateFormat.format(new Date(received));
	}

	public void setReceived(String received)
	{
		this.received = Long.parseLong(received);
	}

	public String getBody()
	{
		return body;
	}

	public void setBody(String body)
	{
		this.body = body;
	}
}
