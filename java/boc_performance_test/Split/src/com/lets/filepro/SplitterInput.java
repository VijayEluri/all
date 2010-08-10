package com.lets.filepro;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;

public class SplitterInput implements Runnable {
	public final static int BUFFER_SIZE = 1 * 1024 * 1024; // 1M
	public final static int MAX_QUEUE_COUNT = 400;

	public final static byte LINE_SPLITTER = '\n';
	public final static byte PROPERTY_SPLITTER = '|';

	public final File file;
	public final long startPosition;
	public final long endPosition;

	private byte[] buffer;
	private int bufferLength = 0;
	private int bufferPosition = 0;
	private int lineStart;
	private FileInputStream inputStream;
	private long position;

	private final LinkedList queue;
	private SplitterObject content = SplitterObject.getObject();

	public SplitterInput(LinkedList queue, File file, long startPosition,
			long endPosition) {
		this.queue = queue;
		if (startPosition < 0)
			startPosition = 0;
		this.file = file;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}

	public SplitterInput(LinkedList queue, File file) {
		this(queue, file, 0, file.length());
	}

	public void output() throws InterruptedException {
		content.file = file;
		synchronized (queue) {
			while (queue.size() >= MAX_QUEUE_COUNT) {
				queue.notifyAll();
				queue.wait();				
			}
			queue.addLast(content);
			content = null;
			queue.notifyAll();
		}
		content = SplitterObject.getObject();
	}

	private boolean fetchBuffer() throws IOException {
		if (inputStream == null)
			return false;
		int len = bufferLength - bufferPosition;
		System.arraycopy(buffer, bufferPosition, buffer, 0, len);
		bufferLength = len;
		bufferPosition = 0;
		int p = bufferLength;
		do {
			int c = inputStream.read(buffer, bufferLength, buffer.length
					- bufferLength);
			if (c == -1) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
				inputStream = null;
				break;
			}
			if (c > 0)
				bufferLength += c;
		} while (bufferLength < buffer.length);
		return bufferLength > p;
	}

	private int scanNewLine() {
		for (int i = bufferPosition; i < bufferLength; i++)
			if (buffer[i] == LINE_SPLITTER)
				return i + 1;
		return -1;
	}

	private boolean nextLine() throws IOException {
		lineStart = bufferPosition;
		if (bufferLength > bufferPosition) {
			int i = scanNewLine();
			if (i != -1) {
				bufferPosition = i;
				return true;
			}
		}
		if (fetchBuffer()) {
			lineStart = bufferPosition;
			int i;
			i = scanNewLine();
			if (i == -1)
				bufferPosition = bufferLength;
			else
				bufferPosition = i;
		}
		return lineStart != bufferLength;
	}

	private void initLine() throws IOException {
		fetchBuffer();
		bufferPosition = scanNewLine();
	}

	private void skipToStartPosition() throws IOException {
		long p = startPosition;
		while (p > 0) {
			long c = inputStream.skip(p);
			if (c == -1) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
				inputStream = null;
				break;
			}
			if (c > 0)
				p -= c;
		}
		position = startPosition;
	}

	private int scanProperty(int start) {
		for (int i = start; i < bufferPosition; i++)
			if (buffer[i] == PROPERTY_SPLITTER)
				return i;
		return -1;
	}

	private boolean isPropertyLine() {
		return scanProperty(lineStart) != -1;
	}

	private void parseProperties() {
		int start = lineStart;
		int pos;
		int pc = 0;
		while ((pos = scanProperty(start)) != -1) {
			int len = pos - start;
			System.arraycopy(buffer, start, content.properties[pc], 0, len);
			content.properties[pc][len] = 0;
			pc++;
			start = pos + 1;
		}
		content.propertyCount = pc;
	}

	private void skipLine() {
		int len = bufferPosition - lineStart;
		position += len;
	}

	private void outputLine() {
		int len = bufferPosition - lineStart;
		System.arraycopy(buffer, lineStart, content.content,
				content.contentLength, len);
		content.contentLength += len;
		position += len;
	}

	public void run() {
		if (endPosition <= startPosition)
			return;
		buffer = new byte[BUFFER_SIZE];
		inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			position = 0;
			if (startPosition == 0)
				initLine();
			else {
				skipToStartPosition();
				initLine();
				skipLine();
				nextLine();
			}
			long propertyPosition = 0;
			do {
				if (isPropertyLine()) {
					propertyPosition = position;
					break;
				}
				skipLine();
				if (!nextLine())
					return;
			} while (true);
			parseProperties();
			while (propertyPosition <= endPosition) {
				outputLine();
				if (!nextLine()) {
					output();
					break;
				}
				if (isPropertyLine()) {
					output();
					propertyPosition = position;
					parseProperties();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
				inputStream = null;
			}
		}
	}
}
