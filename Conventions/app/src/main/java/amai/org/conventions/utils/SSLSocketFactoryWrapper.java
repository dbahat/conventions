package amai.org.conventions.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLSocketFactoryWrapper extends SSLSocketFactory {
	private SSLSocketFactory factory;
	private String[] protocols;

	public SSLSocketFactoryWrapper(SSLSocketFactory factory) {
		this.factory = factory;
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return factory.getDefaultCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return factory.getSupportedCipherSuites();
	}

	@Override
	public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
		Socket socket = factory.createSocket(s, host, port, autoClose);
		setProtocols(socket);
		return socket;
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		Socket socket = factory.createSocket(host, port);
		setProtocols(socket);
		return socket;
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
		Socket socket = factory.createSocket(host, port, localHost, localPort);
		setProtocols(socket);
		return socket;
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		Socket socket = factory.createSocket(host, port);
		setProtocols(socket);
		return socket;
	}

	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
		Socket socket = factory.createSocket(address, port, localAddress, localPort);
		setProtocols(socket);
		return socket;
	}

	public void setEnabledProtocols(String[] protocols) {
		this.protocols = protocols;
	}

	private void setProtocols(Socket socket) {
		if (socket instanceof SSLSocket && protocols != null) {
			((SSLSocket) socket).setEnabledProtocols(protocols);
		}
	}
}
