package callgraph_tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLSocketExampleA {

	SSLSocketFactory ssf;
	SSLSocket socket;

	public SSLSocketExampleA() {
		this.ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
		try {
			this.socket = (SSLSocket) ssf.createSocket("gmail.com", 443);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startSession() throws SSLHandshakeException, SSLPeerUnverifiedException {
		HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
		SSLSession s = socket.getSession();

		// Verify that the certicate hostname is for mail.google.com
		// This is due to lack of SNI support in the current SSLSocket.
		// This previously worked, but it seemed that they now
		// issue a certificate with gmail.com. Example is intended
		// for verification purposes.
		if (!hv.verify("mail.google.com", s)) {
			throw new SSLHandshakeException("Expected mail.google.com, found " + s.getPeerPrincipal());
		}
	}

	public void read() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String x = in.readLine();
		System.out.println(x);
		in.close();
	}

	public void close() throws IOException {
		socket.close();
	}

	public static void main(String args[]) throws Exception {
		SSLSocketExampleA main = new SSLSocketExampleA();

		main.startSession();
		main.read();
		main.close();
	}

}